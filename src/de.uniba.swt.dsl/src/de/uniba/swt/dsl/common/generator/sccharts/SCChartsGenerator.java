package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnException;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SCChartsGenerator {

    private static final Logger logger = Logger.getLogger(SCChartsGenerator.class);

    @Inject SCChartsTextualBuilder builder;

    @Inject
    StateTable stateTable;

    @Inject
    SCChartsNormalizer normalizer;

    private RootModule rootModule;
    private Map<FuncDecl, RootState> mapFuncState;

    public String generate(RootModule rootModule) {
        this.rootModule = normalizer.normalizeModule(rootModule);
        SCCharts models = buildSCChartsModels();
        logger.debug(models);
        return builder.buildString(models);
    }

    private SCCharts buildSCChartsModels() {
        // create models
        mapFuncState = rootModule
                .getProperties()
                .stream()
                .filter(p -> p instanceof FuncDecl).map(p -> (FuncDecl)p)
                .collect(Collectors.toMap(f -> f, this::buildModel));

        // update model
        List<RootState> states = new ArrayList<>();
        mapFuncState.forEach((funcDecl, rootState) -> {
            updateModel(funcDecl, rootState);
            states.add(rootState);
        });

        return new SCCharts(states);
    }

    /**
     * temporary allocate param array size to 1024
     * TODO should guess the size of param based on function call
     */
    private int DEFAULT_PARAM_ARRAY_SIZE = 1024;

    private RootState buildModel(FuncDecl funcDecl) {
        RootState superState = new RootState();
        superState.setId(funcDecl.getName());

        // input
        if (funcDecl.getParamDecls() != null) {
            for (RefVarDecl paramDecl : funcDecl.getParamDecls()) {
                superState.getDeclarations().add(convertDeclaration(paramDecl.getType(), paramDecl.getName(), paramDecl.isArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, true, false));
            }
        }

        // output
        if (funcDecl.isReturn()) {
            superState.getDeclarations().add(convertDeclaration(funcDecl.getReturnType(), SCChartsGenUtil.VAR_OUTPUT_NAME, funcDecl.isReturnArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, false, true));
        }

        // local variables
        for (Statement stmt : funcDecl.getStmtList().getStmts()) {
            if (stmt instanceof VarDeclStmt) {
                VarDeclStmt varDeclStmt = (VarDeclStmt)stmt;
                VarDecl varDecl = varDeclStmt.getDecl();

                // array cardinality must be a int literal
                int arraySize = 0;
                if (varDecl.isArray()) {
                    if (varDecl.getCardinality() instanceof NumberLiteral) {
                        arraySize = (int) ((NumberLiteral) varDecl.getCardinality()).getValue();
                    } else {
                        throw new BahnException("Array cardinality must be a literal integer");
                    }
                }

                superState.getDeclarations().add(convertDeclaration(varDecl.getType(), varDecl.getName(), arraySize, false, false));
            }
        }

        // all variables
        logger.debug("state declarations: " + LogHelper.printObject(superState.getDeclarations()));

        return superState;
    }

    private void updateModel(FuncDecl funcDecl, RootState model) {
        var states = createStates(model.getDeclarations(), funcDecl.getStmtList());
        model.getStates().addAll(states);

        // mark first and last
        if (!model.getStates().isEmpty()) {
            model.getStates().get(0).setInitial(true);
            model.getStates().get(model.getStates().size() - 1).setFinal(true);
        }
    }

    private List<State> createStates(List<SVarDeclaration> declarations, StatementList statementList) {
        // generate states based on statement list
        State currentState = null;
        State nextState = null;
        List<State> states = new ArrayList<>();
        for (Statement stmt : statementList.getStmts()) {
            // last state
            currentState = Objects.requireNonNullElseGet(nextState, () -> new State(stateTable.nextStateId()));

            // next state is always new
            nextState = new State(stateTable.nextStateId());

            // update current state
            states.add(currentState);
            updateState(states, declarations, currentState, nextState, stmt);
        }

        // add last state
        if (nextState != null) {
            states.add(nextState);
        }
        return states;
    }

    /**
     * SelectionStmt | IterationStmt | VarDeclStmt | AssignmentStmt | FunctionCallStmt | ReturnStmt
     * @param states
     * @param declarations
     * @param state
     * @param nextState
     * @param stmt
     */
    private void updateState(List<State> states, List<SVarDeclaration> declarations, State state, State nextState, Statement stmt) {

        // function call -> reference
        updateStateReferenceCallIfNeeded(state, stmt);

        // if..else
        if (stmt instanceof SelectionStmt) {
            SelectionStmt selectionStmt = (SelectionStmt)stmt;
            var condition = selectionStmt.getExpr();
            states.addAll(generateBranchingStates(declarations, state, nextState, selectionStmt.getThenStmts(), condition));

            // else branch
            if (selectionStmt.getElseStmts() != null) {
                states.addAll(generateBranchingStates(declarations, state, nextState, selectionStmt.getElseStmts(), null));
            } else {
                state.getOutgoingTransitions().add(new Transition(nextState));
            }
            return;
        }

        // transition
        var transition = new Transition(nextState);
        state.getOutgoingTransitions().add(transition);
        if (state.getReferenceState() == null) {
            var effect = generateEffect(declarations, stmt);
            if (effect != null)
                transition.setEffects(List.of(effect));
        }
    }

    private List<State> generateBranchingStates(List<SVarDeclaration> declarations, State state, State nextState, StatementList stateList, Expression condition) {
        List<State> states = createStates(declarations, stateList);
        if (states.size() > 0) {
            var tran = new Transition(states.get(0));
            if (condition != null)
                tran.setTrigger(condition);
            state.getOutgoingTransitions().add(tran);

            // go to nex state
            states.get(states.size() - 1).getOutgoingTransitions().add(new Transition(nextState));
        }
        return states;
    }

    private void updateStateReferenceCallIfNeeded(State state, Statement stmt) {
        if (stmt instanceof VarDeclStmt) {
            VarDeclStmt varDeclStmt = (VarDeclStmt)stmt;
            if (varDeclStmt.getAssignment() != null) {
                if (findRefState(state, varDeclStmt.getAssignment().getExpr())) {
                    // update return
                    state.getReferenceBindingExprs().add(createValuedReferenceExpr(varDeclStmt.getDecl()));
                    return;
                }
            }
        }

        if (stmt instanceof AssignmentStmt) {
            AssignmentStmt assignmentStmt = (AssignmentStmt) stmt;
            if (findRefState(state, assignmentStmt.getAssignment().getExpr())) {
                // update return
                state.getReferenceBindingExprs().add(assignmentStmt.getReferenceExpr());
                return;
            }
        }

        if (stmt instanceof FunctionCallStmt) {
            FunctionCallStmt functionCallStmt = (FunctionCallStmt) stmt;
            if (findRefState(state, functionCallStmt.getExpr())) {
                return;
            }
        }

        //TODO support return stmt, if stmt
    }

    private ValuedReferenceExpr createValuedReferenceExpr(VarDecl decl) {
        var expr = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        expr.setDecl(decl);
        return expr;
    }

    private boolean findRefState(State state, Expression expr) {
        if (expr instanceof FunctionCallExpr) {
            FunctionCallExpr functionCallExpr = (FunctionCallExpr)expr;
            var refState = mapFuncState.get(functionCallExpr.getDecl());
            if (refState != null) {
                state.setReferenceState(refState);
                state.setReferenceBindingExprs(functionCallExpr.getParams());
                return true;
            }
        }

        return false;
    }

    private Effect generateEffect(List<SVarDeclaration> declarations, Statement stmt) {
        if (stmt instanceof VarDeclStmt) {

            if (((VarDeclStmt) stmt).getAssignment() == null)
                return null;

            AssignmentEffect effect = new AssignmentEffect();
            effect.setExpression(((VarDeclStmt) stmt).getAssignment().getExpr());
            effect.setVarDeclaration(findVarDecl(declarations, ((VarDeclStmt) stmt).getDecl().getName()));
            return effect;
        }

        if (stmt instanceof AssignmentStmt) {
            AssignmentEffect effect = new AssignmentEffect();
            effect.setExpression(((AssignmentStmt) stmt).getAssignment().getExpr());
            effect.setVarDeclaration(findVarDecl(declarations, ((AssignmentStmt) stmt).getReferenceExpr().getDecl().getName()));
            return effect;
        }

        if (stmt instanceof FunctionCallStmt) {
            Effect effect = new Effect();
            effect.setExpression(((FunctionCallStmt) stmt).getExpr());
            return effect;
        }

        if (stmt instanceof ReturnStmt) {
            AssignmentEffect effect = new AssignmentEffect();
            effect.setExpression(((ReturnStmt) stmt).getExpr());
            effect.setVarDeclaration(findVarDecl(declarations, SCChartsGenUtil.VAR_OUTPUT_NAME));
            return effect;
        }

        throw new RuntimeException("Statement is not supported");
    }

    private SVarDeclaration findVarDecl(List<SVarDeclaration> declarations, String name) {
        return declarations.stream()
                .filter(d -> d.getName().equals(name))
                .findAny()
                .orElseThrow();
    }

    /**
     * Convert from Bahn decl to SCChart interface decl
     * Initial value is not supported, generated as a separated transaction
     * @param type
     * @param name
     * @param arrayCardinality
     * @param isInput
     * @param isOutput
     * @return
     */
    private SVarDeclaration convertDeclaration(DataType type, String name, int arrayCardinality, boolean isInput, boolean isOutput) {
        var result = new SVarDeclaration();
        result.setDataType(convertDataType(type));
        result.setName(name);
        result.setInput(isInput);
        result.setOutput(isOutput);
        result.setCardinality(arrayCardinality);
        return result;
    }

    private SDataType convertDataType(DataType type) {
        switch (type) {
            case INT_TYPE:
                return SDataType.INT;
            case FLOAT_TYPE:
                return SDataType.FLOAT;
            case BOOLEAN_TYPE:
                return SDataType.BOOL;
            case STRING_TYPE:
                return SDataType.STRING;
        }

        throw new RuntimeException("Type is not supported: " + type);
    }
}

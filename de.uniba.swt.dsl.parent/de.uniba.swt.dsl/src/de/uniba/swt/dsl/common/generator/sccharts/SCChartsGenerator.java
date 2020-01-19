package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private RootState buildModel(FuncDecl funcDecl) {
        RootState superState = new RootState();
        superState.setId(funcDecl.getName());

        // input
        if (funcDecl.getParamDecls() != null) {
            for (RefVarDecl paramDecl : funcDecl.getParamDecls()) {
                superState.getDeclarations().add(convertDeclaration(paramDecl.getType(), paramDecl.getName(), paramDecl.isArray(), true, false));
            }
        }

        // output
        if (funcDecl.isReturn()) {
            superState.getDeclarations().add(convertDeclaration(funcDecl.getReturnType(), SCChartsGenUtil.VAR_OUTPUT_NAME, funcDecl.isReturnArray(), false, true));
        }

        // local variables
        for (Statement stmt : funcDecl.getStmtList().getStmts()) {
            if (stmt instanceof VarDeclStmt) {
                VarDeclStmt varDeclStmt = (VarDeclStmt)stmt;
                VarDecl varDecl = varDeclStmt.getDecl();
                var sDecl = convertDeclaration(varDecl.getType(), varDecl.getName(), varDecl.isArray(), false, false);
                superState.getDeclarations().add(sDecl);
            }
        }

        // all variables
        logger.info("state declarations: " + LogHelper.printObject(superState.getDeclarations()));

        return superState;
    }

    private void updateModel(FuncDecl funcDecl, RootState model) {
        // generate states based on statement list
        State currentState = null;
        State nextState = null;
        for (Statement stmt : funcDecl.getStmtList().getStmts()) {
            currentState = stateTable.nextState(currentState);
            nextState = stateTable.nextState(currentState);
            model.getStates().add(currentState);
            updateState(model, currentState, nextState, stmt);
        }

        // add last state
        if (nextState != null) {
            model.getStates().add(nextState);
        }

        // mark first and last
        if (!model.getStates().isEmpty()) {
            model.getStates().get(0).setInitial(true);
            model.getStates().get(model.getStates().size() - 1).setFinal(true);
        }
    }

    /**
     * SelectionStmt | IterationStmt | VarDeclStmt | AssignmentStmt | FunctionCallStmt | ReturnStmt
     * @param superState
     * @param state
     * @param nextState
     * @param stmt
     */
    private void updateState(SuperState superState, State state, State nextState, Statement stmt) {
        // transition
        var transition = new Transition();
        transition.setTargetState(nextState);
        state.getOutgoingTransitions().add(transition);

        // function call
        updateStateReferenceCallIfNeeded(state, stmt);

        // effect
        if (state.getReferenceState() == null) {
            var effect = generateEffect(superState.getDeclarations(), stmt);
            transition.setEffects(List.of(effect));
        }
    }

    private void updateStateReferenceCallIfNeeded(State state, Statement stmt) {
        if (stmt instanceof VarDeclStmt) {
            VarDeclStmt varDeclStmt = (VarDeclStmt)stmt;
            if (findRefState(state, varDeclStmt.getAssignment().getExpr())) {
                // update return
                state.getReferenceBindingExprs().add(createValuedReferenceExpr(varDeclStmt.getDecl()));
                return;
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
        logger.info("findVarDecl: " + name);
        return declarations.stream()
                .filter(d -> d.getName().equals(name))
                .findAny()
                .orElseThrow();
    }

    private SVarDeclaration convertDeclaration(DataType type, String name, boolean array, boolean isInput, boolean isOutput) {
        var result = new SVarDeclaration();
        result.setDataType(convertDataType(type));
        result.setName(name);
        result.setInput(isInput);
        result.setOutput(isOutput);
        //TODO array
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

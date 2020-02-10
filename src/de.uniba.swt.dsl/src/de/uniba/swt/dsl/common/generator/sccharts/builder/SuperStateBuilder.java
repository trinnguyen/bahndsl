package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnException;
import de.uniba.swt.dsl.common.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SuperStateBuilder {
    public final String VAR_OUTPUT_NAME = "out";

    /**
     * temporary allocate param array size to 1024
     * TODO should guess the size of param based on function call
     */
    public final int DEFAULT_PARAM_ARRAY_SIZE = 1024;

    private Map<FuncDecl, RootState> mapFuncState;
    private StateTable stateTable;
    protected SuperState superState;
    private StatementList statementList;
    private Stack<SuperState> stackSuperStates;

    public SuperStateBuilder(Map<FuncDecl, RootState> mapFuncState, Stack<SuperState> stackSuperStates, String id, StatementList statementList) {
        this(mapFuncState, stackSuperStates, new StateTable(id), new SuperState(id), statementList);
    }

    protected SuperStateBuilder(Map<FuncDecl, RootState> mapFuncState, Stack<SuperState> stackSuperStates, StateTable stateTable, SuperState superState, StatementList statementList) {
        this.mapFuncState = mapFuncState;
        this.stackSuperStates = stackSuperStates;
        this.stateTable = stateTable;
        this.superState = superState;
        this.statementList = statementList;
    }

    public SuperState build() {
        // push
        stackSuperStates.push(superState);

        // initial
        initializeDeclarationInterface(superState, statementList);

        // add child
        if (statementList != null) {
            addChildStates(statementList);
        }

        // pop stack
        stackSuperStates.pop();

        return superState;
    }

    private void addChildStates(StatementList stmtList) {

        String nextStateId = null;
        for (Statement stmt : stmtList.getStmts()) {
            var id = StringUtil.isNotEmpty(nextStateId) ? nextStateId : stateTable.nextStateId();
            nextStateId = stateTable.nextStateId();

            // selection
            if (stmt instanceof SelectionStmt) {
                var curState = buildSelectionSuperState(id, (SelectionStmt) stmt);
                updateInitialAndFinalState(curState);
                curState.setJoinTargetId(nextStateId);
                superState.getStates().add(curState);
            } else if (stmt instanceof IterationStmt) {
                addIterationSuperState(id, nextStateId, (IterationStmt) stmt);
            } else {
                var state = addNormalState(id, nextStateId, stmt);
                superState.getStates().add(state);
            }
        }

        // add last state
        if (StringUtil.isNotEmpty(nextStateId)) {
            superState.getStates().add(new State(nextStateId));
        }

        // mark first and last
        updateInitialAndFinalState(superState);
    }

    private void updateInitialAndFinalState(SuperState state) {
        if (!state.getStates().isEmpty()) {
            state.getStates().get(0).setInitial(true);
            state.getStates().get(state.getStates().size() - 1).setFinal(true);
        }
    }

    private SuperState buildSelectionSuperState(String id, SelectionStmt stmt) {
        if (stmt.getElseStmts() != null) {

            // create super state manually
            StateTable stateTable = new StateTable(id);
            SuperState superState = new SuperState(id);
            stackSuperStates.push(superState);

            // create 3 states
            State initialState = new State(stateTable.nextStateId());
            var thenState = new SuperStateBuilder(mapFuncState, stackSuperStates, stateTable.nextStateId(), stmt.getThenStmts()).build();
            var elseState = new SuperStateBuilder(mapFuncState, stackSuperStates, stateTable.nextStateId(), stmt.getThenStmts()).build();
            State finalState = new State(stateTable.nextStateId());

            // initial -> then
            Transition conditionTran = new Transition(thenState.getId());
            conditionTran.setTrigger(stmt.getExpr());
            initialState.getOutgoingTransitions().add(conditionTran);

            // or else
            initialState.getOutgoingTransitions().add(new Transition(elseState.getId()));

            // go to final state
            thenState.setJoinTargetId(finalState.getId());
            elseState.setJoinTargetId(finalState.getId());

            // add and return
            superState.getStates().add(initialState);
            superState.getStates().add(thenState);
            superState.getStates().add(elseState);
            superState.getStates().add(finalState);

            stackSuperStates.pop();
            return superState;
        }

        return new SuperStateBuilder(mapFuncState, stackSuperStates, id, stmt.getThenStmts()).build();
    }

    private void addIterationSuperState(String id, String nextStateId, IterationStmt stmt) {
        // create super state manually
        StateTable stateTable = new StateTable(id);

        // create body states
        State initialState = new State(id);
        var bodyState = new SuperStateBuilder(mapFuncState, stackSuperStates, stateTable.nextStateId(), stmt.getStmts()).build();

        // link: while -> body
        Transition conditionTran = new Transition(bodyState.getId());
        conditionTran.setTrigger(stmt.getExpr());
        initialState.getOutgoingTransitions().add(conditionTran);

        // body -> back to beginning
        bodyState.setJoinTargetId(initialState.getId());

        // go to final state
        initialState.getOutgoingTransitions().add(new Transition(nextStateId));

        // add
        superState.getStates().add(initialState);
        superState.getStates().add(bodyState);
    }

    private State addNormalState(String id, String nextStateId, Statement stmt) {
        var state = new State(id);
        updateStateReferenceCallIfNeeded(state, stmt);

        // transition
        var transition = new Transition(nextStateId);
        state.getOutgoingTransitions().add(transition);
        if (state.getReferenceState() == null) {
            var effect = generateEffect(stmt);
            if (effect != null)
                transition.setEffects(List.of(effect));
        }

        return state;
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
            findRefState(state, functionCallStmt.getExpr());
        }
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

    private Effect generateEffect(Statement stmt) {
        if (stmt instanceof VarDeclStmt) {

            if (((VarDeclStmt) stmt).getAssignment() == null)
                return null;

            AssignmentEffect effect = new AssignmentEffect();
            effect.setExpression(((VarDeclStmt) stmt).getAssignment().getExpr());
            effect.setVarDeclaration(findVarDecl(((VarDeclStmt) stmt).getDecl().getName()));
            return effect;
        }

        if (stmt instanceof AssignmentStmt) {
            AssignmentEffect effect = new AssignmentEffect();
            effect.setExpression(((AssignmentStmt) stmt).getAssignment().getExpr());
            effect.setVarDeclaration(findVarDecl(((AssignmentStmt) stmt).getReferenceExpr().getDecl().getName()));
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
            effect.setVarDeclaration(findVarDecl(VAR_OUTPUT_NAME));
            return effect;
        }

        throw new RuntimeException("Statement is not supported");
    }

    private SVarDeclaration findVarDecl(String name) {
        for (SuperState stackSuperState : stackSuperStates) {
            var declarations = stackSuperState.getDeclarations();
            var res = declarations.stream()
                    .filter(d -> d.getName().equals(name))
                    .findAny();
            if (res.isPresent())
                return res.get();
        }

        return null;
    }


    /**
     * caching all local variables (decl) and host code reference
     * @param superState
     * @param statementList
     */
    private void initializeDeclarationInterface(SuperState superState, StatementList statementList) {
        if (statementList == null)
            return;

        // local variables
        for (Statement stmt : statementList.getStmts()) {

            // VarDeclStmt
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

                // find reference expression
                if (varDeclStmt.getAssignment() != null) {
                    findAndAddHostCodeReference(superState, varDeclStmt.getAssignment().getExpr());
                }
                continue;
            }

            // AssignmentStmt
            if (stmt instanceof AssignmentStmt) {
                findAndAddHostCodeReference(superState, ((AssignmentStmt) stmt).getAssignment().getExpr());
                continue;
            }

            // ReturnStmt
            if (stmt instanceof ReturnStmt) {
                findAndAddHostCodeReference(superState, ((ReturnStmt) stmt).getExpr());
            }
        }
    }

    /**
     * recursively find all host code reference in expression
     * @param superState
     * @param expression
     */
    private void findAndAddHostCodeReference(SuperState superState, Expression expression) {
        if (expression == null)
            return;

        if (expression instanceof ExternalFunctionCallExpr) {
            superState.getHostCodeReferences().add(((ExternalFunctionCallExpr) expression).getName());
            return;
        }

        if (expression instanceof UnaryExpr) {
            findAndAddHostCodeReference(superState, ((UnaryExpr) expression).getExpr());
            return;
        }

        if (expression instanceof ParenthesizedExpr) {
            findAndAddHostCodeReference(superState, ((ParenthesizedExpr) expression).getExpr());
            return;
        }

        if (expression instanceof ValuedReferenceExpr) {
            var indexExpr = ((ValuedReferenceExpr) expression).getIndexExpr();
            if (indexExpr != null) {
                findAndAddHostCodeReference(superState, indexExpr);
            }
            return;
        }

        if (expression instanceof FunctionCallExpr) {
            for (Expression param : ((FunctionCallExpr) expression).getParams()) {
                findAndAddHostCodeReference(superState, param);
            }
            return;
        }

        // go through
        if (expression instanceof OpExpression) {
            findAndAddHostCodeReference(superState, ((OpExpression) expression).getLeftExpr());
            findAndAddHostCodeReference(superState, ((OpExpression) expression).getRightExpr());
        }
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
    protected SVarDeclaration convertDeclaration(DataType type, String name, int arrayCardinality, boolean isInput, boolean isOutput) {
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

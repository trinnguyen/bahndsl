package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SuperStateBuilder {
    public final String VAR_OUTPUT_NAME = "_out";

    /**
     * temporary allocate param array size to 1024
     * TODO should guess the size of param based on function call
     */
    public final int DEFAULT_PARAM_ARRAY_SIZE = BahnConstants.DEFAULT_ARRAY_SIZE;

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

        // add regular states from the child list
        if (statementList != null && statementList.getStmts() != null && !statementList.getStmts().isEmpty()) {
            addChildStates(statementList);
        } else {
            // add 2 states, one for initial and the other for final, must be deferred transition for being compatible
            // with statebased compilation trategy
            var initialState = new State(stateTable.nextStateId());
            var finalState = new State(stateTable.nextStateId());
            initialState.addTransition(finalState.getId(), false);
            superState.getStates().add(initialState);
            superState.getStates().add(finalState);
        }

        // mark first and last
        updateInitialAndFinalState(superState);

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
                var state = createState(id, nextStateId, stmt);
                if (state != null) {
                    superState.getStates().add(state);
                } else {
                    // stay
                    nextStateId = id;
                }
            }
        }

        // add last state
        if (StringUtil.isNotEmpty(nextStateId)) {
            superState.getStates().add(new State(nextStateId));
        }
    }

    private void updateInitialAndFinalState(SuperState state) {
        if (!state.getStates().isEmpty()) {
            state.getStates().get(0).setInitial(true);
            state.getStates().get(state.getStates().size() - 1).setFinal(true);
        }
    }

    private SuperState buildSelectionSuperState(String id, SelectionStmt stmt) {
        // create super state manually
        StateTable stateTable = new StateTable(id);
        SuperState superState = new SuperState(id);
        stackSuperStates.push(superState);

        // create 3 states
        State initialState = new State(stateTable.nextStateId());
        var thenState = new SuperStateBuilder(mapFuncState, stackSuperStates, stateTable.nextStateId(), stmt.getThenStmts()).build();
        State finalState = new State(stateTable.nextStateId());

        // initial -> then
        Transition conditionTran = new Transition(thenState.getId());
        conditionTran.setTrigger(stmt.getExpr());
        initialState.getOutgoingTransitions().add(conditionTran);
        thenState.setJoinTargetId(finalState.getId());

        // add
        superState.getStates().add(initialState);
        superState.getStates().add(thenState);

        // go to final state
        SuperState elseState = null;
        if (stmt.getElseStmts() != null) {
            elseState = new SuperStateBuilder(mapFuncState, stackSuperStates, stateTable.nextStateId(), stmt.getElseStmts()).build();
            initialState.getOutgoingTransitions().add(new Transition(elseState.getId()));
            elseState.setJoinTargetId(finalState.getId());
            superState.getStates().add(elseState);
        } else {
            initialState.getOutgoingTransitions().add(new Transition(finalState.getId()));
        }


        // add final
        superState.getStates().add(finalState);
        stackSuperStates.pop();
        return superState;
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

    private State createState(String id, String nextStateId, Statement stmt) {
        var superState = findFunctionCallSuperState(stmt);
        if (superState != null) {
            updateFunctionCallSuperState(superState, id, nextStateId);
            return superState;
        }

        // transition
        var effect = generateEffect(stmt);
        if (effect != null) {
            var transition = new Transition(nextStateId);
            transition.getEffects().add(effect);

            var state = new State(id);
            state.getOutgoingTransitions().add(transition);
            return state;
        }

        return null;
    }

    private void updateFunctionCallSuperState(SuperState superState, String id, String nextStateId) {
        // update id and target
        superState.setId(id);
        superState.setJoinTargetId(nextStateId);
    }

    private SuperState findFunctionCallSuperState(Statement stmt) {
        if (stmt instanceof VarDeclStmt) {
            VarDeclStmt varDeclStmt = (VarDeclStmt)stmt;
            if (varDeclStmt.getAssignment() != null) {
                return findRefState(varDeclStmt.getAssignment().getExpr(), createValuedReferenceExpr(varDeclStmt.getDecl()));
            }
        }

        if (stmt instanceof AssignmentStmt) {
            AssignmentStmt assignmentStmt = (AssignmentStmt) stmt;
            return findRefState(assignmentStmt.getAssignment().getExpr(), assignmentStmt.getReferenceExpr());
        }

        if (stmt instanceof FunctionCallStmt) {
            FunctionCallStmt functionCallStmt = (FunctionCallStmt) stmt;
            return findRefState(functionCallStmt.getExpr(), null);
        }

        return null;
    }

    private SuperState findRefState(Expression expr, ValuedReferenceExpr returnRefExpr) {
        if (expr instanceof RegularFunctionCallExpr) {
            var functionCallExpr = (RegularFunctionCallExpr)expr;
            var refState = mapFuncState.get(functionCallExpr.getDecl());
            if (refState != null) {
                return createFuncRefState(refState, functionCallExpr.getParams(), returnRefExpr);
            }
        }

        return null;
    }

    private static SuperState createFuncRefState(SuperState superState, List<Expression> params, Expression returnExpr) {
        var result = new SuperState(superState, params);
        if (returnExpr != null)
            result.getReferenceBindingExprs().add(returnExpr);
        return result;
    }

    private ValuedReferenceExpr createValuedReferenceExpr(VarDecl decl) {
        var expr = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        expr.setDecl(decl);
        return expr;
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

            var ref = ((AssignmentStmt) stmt).getReferenceExpr();
            effect.setVarDeclaration(findVarDecl(ref.getDecl().getName()));
            if (ref.getIndexExpr() != null) {
                effect.setIndexExpr(ref.getIndexExpr());
            }
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

            if (stmt instanceof SelectionStmt) {
                findAndAddHostCodeReference(superState, ((SelectionStmt) stmt).getExpr());
                continue;
            }

            if (stmt instanceof IterationStmt) {
                findAndAddHostCodeReference(superState, ((IterationStmt) stmt).getExpr());
            }

            // VarDeclStmt
            if (stmt instanceof VarDeclStmt) {
                VarDeclStmt varDeclStmt = (VarDeclStmt)stmt;
                VarDecl varDecl = varDeclStmt.getDecl();

                // array cardinality must be a int literal
                int arraySize = varDecl.isArray() ? BahnConstants.DEFAULT_ARRAY_SIZE : 0;
                /*
                if (varDecl.isArray()) {
                    if (varDecl.getCardinality() instanceof NumberLiteral) {
                        arraySize = (int) ((NumberLiteral) varDecl.getCardinality()).getValue();
                    } else {
                        throw new BahnException("Array cardinality must be a literal integer");
                    }
                }
                 */

                superState.getDeclarations().add(convertDeclaration(varDecl.getType(), varDecl.getName(), arraySize, false, false));

                // find reference expression
                if (varDeclStmt.getAssignment() != null) {
                    findAndAddHostCodeReference(superState, varDeclStmt.getAssignment().getExpr());
                }
                continue;
            }

            if (stmt instanceof FunctionCallStmt) {
                findAndAddHostCodeReference(superState, ((FunctionCallStmt) stmt).getExpr());
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
            // params
            for (Expression param : ((ExternalFunctionCallExpr) expression).getParams()) {
                findAndAddHostCodeReference(superState, param);
            }

            // function itself
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

        if (expression instanceof RegularFunctionCallExpr) {
            for (Expression param : ((RegularFunctionCallExpr) expression).getParams()) {
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

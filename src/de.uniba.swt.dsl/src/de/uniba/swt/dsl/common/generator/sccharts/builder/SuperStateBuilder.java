package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SuperStateBuilder {

    /**
     * temporary allocate param array size to 1024
     * TODO should guess the size of param based on function call
     */
    public final int DEFAULT_PARAM_ARRAY_SIZE = BahnConstants.DEFAULT_ARRAY_SIZE;

    private final Map<FuncDecl, RootState> mapFuncState;
    private final StateTable stateTable;
    protected SuperState superState;
    private final StatementList statementList;
    private final Stack<SuperState> stackSuperStates;

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
            // add 2 states, one for initial and the other for final, must be regular transition for being compatible
            // with statebased compilation strategy
            var initialState = new State(stateTable.nextStateId());
            var finalState = new State(stateTable.finalStateId());
            initialState.addTransition(finalState.getId(), TransitionType.Regular, null);
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
        String finalStateId = stateTable.finalStateId();
        for (int i = 0; i < stmtList.getStmts().size(); i++) {

            // process
            var stmt = stmtList.getStmts().get(i);
            var isLast = i == stmtList.getStmts().size() - 1;
            var isReturn = stmt instanceof ReturnStmt;
            var isBreak = stmt instanceof BreakStmt;
            var id = StringUtil.isNotEmpty(nextStateId) ? nextStateId : stateTable.nextStateId();

            // build regular state
            nextStateId = (isLast || isReturn) ? finalStateId : stateTable.nextStateId();

            // selection
            if (stmt instanceof SelectionStmt) {
                addSelectionSuperState(id, nextStateId, finalStateId, (SelectionStmt) stmt);
            } else if (stmt instanceof IterationStmt) {
                addIterationSuperState(id, nextStateId, finalStateId, (IterationStmt) stmt);
            } else {
                var state = createState(id, nextStateId, stmt);
                if (state != null) {
                    superState.getStates().add(state);
                } else {
                    // stay
                    nextStateId = id;
                }
            }

            // ignore remaining actions if return or break
            if (isReturn || isBreak)
                break;
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

    private void addSelectionSuperState(String id, String nextStateId, String finalStateId, SelectionStmt stmt) {

        // create 3 states
        String lastStateId = stateTable.nextStateId();
        State initialState = new State(id);

        // initial -> then
        StateTable localStateTable = new StateTable(id);
        var thenState = new SuperStateBuilder(mapFuncState, stackSuperStates, localStateTable.nextStateId(), stmt.getThenStmts()).build();
        thenState.setJoinTargetId(lastStateId);
        initialState.addImmediateTransition(thenState.getId(), stmt.getExpr());

        // initial -> else
        SuperState elseState = null;
        if (stmt.getElseStmts() != null) {
            elseState = new SuperStateBuilder(mapFuncState, stackSuperStates, localStateTable.nextStateId(), stmt.getElseStmts()).build();
            elseState.setJoinTargetId(lastStateId);
            initialState.addImmediateTransition(elseState.getId());
        }

        // add
        superState.getStates().add(initialState);
        superState.getStates().add(thenState);

        // go to final state
        if (elseState != null) {
            superState.getStates().add(elseState);
        } else {
            initialState.addImmediateTransition(lastStateId);
        }

        // create last state
        State lastState = new State(lastStateId);
        lastState.addImmediateTransition(nextStateId);
        superState.getStates().add(lastState);

        // check break when finishing the loop -> go to final
        addHasBreakTransitionExist(lastState, finalStateId, stmt, false);

        // check return when finishing the loop -> move to final
        addHasReturnTransitionExist(lastState, finalStateId, stmt);
    }

    private void addIterationSuperState(String id, String nextStateId, String finalStateId, IterationStmt stmt) {
        // create super state manually
        State initialState = new State(id);
        State lastState = new State(stateTable.nextStateId());

        // create body states
        StateTable localStateTable = new StateTable(id);
        var bodyState = new SuperStateBuilder(mapFuncState, stackSuperStates, localStateTable.nextStateId(), stmt.getStmts()).build();

        // link: initial check loop condition
        initialState.addImmediateTransition(bodyState.getId(), stmt.getExpr());

        // body -> back to beginning
        bodyState.setJoinTargetId(lastState.getId());

        // go to next state
        initialState.addImmediateTransition(nextStateId);
        lastState.addImmediateTransition(initialState.getId());

        // add
        superState.getStates().add(initialState);
        superState.getStates().add(bodyState);
        superState.getStates().add(lastState);

        // check break when finishing the loop -> go to next step
        addHasBreakTransitionExist(lastState, nextStateId, stmt, true);

        // check return when finishing the loop -> move to final
        addHasReturnTransitionExist(lastState, finalStateId, stmt);
    }

    private void addHasBreakTransitionExist(State lastState, String finalStateId, Statement stmt, boolean reset) {
        var breakTransition = createHasBreakTransitionIfExist(stmt, finalStateId, reset);
        if (breakTransition != null) {
            lastState.getOutgoingTransitions().add(0, breakTransition);
        }
    }

    private void addHasReturnTransitionExist(State lastState, String finalStateId, Statement stmt) {
        var returnTransition = createHasReturnTransitionIfExist(stmt, finalStateId);
        if (returnTransition != null) {
            // reset all immediate to regular
            for (Transition outgoingTransition : lastState.getOutgoingTransitions()) {
                if (outgoingTransition.getTransitionType() == TransitionType.Immediate) {
                    outgoingTransition.setTransitionType(TransitionType.Regular);
                }
            }

            lastState.getOutgoingTransitions().add(0, returnTransition);
        }
    }

    private Transition createHasReturnTransitionIfExist(Statement stmt, String finalStateId) {
        if (!BahnUtil.hasReturnStmtInBlock(stmt))
            return null;

        var hasReturnVar = findVarDecl(SCChartsUtil.VAR_HAS_RETURN_NAME);
        if (hasReturnVar != null) {
            var tran = new Transition(finalStateId, TransitionType.AbortTo);
            tran.setTrigger(SCChartsUtil.createTrueBooleanTrigger(hasReturnVar));
            return tran;
        }

        return null;
    }

    private Transition createHasBreakTransitionIfExist(Statement stmt, String nextStateId, boolean reset) {
        if (!BahnUtil.hasBreakStmtInBlock(stmt))
            return null;

        var hasBreakVar = findVarDecl(SCChartsUtil.VAR_HAS_BREAK);
        if (hasBreakVar != null) {
            var transition = new Transition(nextStateId, TransitionType.Immediate);
            transition.setTrigger(SCChartsUtil.createTrueBooleanTrigger(hasBreakVar));

            // reset break
            if (reset) {
                transition.getEffects().add(SCChartsUtil.generateBoolAssignEffect(hasBreakVar, false));
            }

            return transition;
        }

        return null;
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

            // add has return link to final state
            if (stmt instanceof ReturnStmt) {
                transition.getEffects().add(SCChartsUtil.generateBoolAssignEffect(findVarDecl(SCChartsUtil.VAR_HAS_RETURN_NAME), true));
            }

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
                return findRefState(varDeclStmt.getAssignment().getExpr(), SCChartsUtil.createValuedReferenceExpr(varDeclStmt.getDecl()));
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

            // add root state if need
            var decl = functionCallExpr.getDecl();
            if (!mapFuncState.containsKey(decl)) {
                mapFuncState.put(decl, buildRootState(decl));
            }

            var refState = mapFuncState.get(functionCallExpr.getDecl());
            if (refState != null) {
                return createFuncRefState(refState, functionCallExpr.getParams(), returnRefExpr);
            }
        }

        return null;
    }

    private static RootState buildRootState(FuncDecl decl) {
        RootStateBuilder rootStateBuilder = new RootStateBuilder(decl);
        rootStateBuilder.build();
        return rootStateBuilder.getRootState();
    }

    private static SuperState createFuncRefState(SuperState superState, List<Expression> params, Expression returnExpr) {
        var result = new SuperState(superState, params);
        if (returnExpr != null)
            result.getReferenceBindingExprs().add(returnExpr);
        return result;
    }

    private Effect generateEffect(Statement stmt) {
        if (stmt instanceof VarDeclStmt) {

            if (((VarDeclStmt) stmt).getAssignment() == null)
                return null;

            AssignmentEffect effect = new AssignmentEffect();
            updateEffect(effect, ((VarDeclStmt) stmt).getAssignment());
            effect.setVarDeclaration(findVarDecl(((VarDeclStmt) stmt).getDecl().getName()));
            return effect;
        }

        if (stmt instanceof AssignmentStmt) {
            AssignmentEffect effect = new AssignmentEffect();
            updateEffect(effect, ((AssignmentStmt) stmt).getAssignment());

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
            effect.setVarDeclaration(findVarDecl(SCChartsUtil.VAR_OUTPUT_NAME));
            return effect;
        }

        if (stmt instanceof BreakStmt) {
            AssignmentEffect effect = new AssignmentEffect();
            effect.setExpression(BahnUtil.createBooleanLiteral(true));
            effect.setVarDeclaration(findVarDecl(SCChartsUtil.VAR_HAS_BREAK));
            return effect;
        }

        throw new RuntimeException("Statement is not supported");
    }

    private void updateEffect(AssignmentEffect effect, VariableAssignment assignment) {
        effect.setExpression(assignment.getExpr());
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

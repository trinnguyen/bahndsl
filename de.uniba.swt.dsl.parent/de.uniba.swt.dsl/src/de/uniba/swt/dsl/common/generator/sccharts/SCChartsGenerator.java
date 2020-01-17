package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;

import java.util.List;
import java.util.stream.Collectors;

public class SCChartsGenerator {

    @Inject SCChartsTextualBuilder builder;

    @Inject
    StateTable stateTable;

    public String generate(RootModule rootModule) {
        SCCharts models = buildSCChartsModels(rootModule);
        return builder.buildString(models);
    }

    private SCCharts buildSCChartsModels(RootModule rootModule) {
        List<RootState> states = rootModule
                .getProperties()
                .stream()
                .filter(p -> p instanceof FuncDecl).map(p -> (FuncDecl)p)
                .map(this::buildModel)
                .collect(Collectors.toUnmodifiableList());
        return new SCCharts(states);
    }

    private RootState buildModel(FuncDecl funcDecl) {
        RootState rootState = new RootState();
        updateModel(rootState, funcDecl);
        return rootState;
    }

    private void updateModel(SuperState superState, FuncDecl funcDecl) {
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

        // generate states based on statement list
        State currentState = null;
        State nextState = null;
        for (Statement stmt : funcDecl.getStmtList().getStmts()) {
            currentState = stateTable.nextState(currentState);
            nextState = stateTable.nextState(currentState);
            superState.getStates().add(currentState);
            updateState(superState, currentState, nextState, stmt);
        }

        // add last state
        if (nextState != null) {
            superState.getStates().add(nextState);
        }

        // mark first and last
        superState.getStates().get(0).setInitial(true);
        superState.getStates().get(superState.getStates().size() - 1).setFinal(true);
    }

    /**
     * SelectionStmt | IterationStmt | VarDeclStmt | AssignmentStmt | FunctionCallStmt | ReturnStmt
     * @param superState
     * @param state
     * @param nextState
     * @param stmt
     */
    private void updateState(SuperState superState, State state, State nextState, Statement stmt) {
        var effect = generateEffect(superState.getDeclarations(), stmt);

        var transition = new Transition();
        transition.setTargetState(nextState);
        transition.setEffects(List.of(effect));
        state.getOutgoingTransitions().add(transition);
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
        return declarations.stream().filter(d -> d.getName().equalsIgnoreCase(name)).findFirst().orElseThrow();
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

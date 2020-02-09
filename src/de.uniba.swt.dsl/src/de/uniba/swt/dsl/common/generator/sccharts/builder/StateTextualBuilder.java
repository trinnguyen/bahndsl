package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.Expression;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StateTextualBuilder extends TextualBuilder {

    @Inject
    ExpressionTextualBuilder expressionTextualBuilder;

    private RootState rootState;

    public String buildString(RootState rootState) {
        clear();
        this.rootState = rootState;
        generateRootState();
        return build();
    }

    private void generateRootState() {
        append("scchart").append(rootState.getId()).append("{").append(LINE_BREAK);

        // host code references
        generateHostcodeReferences(rootState.getHostCodeReferences());

        // interface variables
        generateVarDecls(rootState.getDeclarations());

        // all root states
        if (rootState.getStates() != null) {
            for (var childState : rootState.getStates()) {
                generateRegularState(childState);
                append(LINE_BREAK);
            }
        }

        append("}");
    }

    private void generateRegularState(State state) {
        if (state.isInitial())
            append("initial");

        if (state.isFinal())
            append("final");

        append("state").append(state.getId());
        if (state.getLabel() != null)
            append(state.getLabel());

        // reference
        if (state.getReferenceState() != null) {
            generateReferenceState(state);
        }

        // transitions
        if (state.getOutgoingTransitions() != null) {
            for (var transition : state.getOutgoingTransitions()) {
                generateTransition(transition);
            }
        }
    }

    private void generateReferenceState(State state) {
        RootState refState = state.getReferenceState();
        append("is").append(refState.getId());

        if (state.getReferenceBindingExprs().size() > 0) {
            var inOutVars = refState.getDeclarations().stream().filter(s -> s.isInput() || s.isOutput()).collect(Collectors.toList());

            List<String> txtBindings = new ArrayList<>();
            for (int i = 0; i < state.getReferenceBindingExprs().size(); i++) {
                var param = state.getReferenceBindingExprs().get(i);
                var decl = inOutVars.get(i);
                txtBindings.add(String.format("%s to %s",
                        generateExpression(param),
                        decl.getName()));
            }
            append("(").append(String.join(",", txtBindings)).append(")");
        }
    }

    private void generateHostcodeReferences(Set<String> hostCodeReferences) {
        for (String name : hostCodeReferences) {
            append(String.format("extern @C \"%s\" %s", name, name));
            append(LINE_BREAK);
        }
    }

    private void generateVarDecls(List<SVarDeclaration> declarations) {
        for (var decl : declarations) {
            generateVarDecl(decl);
            append(LINE_BREAK);
        }
    }

    /**
     * input int a
     * input int b
     * input output int c
     * output int c
     * int d
     * @param decl declaration
     */
    private void generateVarDecl(SVarDeclaration decl) {
        if (decl.isInput())
            append("input");

        if (decl.isOutput())
            append("output");

        append(decl.getDataType().toString().toLowerCase());
        append(decl.getName());
        if (decl.getCardinality() > 0) {
            append("[" + decl.getCardinality() + "]");
        }
    }

    private void generateTransition(Transition transition) {
        // action
        if (transition.isImmediate()) {
            append("immediate");
        }
        if (transition.isDeferred()) {
            append("deferred");
        }
        generateAction(transition);

        // go to
        if (transition.getTargetState() != null) {
            append("go").append("to").append(transition.getTargetState().getId());
        }
    }

    private void generateLocalAction(LocalAction action) {
        switch (action.getActionType()) {
            case Entry:
                append("entry");
                break;
            case During:
                append("during");
                break;
            case Exit:
                append("exit");
                break;
        }

        generateAction(action);
    }

    /**
     * do effect = 0
     * if trigger do effect =0
     * @param action action
     */
    private void generateAction(Action action) {
        if (action.getTrigger() != null) {
            append("if").append(generateExpression(action.getTrigger()));
        }

        if (action.getEffects() != null && !action.getEffects().isEmpty()) {
            append("do ");
            append(action.getEffects().stream().map(this::generateEffect).collect(Collectors.joining(";")));
        }
    }

    private String generateEffect(Effect effect) {
        var strBuilder = new StringBuilder();
        if (effect instanceof AssignmentEffect) {
            var decl = ((AssignmentEffect) effect).getVarDeclaration();
            strBuilder.append(decl.getName()).append(" = ");
        }
        strBuilder.append(generateExpression(effect.getExpression()));
        return strBuilder.toString();
    }

    private String generateExpression(Expression expression) {
        return expressionTextualBuilder.buildString(expression);
    }
}

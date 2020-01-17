package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.VarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SCChartsTextualBuilder extends TextualBuilder {

    public String buildString(SCCharts model) {
        clear();
        for (var rootState : model.getRootStates()) {
            generateState(rootState, true);
            append(LINE_BREAK);
        }

        return build();
    }

    private void generateState(State state, boolean isRootModel) {
        if (state.isInitial())
            append("initial");

        if (state.isFinal())
            append("final");

        var prefix = isRootModel ? "scchart" : "state";
        append(prefix).append(state.getId());
        if (state.getLabel() != null)
            append(state.getLabel());

        // super
        if (state instanceof SuperState) {
            appendLine("{");

            // variables
            generateVarDecls(((SuperState)state).getDeclarations());

            // generate actions
            SuperState superState = (SuperState) state;
            if (superState.getLocalActions() != null) {
                for (var action : superState.getLocalActions()) {
                    generateLocalAction(action);
                }
            }

            // all root states
            if (superState.getStates() != null) {
                for (var childState : superState.getStates()) {
                    generateState(childState, false);
                    append(LINE_BREAK);
                }
            }
        }

        // transitions
        if (state.getOutgoingTransitions() != null) {
            for (var transition : state.getOutgoingTransitions()) {
                generateTransition(transition);
            }
        }

        // close
        if (state instanceof SuperState)
            appendLine("}");
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

        // initial value
        if (decl.getInitialExpr() != null) {
            append("=").append(ExpressionTextualBuilder.buildString(decl.getInitialExpr()));
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
            append("if").append(ExpressionTextualBuilder.buildString(action.getTrigger()));
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
        strBuilder.append(ExpressionTextualBuilder.buildString(effect.getExpression()));
        return strBuilder.toString();
    }
}

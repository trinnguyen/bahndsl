package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.AssignmentStmt;
import de.uniba.swt.dsl.bahn.Expression;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.StringUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StateTextualBuilder extends TextualBuilder {

    @Inject
    ExpressionTextualBuilder expressionTextualBuilder;

    @Inject
    ParamBindingTable paramBindingTable;

    private RootState rootState;

    public String buildString(RootState rootState) {
        clear();
        this.rootState = rootState;
        generateRootState();
        return build();
    }

    private void generateRootState() {
        paramBindingTable.reset();
        append("scchart").append(rootState.getId());
        generateSuperState(rootState, true);
    }

    private void generateSuperState(SuperState superState, boolean generateInputOutput) {

        // add the mapping
        paramBindingTable.addBindingMappingIfNeeded(superState);

        append("{");
        increaseIndent().appendLine("");

        // host code references
        generateHostcodeReferences(superState.getHostCodeReferences());

        // interface variables
        generateVarDecls(superState.getDeclarations(), generateInputOutput);

        // generate local actions
        for (LocalAction localAction : superState.getLocalActions()) {
            generateLocalAction(localAction);
        }

        // all root states
        if (superState.getStates() != null) {
            for (var childState : superState.getStates()) {
                if (childState instanceof SuperState) {
                    appendLine("");
                    generateStateId(childState);
                    generateSuperState((SuperState)childState, false);
                    appendLine("");
                } else {
                    generateRegularState(childState);
                }
            }
        }

        decreaseIndent();
        appendLine("}");

        // join
        if (StringUtil.isNotEmpty(superState.getJoinTargetId())) {
            appendLine("join to " + superState.getJoinTargetId());
        }

        // remove binding
        paramBindingTable.removeBindingMappingIfNeeded(superState);
    }

    private void generateRegularState(State state) {
        appendLine("");
        generateStateId(state);

        // transitions
        if (state.getOutgoingTransitions() != null) {
            for (var transition : state.getOutgoingTransitions()) {
                generateTransition(transition);
            }
        }

        if (!state.isFinal())
            appendLine("");
    }

    private void generateStateId(State state) {
        if (state.isInitial())
            append("initial");

        if (state.isFinal())
            append("final");

        append("state").append(state.getId());
        if (state.getLabel() != null)
            append("\"" + state.getLabel() + "\"");
    }

    private void generateHostcodeReferences(Set<String> hostCodeReferences) {
        for (String name : hostCodeReferences) {
            append(String.format("extern @C \"%s\" %s", name, name));
            append(LINE_BREAK);
        }
    }

    private void generateVarDecls(List<SVarDeclaration> declarations, boolean generateInputOutput) {
        for (var decl : declarations) {

            if ((decl.isInput() || decl.isOutput()) && !generateInputOutput) {
                continue;
            }

            generateVarDecl(decl);
            appendLine("");
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
        appendLine("");
        if (transition.getTransitionType() == TransitionType.Immediate) {
            append("immediate");
        }

        generateAction(transition);

        // go to
        if (transition.getTransitionType() == TransitionType.AbortTo) {
            append("abort").append("to");
        } else {
            append("go").append("to");
        }

        // target
        append(transition.getTargetStateId());
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
            var assignmentEffect = (AssignmentEffect) effect;
            var declName = assignmentEffect.getVarDeclaration().getName();
            var name = paramBindingTable.lookupBindingName(declName);

            strBuilder.append(name);

            // array index
            if (assignmentEffect.getIndexExpr() != null) {
                strBuilder.append("[");
                strBuilder.append(generateExpression(assignmentEffect.getIndexExpr()));
                strBuilder.append("]");
            }

            strBuilder.append(" = ");

            // array expr
            if (assignmentEffect.getArrayExprs() != null) {
                strBuilder.append(expressionTextualBuilder.buildString(assignmentEffect.getArrayExprs()));
            }
        }

        if (effect.getExpression() != null) {
            strBuilder.append(generateExpression(effect.getExpression()));
        }

        return strBuilder.toString().trim();
    }

    private String generateExpression(Expression expression) {
        return expressionTextualBuilder.buildString(expression);
    }
}

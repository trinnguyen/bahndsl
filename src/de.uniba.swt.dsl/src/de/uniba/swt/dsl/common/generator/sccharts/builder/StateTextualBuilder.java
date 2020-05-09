/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

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

    private RootState rootState;

    public String buildString(RootState rootState) {
        clear();
        this.rootState = rootState;
        generateRootState();
        return build();
    }

    private void generateRootState() {
        append("scchart").append(rootState.getId());
        generateSuperState(rootState, true);
    }

    private void generateSuperState(SuperState superState, boolean generateInputOutput) {

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
    }

    private void generateRegularState(State state) {
        appendLine("");
        generateStateId(state);

        // binding state
        if (state instanceof BindingState) {
            var bindingState = (BindingState)state;
            append("is").append(bindingState.getFunctionName());
            if ((bindingState.getArguments() != null && bindingState.getArguments().size() > 0)
                    || bindingState.getReturnRef() != null) {

                append("(");
                String argsText = null;
                if (bindingState.getArguments() != null) {
                    argsText = bindingState.getArguments().stream().map(this::generateExpression).collect(Collectors.joining(","));
                    append(argsText);
                }

                if (bindingState.getReturnRef() != null) {
                    if (argsText != null && !argsText.isBlank()) {
                        append(",");
                    }

                    append(generateExpression(bindingState.getReturnRef()));
                }

                append(")");
            }
        }

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

        append(decl.formatDataType());
        append(decl.getName());
        if (decl.isArray()) {
            append("[" + decl.getCardinality() + "]");
        }
    }

    private void generateTransition(Transition transition) {
        appendLine("");
        if (transition.getTransitionType() == TransitionType.Immediate || transition.getTransitionType() == TransitionType.AbortTo) {
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
            strBuilder.append(assignmentEffect.getVarDeclaration().getName());

            // array index
            if (assignmentEffect.getIndexExpr() != null) {
                strBuilder.append("[");
                strBuilder.append(generateExpression(assignmentEffect.getIndexExpr()));
                strBuilder.append("]");
            }

            strBuilder.append(" = ");
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

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

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;
import de.uniba.swt.dsl.validation.util.ExprUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExpressionTextualBuilder extends TextualBuilder {

    public String buildString(Expression expression) {
        clear();
        generateExpr(expression);
        return build();
    }

    private void generateExpr(Expression expression) {
        if (expression instanceof OpExpression) {
            generateExpr((OpExpression) expression);
        }
    }

    private void generateExpr(OpExpression expression) {
        // primary
        if (expression instanceof PrimaryExpr) {
            generateExpr((PrimaryExpr) expression);
            return;
        }

        // op again
        generateExpr(expression.getLeftExpr());
        generateOp(expression.getOp());

        if (expression.getRightExpr() != null) {
            generateExpr(expression.getRightExpr());
        }
    }

    /**
     * UnaryExpr
     * | ParenthesizedExpr
     * | LiteralExpr
     * | ValuedReferenceExpr
     * | FunctionCallExpr
     * @param expression expression
     */
    private void generateExpr(PrimaryExpr expression) {
        // UnaryExpr
        if (expression instanceof UnaryExpr) {
            append("!");
            generateExpr(((UnaryExpr) expression).getExpr());
            return;
        }

        // ParenthesizedExpr
        if (expression instanceof ParenthesizedExpr) {
            append("(");
            generateExpr(((ParenthesizedExpr) expression).getExpr());
            append(")");
            return;
        }

        // ArrayLiteralExpr
        if (expression instanceof ArrayLiteralExpr) {
            generateExprArrayLiteralExpr((ArrayLiteralExpr) expression);
            return;
        }

        // LiteralExpr
        if (expression instanceof LiteralExpr) {
            generateExpr((LiteralExpr)expression);
            return;
        }

        // ValuedReferenceExpr
        if (expression instanceof ValuedReferenceExpr) {
            ValuedReferenceExpr referenceExpr = (ValuedReferenceExpr) expression;
            append(((ValuedReferenceExpr) expression).getDecl().getName());

            // index
            if (referenceExpr.getIndexExpr() != null) {
                append("[");
                generateExpr(referenceExpr.getIndexExpr());
                append("]");
            }
        }

        // ExternalFunctionCallExprImpl
        if (expression instanceof ExternalFunctionCallExpr) {
            ExternalFunctionCallExpr externExpr = (ExternalFunctionCallExpr) expression;
            append(externExpr.getName());
            append("(");
            if (externExpr.getParams().size() > 0) {
                for (int i = 0; i < externExpr.getParams().size(); i++) {
                    generateExpr(externExpr.getParams().get(i));
                    if (i < externExpr.getParams().size() - 1) {
                        append(",");
                    }
                }
            }
            append(")");
        }
    }

    private void generateExprArrayLiteralExpr(ArrayLiteralExpr expression) {
        append("{");
        if (expression.getArrExprs().size() > 0) {
            for (int i = 0; i < expression.getArrExprs().size(); i++) {
                generateExpr(expression.getArrExprs().get(i));
                if (i < expression.getArrExprs().size() - 1) {
                    append(",");
                }
            }
        }
        append("}");
    }

    /**
     * BooleanLiteral
     * 	| NumberLiteral
     * 	| HexLiteral
     * 	| StringLiteral
     * 	| NullLiteral
     * 	| PointAspectLiteral
     * @param expr expression
     */
    private void generateExpr(LiteralExpr expr) {
        if (expr instanceof BooleanLiteral) {
            append(((BooleanLiteral) expr).isBoolValue() ? "true" : "false");
            return;
        }

        if (expr instanceof NumberLiteral) {
            double val = ((NumberLiteral) expr).getValue();
            if (ExprUtil.isInteger(val)) {
                append(String.valueOf((int)val));
            } else {
                append(String.valueOf(val));
            }
            return;
        }

        if (expr instanceof StringLiteral) {
            String value = ((StringLiteral) expr).getValue();
            append("\"" + value + "\"");
        }
    }

    private void generateOp(OperatorType op) {
        append(op.toString());
    }
}

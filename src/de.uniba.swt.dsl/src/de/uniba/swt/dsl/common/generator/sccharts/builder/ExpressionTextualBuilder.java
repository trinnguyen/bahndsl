package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;
import de.uniba.swt.dsl.validation.util.ExprUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExpressionTextualBuilder extends TextualBuilder {

    @Inject
    ParamBindingTable paramBindingTable;

    public String buildString(List<LiteralExpr> arrExprs) {
        clear();
        append("{");
        if (arrExprs.size() > 0) {
            for (int i = 0; i < arrExprs.size(); i++) {
                generateExpr(arrExprs.get(i));
                if (i < arrExprs.size() - 1) {
                    append(",");
                }
            }
        }
        append("}");
        return build();
    }

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

        // LiteralExpr
        if (expression instanceof LiteralExpr) {
            generateExpr((LiteralExpr)expression);
            return;
        }

        // ValuedReferenceExpr
        if (expression instanceof ValuedReferenceExpr) {
            ValuedReferenceExpr referenceExpr = (ValuedReferenceExpr) expression;
            append(paramBindingTable.lookupBindingName(((ValuedReferenceExpr) expression).getDecl().getName()));

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

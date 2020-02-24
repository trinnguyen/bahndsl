package de.uniba.swt.dsl.validation.typing;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.util.ExprUtil;
import de.uniba.swt.dsl.validation.util.OperatorTypeHelper;

class TypeComputingHelper {
    public static ExprDataType computeDataType(Expression expr, HintDataType hintType) {
        // PrimaryExpr
        if (expr instanceof PrimaryExpr) {

            // LiteralExpr
            if (expr instanceof LiteralExpr) {
                return computeLiteralExprDataType((LiteralExpr)expr, hintType);
            }

            // UnaryExpr or ParenthesizedExpr
            if (expr instanceof UnaryExpr) {
                return computeDataType(((UnaryExpr) expr).getExpr(), hintType);
            }
            if (expr instanceof ParenthesizedExpr) {
                return computeDataType(((ParenthesizedExpr) expr).getExpr(), hintType);
            }

            // ValuedReferenceExpr
            if (expr instanceof ValuedReferenceExpr) {
                return computeValuedReferenceExpr((ValuedReferenceExpr) expr);
            }

            // FunctionCallExpr
            if (expr instanceof FunctionCallExpr) {
                return computeFunctionCallDataType((FunctionCallExpr)expr);
            }
        }

        // OpExpression: ensure both side having the same type
        if (expr instanceof OpExpression) {
            return computeOpExpression((OpExpression) expr);
        }

        throw new RuntimeException("Unable to compute data type for expression: " + expr);
    }

    public static ExprDataType computeOpExpression(OpExpression opExpr) {
        if (opExpr.getLeftExpr() != null || opExpr.getRightExpr() != null)
            return computeOpExpressionDataType(opExpr);

        return null;
    }

    public static ExprDataType computeValuedReferenceExpr(ValuedReferenceExpr referenceExpr) {
        var dataType = getDataType(referenceExpr.getDecl());
        if (dataType.isArray() && referenceExpr.getIndexExpr() != null) {
            dataType.setArray(false);
        }

        return dataType;
    }

    public static ExprDataType getDataType(RefVarDecl decl) {
        return new ExprDataType(decl.getType(), decl.isArray());
    }

    private static ExprDataType computeLiteralExprDataType(LiteralExpr expr, HintDataType hintType) {
        if (expr instanceof BooleanLiteral) {
            return ExprDataType.ScalarBool;
        }

        if (expr instanceof NumberLiteral) {
            if (hintType == HintDataType.INT)
                return ExprDataType.ScalarInt;

            if (hintType == HintDataType.FLOAT)
                return ExprDataType.ScalarFloat;

            // auto suggesting int
            double value = ((NumberLiteral) expr).getValue();
            if (ExprUtil.isInteger(value))
                return ExprDataType.ScalarInt;

            return ExprDataType.ScalarFloat;
        }

        if (expr instanceof HexLiteral) {
            return ExprDataType.ScalarInt;
        }

        if (expr instanceof StringLiteral) {
            return ExprDataType.ScalarString;
        }

        return null;
    }

    private static ExprDataType computeOpExpressionDataType(OpExpression expr) {
        OperatorType op = expr.getOp();

        if (OperatorTypeHelper.isArithmeticOp(op)) {

            // cast to int or float based on the member of operator
            ExprDataType leftDataType = null;
            ExprDataType rightDataType = null;

            if (expr.getLeftExpr() instanceof ValuedReferenceExpr) {
                leftDataType = computeValuedReferenceExpr((ValuedReferenceExpr)expr.getLeftExpr());
            }

            if (expr.getRightExpr() instanceof ValuedReferenceExpr) {
                rightDataType = computeValuedReferenceExpr((ValuedReferenceExpr)expr.getRightExpr());
            }

            // float has higher priority than int
            if (leftDataType != null && leftDataType.getDataType() == DataType.FLOAT_TYPE)
                return leftDataType;

            if (rightDataType != null && rightDataType.getDataType() == DataType.FLOAT_TYPE)
                return rightDataType;

            // pick from one of them
            var singleDataType = leftDataType != null ? leftDataType : rightDataType;
            if (singleDataType != null)
                return singleDataType;

            return ExprDataType.ScalarFloat;
        }

        return ExprDataType.ScalarBool;
    }

    private static ExprDataType computeFunctionCallDataType(FunctionCallExpr expr) {
        FuncDecl decl = expr.getDecl();
        if (decl.isReturn())
            return new ExprDataType(decl.getReturnType(), decl.isReturnArray());

        return ExprDataType.Void;
    }
}

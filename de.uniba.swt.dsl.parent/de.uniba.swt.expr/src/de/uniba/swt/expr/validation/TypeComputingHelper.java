package de.uniba.swt.expr.validation;

import de.uniba.swt.expr.bahnexpr.*;

public class TypeComputingHelper {
    public static ExprDataType computeDataType(Expression expr) throws ValidationException {
        // PrimaryExpr
        if (expr instanceof PrimaryExpr) {

            // LiteralExpr
            if (expr instanceof LiteralExpr) {
                return computeLiteralExprDataType((LiteralExpr)expr);
            }

            // UnaryExpr or ParenthesizedExpr
            if (expr instanceof UnaryExpr) {
                return computeDataType(((UnaryExpr) expr).getExpr());
            }
            if (expr instanceof ParenthesizedExpr) {
                return computeDataType(((ParenthesizedExpr) expr).getExpr());
            }

            // ValuedReferenceExpr
            if (expr instanceof ValuedReferenceExpr) {
                return getDataType(((ValuedReferenceExpr) expr).getDecl());
            }

            // FunctionCallExpr
            if (expr instanceof FunctionCallExpr) {
                return computeFunctionCallDataType((FunctionCallExpr)expr);
            }
        }

        // OpExpression: ensure both side having the same type
        if (expr instanceof OpExpression) {
            OpExpression opExpr = (OpExpression) expr;
            if (opExpr.getLeftExpr() != null || opExpr.getRightExpr() != null)
                return computeOpExpressionDataType((OpExpression)expr);
        }

        throw new RuntimeException("Unable to compute data type for expression: " + expr);
    }

    public static ExprDataType getDataType(RefVarDecl decl) {
        return new ExprDataType(decl.getType(), decl.isArray());
    }

    private static ExprDataType computeLiteralExprDataType(LiteralExpr expr) {
        if (expr instanceof BooleanLiteral) {
            return ExprDataType.ScalarBool;
        }

        //FIXME support float type
        if (expr instanceof NumberLiteral) {
            return ExprDataType.ScalarInt;
        }

        if (expr instanceof HexLiteral) {
            return ExprDataType.ScalarInt;
        }

        if (expr instanceof StringLiteral) {
            return ExprDataType.ScalarString;
        }

        //FIXME data type for object and NullLiteral
        return null;
    }

    private static ExprDataType computeOpExpressionDataType(OpExpression expr) {
        OperatorType op = expr.getOp();
        if (OperatorTypeHelper.isArithmeticOp(op)) {
            return ExprDataType.ScalarInt;
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

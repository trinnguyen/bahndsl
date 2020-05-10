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

package de.uniba.swt.dsl.validation.typing;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.util.ExprUtil;
import de.uniba.swt.dsl.validation.util.OperatorTypeHelper;

class TypeComputingHelper {
    public static ExprDataType computeExpr(Expression expr, HintDataType hintType) {
        // PrimaryExpr
        if (expr instanceof PrimaryExpr) {

            // LiteralExpr
            if (expr instanceof LiteralExpr) {
                return computeLiteralExprDataType((LiteralExpr)expr, hintType);
            }

            // UnaryExpr or ParenthesizedExpr
            if (expr instanceof UnaryExpr) {
                return computeExpr(((UnaryExpr) expr).getExpr(), hintType);
            }
            if (expr instanceof ParenthesizedExpr) {
                return computeExpr(((ParenthesizedExpr) expr).getExpr(), hintType);
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
            OpExpression opExpr = (OpExpression) expr;

            // binary operations
            if (opExpr.getLeftExpr() != null || opExpr.getRightExpr() != null)
                return computeOpExpression(opExpr, hintType);
        }

        if (expr instanceof ArrayLiteralExpr) {
            var arrExpr = (ArrayLiteralExpr) expr;
            if (arrExpr.getArrExprs().size() > 0) {
                var itemType = computeExpr(arrExpr.getArrExprs().get(0), HintDataType.NONE);
                return new ExprDataType(itemType.getDataType(), true);
            }
        }

        throw new RuntimeException("Unable to compute data type for expression: " + expr);
    }

    public static ExprDataType computeValuedReferenceExpr(ValuedReferenceExpr referenceExpr) {
        var dataType = getDataType(referenceExpr.getDecl());
        if (dataType.isArray()) {
            if (referenceExpr.isLength()) {
                return ExprDataType.ScalarInt;
            }

            if (referenceExpr.getIndexExpr() != null) {
                dataType.setArray(false);
            }
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

        if (expr instanceof StringLiteral) {
            return ExprDataType.ScalarString;
        }

        return null;
    }

    private static ExprDataType computeOpExpression(OpExpression expr, HintDataType hintType) {
        OperatorType op = expr.getOp();

        if (OperatorTypeHelper.isArithmeticOp(op)) {

            // cast to int or float based on the member of operator
            ExprDataType leftDataType = null;
            ExprDataType rightDataType = null;

            if (expr.getLeftExpr() != null) {
                leftDataType = computeExpr(expr.getLeftExpr(), hintType);
            }

            if (expr.getRightExpr() != null) {
                rightDataType = computeExpr(expr.getRightExpr(), hintType);
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
        // BehaviourExpr
        if (expr instanceof BehaviourExpr) {

            BehaviourExpr behaviourExpr = (BehaviourExpr) expr;

            if (behaviourExpr instanceof BehaviourGetExpr) {
                BehaviourSubGetExpr getter = ((BehaviourGetExpr) expr).getGetExpr();
                if (getter instanceof GetConfigFuncExpr) {
                    var prop = ((GetConfigFuncExpr) getter).getProp();
                    boolean isArray = prop.isArray();
                    switch (prop.getType()) {
                        case INT_TYPE:
                            return isArray ? ExprDataType.ArrayInt : ExprDataType.ScalarInt;
                        case FLOAT_TYPE:
                            return isArray ? ExprDataType.ArrayFloat : ExprDataType.ScalarFloat;
                        case BOOLEAN_TYPE:
                            return isArray ? ExprDataType.ArrayFloat : ExprDataType.ScalarBool;
                        case STRING_TYPE:
                            return isArray ? ExprDataType.ArrayString : ExprDataType.ScalarString;
                    }
                }

                if (getter instanceof GetPointPositionFuncExpr) {
                    return ExprDataType.ScalarString;
                }

                if (getter instanceof GetRoutesFuncExpr) {
                    return ExprDataType.ArrayString;
                }

                if (getter instanceof GetTrainSpeedFuncExpr) {
                    return ExprDataType.ScalarInt;
                }

                if (getter instanceof GetTrackStateFuncExpr) {
                    return ExprDataType.ScalarString;
                }
            }

            if (behaviourExpr instanceof BehaviourSetExpr) {
                var setter = ((BehaviourSetExpr) behaviourExpr).getSetExpr();

                if (setter instanceof SetConfigFuncExpr) {
                    return ExprDataType.ScalarBool;
                }

                if (setter instanceof SetTrainSpeedFuncExpr) {
                    return ExprDataType.ScalarBool;
                }

                if (setter instanceof SetTrackStateFuncExpr) {
                    return ExprDataType.ScalarBool;
                }
            }

            if (behaviourExpr instanceof GrantRouteFuncExpr) {
                return ExprDataType.ScalarBool;
            }

            if (behaviourExpr instanceof EvaluateFuncExpr) {
                return ExprDataType.ScalarBool;
            }
        }

        if (expr instanceof RegularFunctionCallExpr) {
            var reg = (RegularFunctionCallExpr) expr;
            var decl = reg.getDecl();
            if (reg.getDecl() != null) {
                if (decl.isReturn()) {
                    return new ExprDataType(decl.getReturnType(), decl.isReturnArray());
                }

                return ExprDataType.Void;
            }
        }

        throw new RuntimeException("Unable to compute data type for function: " + expr);
    }
}

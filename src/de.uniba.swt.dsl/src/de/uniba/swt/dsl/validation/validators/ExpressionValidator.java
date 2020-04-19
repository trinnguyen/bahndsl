package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.typing.HintDataType;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;
import de.uniba.swt.dsl.validation.util.ExprUtil;
import de.uniba.swt.dsl.validation.util.OperatorTypeHelper;
import de.uniba.swt.dsl.validation.util.ValidationException;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpressionValidator {

    @Inject
    TypeCheckingTable typeCheckingTable;

    /**
     * validate an expression
     * @param expr expr
     */
    public void validate(Expression expr) throws ValidationException {
        // PrimaryExpr
        if (expr instanceof PrimaryExpr) {
            // UnaryExpr or ParenthesizedExpr
            if (expr instanceof UnaryExpr) {
                validate(((UnaryExpr) expr).getExpr());
            }
            if (expr instanceof ParenthesizedExpr) {
                validate(((ParenthesizedExpr) expr).getExpr());
            }

            // ValuedReferenceExpr
            if (expr instanceof ValuedReferenceExpr) {
                validateValuedReferenceExpr((ValuedReferenceExpr)expr);
            }

            // FunctionCallExpr
            if (expr instanceof RegularFunctionCallExpr) {
                validateRegularFuncCall((RegularFunctionCallExpr)expr);
            }
        }

        // OpExpression: ensure both side having the same type
        if (expr instanceof OpExpression) {
            validateOpExpression((OpExpression)expr);
        }
    }

    private void validateValuedReferenceExpr(ValuedReferenceExpr expr) throws ValidationException {
        if (expr.getIndexExpr() != null) {
            if (!expr.getDecl().isArray())
                throw new ValidationException(String.format("Invalid value reference. Variable %s is not an array", expr.getDecl().getName()), BahnPackage.Literals.VALUED_REFERENCE_EXPR__INDEX_EXPR);

            var indexType = typeCheckingTable.computeDataType(expr.getIndexExpr(), HintDataType.INT);
            if (indexType.getDataType() != DataType.INT_TYPE) {
                throw new ValidationException("Type Error: Expected int", BahnPackage.Literals.OP_EXPRESSION__LEFT_EXPR);
            }
        }
    }

    private void validateRegularFuncCall(RegularFunctionCallExpr expr) throws ValidationException {
        var paramDecls = expr.getDecl().getParamDecls();

        if (paramDecls.size() != expr.getParams().size()) {
            String msg = String.format("Expected %d arguments but found %d", paramDecls.size(), expr.getParams().size());
            throw new ValidationException(msg, BahnPackage.Literals.REGULAR_FUNCTION_CALL_EXPR__PARAMS);
        }

        for (int i = 0; i < expr.getParams().size(); i++) {
            var argType = typeCheckingTable.computeDataType(expr.getParams().get(i));
            var decl = paramDecls.get(i);
            ensureTypeMatched(new ExprDataType(decl.getType(), decl.isArray()), argType, BahnPackage.Literals.REGULAR_FUNCTION_CALL_EXPR__PARAMS);
        }
    }

    /**
     * Ensure both side of op expressions having the same types
     * @param opExpr expression
     * @throws ValidationException exception
     */
    private void validateOpExpression(OpExpression opExpr) throws ValidationException {
        ExprDataType dataTypeLeft = null;
        if (opExpr.getLeftExpr() != null) {
            dataTypeLeft = typeCheckingTable.computeDataType(opExpr.getLeftExpr());
        }

        ExprDataType dataTypeRight = null;
        if (opExpr.getRightExpr() != null) {
            dataTypeRight = typeCheckingTable.computeDataType(opExpr.getRightExpr());
        }

        // ensure same type
        if (dataTypeLeft != null && dataTypeRight != null) {

            // validate op data type
            ExprDataType[] dataTypes = getChildOpDataTypes(opExpr.getOp());
            if (!ExprUtil.arrayContains(dataTypes, dataTypeLeft)) {
                throw ValidationException.createTypeException(getExpectedTypes(dataTypes), dataTypeLeft.displayTypeName(), BahnPackage.Literals.OP_EXPRESSION__LEFT_EXPR);
            }

            if (!ExprUtil.arrayContains(dataTypes, dataTypeRight)) {
                throw ValidationException.createTypeException(getExpectedTypes(dataTypes), dataTypeRight.displayTypeName(), BahnPackage.Literals.OP_EXPRESSION__RIGHT_EXPR);
            }

            if (!Objects.equals(dataTypeLeft, dataTypeRight)) {
                if (dataTypeLeft.isScalarNumber() && dataTypeRight.isScalarNumber()) {
                    return;
                }

                throw new ValidationException("Type Error: Expressions must have the same type. Actual: " + dataTypeLeft.displayTypeName() + " and " + dataTypeRight.displayTypeName(), BahnPackage.Literals.OP_EXPRESSION__RIGHT_EXPR);
            }
        }
    }

    private static ExprDataType[] getChildOpDataTypes(OperatorType op) {

        if (OperatorTypeHelper.isArithmeticOp(op) || OperatorTypeHelper.isRelationalOp(op)) {
            return new ExprDataType[]{ ExprDataType.ScalarInt, ExprDataType.ScalarFloat};
        }

        if (OperatorTypeHelper.isLogicalOp(op))
            return new ExprDataType[]{ ExprDataType.ScalarBool};

        return new ExprDataType[]{ ExprDataType.ScalarInt, ExprDataType.ScalarFloat, ExprDataType.ScalarBool, ExprDataType.ScalarString};
    }

    private static String getExpectedTypes(ExprDataType[] types) {
        if (types == null || types.length == 0)
            return null;

        return Arrays.stream(types).map(ExprDataType::displayTypeName).collect(Collectors.joining(" or "));
    }

    private void ensureTypeMatched(ExprDataType expectedType, ExprDataType actualType, EStructuralFeature feature) throws ValidationException {
        if (!expectedType.equals(actualType)) {
            throw ValidationException.createTypeException(expectedType, actualType, feature);
        }
    }
}



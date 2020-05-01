package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;

import java.util.Collection;
import java.util.List;

public class StringEqualNormalizer extends AbstractNormalizer {

    public final static String ExternStringEqualsFuncName = "string_equals";

    // public final static String ExternStringConcatFuncName = "string_concat";

    @Inject
    TypeCheckingTable typeCheckingTable;

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        if (expr instanceof OpExpression) {
            var opExpr = (OpExpression) expr;
            if (opExpr.getLeftExpr() != null
                    && opExpr.getRightExpr() != null) {
                var typeLeft = typeCheckingTable.computeDataType(opExpr.getLeftExpr());
                var typeRight = typeCheckingTable.computeDataType(opExpr.getLeftExpr());

                // verify 2 sides are string
                if (typeLeft != null && typeLeft.isScalarString() && typeRight != null && typeRight.isScalarString()) {

                    String externName = getExternName(opExpr.getOp());
                    if (externName != null && !externName.isBlank()) {
                        PrimaryExpr externExpr = SyntacticTransformer.createExternalFunctionCallExpr(externName, List.of(opExpr.getLeftExpr(), opExpr.getRightExpr()));

                        // create unary if not equal
                        if (opExpr.getOp() == OperatorType.NOT_EQUAL) {
                            var unaryExpr = BahnFactory.eINSTANCE.createUnaryExpr();
                            unaryExpr.setExpr(externExpr);
                            externExpr = unaryExpr;
                        }

                        // replace
                        BahnUtil.replaceEObject(expr, externExpr);

                        return List.of();
                    }

                }
            }
        }

        return null;
    }

    private String getExternName(OperatorType op) {
        if (op == OperatorType.EQUAL || op == OperatorType.NOT_EQUAL) {
            return ExternStringEqualsFuncName;
        }

//        if (op == OperatorType.PLUS) {
//            return ExternStringConcatFuncName;
//        }

        return null;
    }
}

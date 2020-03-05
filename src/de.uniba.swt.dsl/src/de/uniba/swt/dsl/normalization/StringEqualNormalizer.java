package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;

import java.util.Collection;
import java.util.List;

public class StringEqualNormalizer extends AbstractNormalizer {

    @Inject
    TypeCheckingTable typeCheckingTable;

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        if (expr instanceof OpExpression) {
            var opExpr = (OpExpression) expr;
            if (opExpr.getLeftExpr() != null
                    && opExpr.getRightExpr() != null
                    && (opExpr.getOp() == OperatorType.EQUAL || opExpr.getOp() == OperatorType.NOT_EQUAL)) {
                var type = typeCheckingTable.computeDataType(opExpr.getLeftExpr());
                if (type != null && type.isScalarString()) {
                    PrimaryExpr externExpr = SyntacticTransformHelper.createExternalFunctionCallExpr("string_equals", List.of(opExpr.getLeftExpr(), opExpr.getRightExpr()));
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

        return null;
    }
}

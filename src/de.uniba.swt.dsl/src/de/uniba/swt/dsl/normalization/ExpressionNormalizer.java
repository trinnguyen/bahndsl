package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

import java.util.Collection;
import java.util.List;

public class ExpressionNormalizer extends AbstractNormalizer {

    @Inject
    SyntacticSugarNormalizer syntacticSugarNormalizer;

    @Inject
    TemporaryVarGenerator varGenerator;

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        if (expr instanceof BehaviourExpr) {
            boolean isSetter = syntacticSugarNormalizer.isSetter((BehaviourExpr)expr);
            Expression replacementExpr = syntacticSugarNormalizer.normalizeBehaviourExpr((BehaviourExpr)expr);
            if (replacementExpr != null) {

                BahnUtil.replaceEObject(expr, replacementExpr);

                // add temporary bool for setter
                if (replacementExpr.eContainer() instanceof FunctionCallStmt && isSetter) {
                    var callStmt = (FunctionCallStmt) replacementExpr.eContainer();
                    var stmt = createVarDeclStmt(varGenerator.createTempVar(ExprDataType.ScalarBool), replacementExpr);
                    BahnUtil.replaceEObject(callStmt, stmt);
                }

                return List.of();
            }
        }

        return null;
    }
}

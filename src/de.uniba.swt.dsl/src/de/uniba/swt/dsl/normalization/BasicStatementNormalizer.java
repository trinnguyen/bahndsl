package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicStatementNormalizer extends AbstractNormalizer {

    @Inject
    TemporaryVarGenerator varGenerator;

    @Override
    protected Collection<Statement> normalizeStmt(Statement stmt) {

        // intercept assign temporary variable for regular function call
        if (stmt instanceof FunctionCallStmt) {
            var expr = (FunctionCallExpr) ((FunctionCallStmt) stmt).getExpr();
            if (expr instanceof RegularFunctionCallExpr) {
                var decl = ((RegularFunctionCallExpr) expr).getDecl();
                if (decl.isReturn()) {
                    VarDecl temp = varGenerator.createTempVar(new ExprDataType(decl.getReturnType(), decl.isReturnArray()));
                    var replaceStmt = createVarDeclStmt(temp, expr);
                    BahnUtil.replaceEObject(stmt, replaceStmt);
                    return List.of();
                }
            }
        }

        return super.normalizeStmt(stmt);
    }

    @Override
    protected List<Statement> processExpr(Expression expr) {
        if (expr instanceof RegularFunctionCallExpr) {
            return normalizeRegularFunction((RegularFunctionCallExpr) expr);
        }

        return null;
    }

    private List<Statement> normalizeRegularFunction(RegularFunctionCallExpr funcExpr) {
        // process all params
        var normalizedParams = normalizeExprs(funcExpr.getParams());

        // stop if direct assignment
        boolean stop = funcExpr.eContainer() instanceof VariableAssignment
                || funcExpr.eContainer() instanceof FunctionCallStmt;
        if (stop) {
            return normalizedParams;
        }

        // create variable reference
        VarDecl temp = varGenerator.createTempVar(new ExprDataType(funcExpr.getDecl().getReturnType(), funcExpr.getDecl().isReturnArray()));
        ValuedReferenceExpr ref = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        ref.setDecl(temp);

        // update
        BahnUtil.replaceEObject(funcExpr, ref);

        // return
        List<Statement> result = new ArrayList<>();
        if (normalizedParams != null) {
            result.addAll(normalizedParams);
        }
        result.add(createVarDeclStmt(temp, funcExpr));

        return result;
    }
}


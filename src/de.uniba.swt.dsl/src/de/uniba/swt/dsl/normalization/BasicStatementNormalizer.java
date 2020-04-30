package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicStatementNormalizer extends AbstractNormalizer {

    @Inject
    TemporaryVarGenerator temporaryVarGenerator;

    @Inject
    TypeCheckingTable typeCheckingTable;

    @Override
    protected Collection<Statement> normalizeStmt(Statement stmt) {

        // intercept assign temporary variable for regular function call
        if (stmt instanceof FunctionCallStmt) {
            var expr = (FunctionCallExpr) ((FunctionCallStmt) stmt).getExpr();
            if (expr instanceof RegularFunctionCallExpr) {
                var decl = ((RegularFunctionCallExpr) expr).getDecl();
                if (decl.isReturn()) {
                    VarDecl temp = temporaryVarGenerator.createTempVar(new ExprDataType(decl.getReturnType(), decl.isReturnArray()));
                    var replaceStmt = createVarDeclStmt(temp, expr);
                    BahnUtil.replaceEObject(stmt, replaceStmt);
                    return List.of();
                }
            }
        }

        // ensure foreach using temporary array
        if (stmt instanceof ForeachStmt) {
            var foreachStmt = (ForeachStmt) stmt;
            if (!(foreachStmt.getArrayExpr() instanceof ValuedReferenceExpr)) {

                // introduce new temporary for variable
                var type = typeCheckingTable.computeDataType(foreachStmt.getArrayExpr());
                VarDecl tempArrayVar = temporaryVarGenerator.createTempVar(type);

                // keep
                var tempArrayVarDeclStmt = createVarDeclStmt(tempArrayVar, foreachStmt.getArrayExpr());

                //replace
                foreachStmt.setArrayExpr(createVarRef(tempArrayVar));

                // return
                return List.of(tempArrayVarDeclStmt);
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
        VarDecl temp = temporaryVarGenerator.createTempVar(new ExprDataType(funcExpr.getDecl().getReturnType(), funcExpr.getDecl().isReturnArray()));
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


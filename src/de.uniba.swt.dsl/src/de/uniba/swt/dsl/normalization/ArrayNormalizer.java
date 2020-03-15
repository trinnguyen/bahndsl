package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;

import java.util.Collection;
import java.util.List;

public class ArrayNormalizer extends AbstractNormalizer {

    @Inject
    ArrayLookupTable arrayLookupTable;

    @Override
    public void normalizeFunc(FuncDecl funcDecl) {
        // update params
        int i = 0;
        while (i < funcDecl.getParamDecls().size()) {
            var paramDecl = funcDecl.getParamDecls().get(i);
            if (paramDecl.isArray()) {
                // add to lookup
                arrayLookupTable.insert(paramDecl);

                // add the size param to the list
                ParamDecl lenParamDecl = BahnFactory.eINSTANCE.createParamDecl();
                lenParamDecl.setArray(false);
                lenParamDecl.setType(DataType.INT_TYPE);
                lenParamDecl.setName(arrayLookupTable.lookupLengthName(paramDecl.getName()));

                // add to list
                funcDecl.getParamDecls().add(++i, lenParamDecl);
            }

            i++;
        }

        super.normalizeFunc(funcDecl);
    }

    @Override
    protected Collection<Statement> normalizeStmt(Statement stmt) {

        if (stmt instanceof VarDeclStmt) {
            VarDeclStmt varDeclStmt = (VarDeclStmt) stmt;
            if (varDeclStmt.getDecl().isArray()) {
                String name = varDeclStmt.getDecl().getName();

                // insert
                arrayLookupTable.insert(varDeclStmt.getDecl());

                // add new stmt
                var lenDeclStmt = BahnFactory.eINSTANCE.createVarDeclStmt();
                lenDeclStmt.setDecl(arrayLookupTable.lookupLengthDecl(name));
                return List.of(lenDeclStmt);
            }
        }

        return super.normalizeStmt(stmt);
    }

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        if (expr instanceof ValuedReferenceExpr) {
            ValuedReferenceExpr refExpr = (ValuedReferenceExpr) expr;
            if (refExpr.isLength()) {

                // replace by a temp variable
                var lenExpr = arrayLookupTable.createLenVarExpr(refExpr.getDecl().getName());
                BahnUtil.replaceEObject(expr, lenExpr);

                // stop
                return null;
            }
        }

        // add size param to regular function call
        if (expr instanceof RegularFunctionCallExpr) {
            var regularFunctionCallExpr = (RegularFunctionCallExpr) expr;
            int i = 0;
            while (i < regularFunctionCallExpr.getParams().size()) {
                var param = regularFunctionCallExpr.getParams().get(i);
                if (param instanceof ValuedReferenceExpr) {
                    var refParam = (ValuedReferenceExpr) param;
                    if (refParam.getDecl().isArray()) {

                        // add the size to the list
                        regularFunctionCallExpr.getParams().add(++i, arrayLookupTable.createLenVarExpr(refParam.getDecl().getName()));
                    }
                }

                i++;
            }
        }

        return null;
    }
}

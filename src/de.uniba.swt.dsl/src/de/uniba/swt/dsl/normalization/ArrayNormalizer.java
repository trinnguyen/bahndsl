/*
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BahnDSL.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 */

package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
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

                // add the size param to the list
                ParamDecl lenParamDecl = BahnFactory.eINSTANCE.createParamDecl();
                lenParamDecl.setArray(false);
                lenParamDecl.setType(DataType.INT_TYPE);
                lenParamDecl.setName(arrayLookupTable.generateTempLenVar(paramDecl.getName()));

                // add to lookup
                arrayLookupTable.insert(paramDecl, lenParamDecl);

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

                // create  new var decl for int length
                VarDecl lenDecl = BahnFactory.eINSTANCE.createVarDecl();
                lenDecl.setType(DataType.INT_TYPE);
                lenDecl.setName(arrayLookupTable.generateTempLenVar(name));

                // insert
                arrayLookupTable.insert(varDeclStmt.getDecl(), lenDecl);

                // set default assignment
                VariableAssignment assignment = BahnFactory.eINSTANCE.createVariableAssignment();
                assignment.setExpr(BahnUtil.createNumLiteral(BahnConstants.DEFAULT_ARRAY_SIZE));

                // add new stmt
                var lenDeclStmt = BahnFactory.eINSTANCE.createVarDeclStmt();
                lenDeclStmt.setDecl(lenDecl);
                lenDeclStmt.setAssignment(assignment);

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

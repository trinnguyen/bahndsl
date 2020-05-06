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

package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

import java.util.Collection;
import java.util.List;

public class SyntacticExprNormalizer extends AbstractNormalizer {

    @Inject
    TemporaryVarGenerator varGenerator;

    @Inject
    SyntacticTransformer syntacticTransformHelper;

    @Inject
    ArrayLookupTable arrayLookupTable;

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        if (expr instanceof BehaviourExpr) {
            boolean isArrayGetter = syntacticTransformHelper.isArrayGetter((BehaviourExpr)expr);
            boolean isSetter = syntacticTransformHelper.isSetter((BehaviourExpr)expr);

            Expression replacementExpr = syntacticTransformHelper.normalizeBehaviourExpr((BehaviourExpr)expr);
            if (replacementExpr != null) {

                // update both getter and setter
                BahnUtil.replaceEObject(expr, replacementExpr);

                // add temporary bool for setter
                if (replacementExpr.eContainer() instanceof FunctionCallStmt && isSetter) {
                    var callStmt = (FunctionCallStmt) replacementExpr.eContainer();
                    var stmt = varGenerator.createTempVarStmt(ExprDataType.ScalarBool);
                    BahnUtil.assignExpression(stmt, replacementExpr);

                    BahnUtil.replaceEObject(callStmt, stmt);
                }

                // check getter of routes or config array
                if (isArrayGetter)
                    return processArrayGetter(replacementExpr);

                return List.of();
            }
        }

        return null;
    }

    private Collection<Statement> processArrayGetter(Expression expr) {
        if (expr.eContainer() instanceof VariableAssignment) {

            // remove assignment
            if (expr.eContainer().eContainer() instanceof VarDeclStmt) {
                var stmt = (VarDeclStmt) expr.eContainer().eContainer();

                // add new assignment for len
                var replacementStmt = BahnUtil.createAssignmentStmt(arrayLookupTable.lookupLengthDecl(stmt.getDecl().getName()), stmt.getAssignment().getExpr());

                // remove assignment from stmt
                stmt.setAssignment(null);
                if (stmt.eContainer() instanceof StatementList) {
                    var list = ((StatementList) stmt.eContainer()).getStmts();
                    int index = list.indexOf(stmt);
                    list.add(index + 1, replacementStmt);
                }

                return null;
            }

            // replace lhs
            if (expr.eContainer().eContainer() instanceof AssignmentStmt) {
                var stmt = (AssignmentStmt) expr.eContainer().eContainer();
                var arrName = stmt.getReferenceExpr().getDecl().getName();
                stmt.setReferenceExpr(arrayLookupTable.createLenVarExpr(arrName));
            }
        }

        return null;
    }
}

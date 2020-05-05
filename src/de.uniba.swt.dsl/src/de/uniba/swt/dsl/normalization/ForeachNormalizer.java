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

public class ForeachNormalizer extends AbstractNormalizer {

    @Inject
    TemporaryVarGenerator temporaryVarGenerator;

    @Inject
    ArrayLookupTable arrayLookupTable;

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        return null;
    }

    @Override
    protected Collection<Statement> normalizeStmt(Statement stmt) {

        if (stmt instanceof ForeachStmt) {
            var foreachStmt = (ForeachStmt) stmt;

            // ensure ref
            if (foreachStmt.getArrayExpr() instanceof ValuedReferenceExpr) {
                var arrRef = (ValuedReferenceExpr) foreachStmt.getArrayExpr();

                // index
                VarDeclStmt indexVarStmt = temporaryVarGenerator.createTempVarStmt(ExprDataType.ScalarInt);
                BahnUtil.assignExpression(indexVarStmt, createNumLiteral(0));

                // condition and current element
                Expression condition = createConditionExpr(indexVarStmt, arrRef.getDecl().getName());

                // update stmt list with start and stop
                VarDeclStmt elementStmt = BahnUtil.createVarDeclStmt(foreachStmt.getDecl(), createArrayIndexRef(arrRef.getDecl().getName(), indexVarStmt));
                var stmtList = foreachStmt.getStmts();
                stmtList.getStmts().add(0, elementStmt);

                // stop
                var indexIncreStmt = createIndexIncrementStmt(indexVarStmt);
                stmtList.getStmts().add(indexIncreStmt);

                // form while stmt
                var whileStmt = BahnFactory.eINSTANCE.createIterationStmt();
                whileStmt.setExpr(condition);
                whileStmt.setStmts(foreachStmt.getStmts());

                // replace
                BahnUtil.replaceEObject(foreachStmt, whileStmt);

                // additional decl stmt
                return List.of(indexVarStmt);
            }
        }

        return super.normalizeStmt(stmt);
    }

    private Statement createIndexIncrementStmt(VarDeclStmt declStmt) {
        // op
        var opExpr = BahnFactory.eINSTANCE.createOpExpression();
        opExpr.setLeftExpr(BahnUtil.createVarRef(declStmt));
        opExpr.setOp(OperatorType.PLUS);
        opExpr.setRightExpr(createNumLiteral(1));

        var assignmentVariable = BahnFactory.eINSTANCE.createVariableAssignment();
        assignmentVariable.setExpr(opExpr);

        var assignmentStmt = BahnFactory.eINSTANCE.createAssignmentStmt();
        assignmentStmt.setReferenceExpr(BahnUtil.createVarRef(declStmt));
        assignmentStmt.setAssignment(assignmentVariable);
        return assignmentStmt;
    }

    private Expression createArrayIndexRef(String name, VarDeclStmt declStmt) {
        var expr = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        expr.setDecl(arrayLookupTable.createArrayVarExpr(name).getDecl());
        expr.setIndexExpr(BahnUtil.createVarRef(declStmt));
        return expr;
    }

    private Expression createConditionExpr(VarDeclStmt declStmt, String name) {
        var expr = BahnFactory.eINSTANCE.createOpExpression();
        expr.setLeftExpr(BahnUtil.createVarRef(declStmt));
        expr.setOp(OperatorType.LESS);
        expr.setRightExpr(arrayLookupTable.createLenVarExpr(name));
        return expr;
    }

    private NumberLiteral createNumLiteral(int val) {
        var numLiteral = BahnFactory.eINSTANCE.createNumberLiteral();
        numLiteral.setValue(val);
        return numLiteral;
    }
}

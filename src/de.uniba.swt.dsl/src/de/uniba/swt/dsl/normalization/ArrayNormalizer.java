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
import com.google.inject.internal.util.$StackTraceElements;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayNormalizer extends AbstractNormalizer {

    @Inject
    ArrayLookupTable arrayLookupTable;

    @Inject
    TypeCheckingTable typeCheckingTable;

    @Override
    public void normalizeFunc(FuncDecl funcDecl) {
        // update params
        int i = 0;
        while (i < funcDecl.getParamDecls().size()) {
            var paramDecl = funcDecl.getParamDecls().get(i);
            if (paramDecl.isArray()) {

                // add to lookup
                arrayLookupTable.insert(paramDecl);

                // add to list
                var lenDecl = arrayLookupTable.lookupLengthDecl(paramDecl.getName());
                if (lenDecl instanceof ParamDecl)
                    funcDecl.getParamDecls().add(++i, (ParamDecl) lenDecl);
            }

            i++;
        }

        super.normalizeFunc(funcDecl);
    }

    @Override
    protected Collection<Statement> normalizeStmt(Statement stmt) {

        // add new stmt for len
        if (stmt instanceof VarDeclStmt) {
            VarDeclStmt varDeclStmt = (VarDeclStmt) stmt;
            var result = normalizeArrayVarDecl(varDeclStmt);
            if (result != null)
                return List.of(result);
        }

        if (stmt instanceof AssignmentStmt) {
            var assignStmt = (AssignmentStmt) stmt;
            var ref = assignStmt.getReferenceExpr();
            if (ref.getDecl().isArray() && !ref.isLength()) {
                // add new statement
                var lenAssign = BahnUtil.createAssignmentStmt(
                        arrayLookupTable.lookupLengthDecl(ref.getDecl().getName()),
                        BahnUtil.createNumLiteral(computeArrayLen(assignStmt.getAssignment())));
                return List.of(lenAssign);
            }
        }

        // ensure foreach using temporary array instead of vecotr
        if (stmt instanceof ForeachStmt) {
            return generateArrayVarIfNeeded(((ForeachStmt) stmt).getArrayExpr());
        }

        return super.normalizeStmt(stmt);
    }

    private Statement normalizeArrayVarDecl(VarDeclStmt varDeclStmt) {
        if (varDeclStmt.getDecl().isArray()) {
            // insert
            arrayLookupTable.insert(varDeclStmt.getDecl());

            // add new stmt
            var lenDecl = arrayLookupTable.lookupLengthDecl(varDeclStmt.getDecl().getName());
            if (lenDecl instanceof VarDecl) {

                // find len if initial with literal
                int len = computeArrayLen(varDeclStmt.getAssignment());

                return BahnUtil.createVarDeclStmt((VarDecl) lenDecl, BahnUtil.createNumLiteral(len));
            }
        }

        return null;
    }

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        // normalise .len property
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

            var callExpr = (RegularFunctionCallExpr) expr;
            List<Statement> stmts = generateArrayVarIfNeeded(callExpr.getParams());

            // add size param to function call after normalising the array vector
             addLengthParamIfNeeded(callExpr);

            // result
            if (stmts != null && stmts.size() > 0)
                return stmts;
        }

        return null;
    }

    private void addLengthParamIfNeeded(RegularFunctionCallExpr expr) {
        int i = 0;
        while (i < expr.getParams().size()) {
            var param = expr.getParams().get(i);
            if (param instanceof ValuedReferenceExpr) {
                var refParam = (ValuedReferenceExpr) param;
                if (refParam.getDecl().isArray()) {

                    // add the size to the list
                    expr.getParams().add(++i, arrayLookupTable.createLenVarExpr(refParam.getDecl().getName()));
                }
            }

            i++;
        }
    }

    private List<Statement> generateArrayVarIfNeeded(List<Expression> exprs) {
        return exprs.stream().map(this::generateArrayVarIfNeeded).filter(lst -> lst != null && lst.size() > 0).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private Collection<Statement> generateArrayVarIfNeeded(Expression expr) {
        if (expr instanceof ArrayLiteralExpr) {
            var arrayLiteralExpr = (ArrayLiteralExpr) expr;

            // introduce new temporary for variable
            var type = typeCheckingTable.computeDataType(arrayLiteralExpr);

            var tempArrayStmt = refactorUsingTemporaryVar(arrayLiteralExpr, type);

            // return
            var norLen = normalizeArrayVarDecl(tempArrayStmt);
            if (norLen != null)
                return List.of(norLen, tempArrayStmt);

            return List.of(tempArrayStmt);
        }

        return null;
    }


    private static int computeArrayLen(VariableAssignment assignment) {
        if (assignment != null && assignment.getExpr() != null) {
            return computeArrayLen(assignment.getExpr());
        }

        return 0;
    }

    private static int computeArrayLen(Expression expression) {
        if (expression instanceof ArrayLiteralExpr) {
            return ((ArrayLiteralExpr) expression).getArrExprs().size();
        }

        if (expression instanceof ParenthesizedExpr) {
            return computeArrayLen(((ParenthesizedExpr) expression).getExpr());
        }

        return 0;
    }
}

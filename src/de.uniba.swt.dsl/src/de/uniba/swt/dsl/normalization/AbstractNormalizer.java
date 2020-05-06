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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractNormalizer {

    @Inject
    TemporaryVarGenerator temporaryVarGenerator;

    public void normalizeFunc(FuncDecl funcDecl) {
        normalizeStmtList(funcDecl.getStmtList());
    }

    private void normalizeStmtList(StatementList list) {
        if (list == null)
            return;

        int index = 0;
        while (index < list.getStmts().size()) {
            Statement stmt = list.getStmts().get(index);

            var result = normalizeStmt(stmt);
            if (result != null) {
                list.getStmts().addAll(index, result);
                index += result.size();
            }

            index++;
        }
    }

    protected Collection<Statement> normalizeStmt(Statement stmt) {
        if (stmt instanceof SelectionStmt) {
            var selectionStmt = (SelectionStmt) stmt;
            normalizeStmtList(selectionStmt.getThenStmts());
            if (selectionStmt.getElseStmts() != null)
                normalizeStmtList(selectionStmt.getElseStmts());

            return normalizeExpr(selectionStmt.getExpr());
        }

        if (stmt instanceof ForeachStmt) {
            normalizeStmtList(((ForeachStmt) stmt).getStmts());
            return normalizeExpr(((ForeachStmt) stmt).getArrayExpr());
        }

        if (stmt instanceof IterationStmt) {
            normalizeStmtList(((IterationStmt) stmt).getStmts());
            return normalizeExpr(((IterationStmt) stmt).getExpr());
        }

        if (stmt instanceof VarDeclStmt) {
            return normalizeAssignment(((VarDeclStmt) stmt).getAssignment());
        }

        if (stmt instanceof AssignmentStmt) {
            return normalizeAssignment(((AssignmentStmt) stmt).getAssignment());
        }

        if (stmt instanceof FunctionCallStmt) {
            return normalizeExpr(((FunctionCallStmt) stmt).getExpr());
        }

        if (stmt instanceof ReturnStmt) {
            return normalizeExpr(((ReturnStmt) stmt).getExpr());
        }

        return null;
    }

    private Collection<Statement> normalizeAssignment(VariableAssignment assignment) {
        if (assignment != null) {
            return normalizeExpr(assignment.getExpr());
        }

        return null;
    }

    protected Collection<Statement> normalizeExpr(Expression expr) {

        // process
        var stmts = processExpr(expr);
        if (stmts != null)
            return stmts;

        // travel through the expression
        if (expr instanceof PrimaryExpr) {

            if (expr instanceof UnaryExpr) {
                return normalizeExpr(((UnaryExpr) expr).getExpr());
            }

            if (expr instanceof ParenthesizedExpr) {
                return normalizeExpr(((ParenthesizedExpr) expr).getExpr());
            }

            if (expr instanceof ValuedReferenceExpr) {
                return normalizeExpr(((ValuedReferenceExpr) expr).getIndexExpr());
            }

            if (expr instanceof FunctionCallExpr) {

                if (expr instanceof RegularFunctionCallExpr) {
                    return normalizeExprs(((RegularFunctionCallExpr) expr).getParams());
                }

                if (expr instanceof ExternalFunctionCallExpr) {
                    return normalizeExprs(((ExternalFunctionCallExpr) expr).getParams());
                }
            }

            return null;
        }

        if (expr instanceof OpExpression) {
            var opExpr = (OpExpression) expr;
            List<Expression> items = new ArrayList<>();
            if (opExpr.getLeftExpr() != null) {
                items.add(opExpr.getLeftExpr());
            }

            if (opExpr.getRightExpr() != null) {
                items.add(opExpr.getRightExpr());
            }

            return normalizeExprs(items);
        }

        return null;
    }

    protected List<Statement> normalizeExprs(List<Expression> exprs) {
        return exprs.stream().map(this::normalizeExpr).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    protected VarDeclStmt refactorUsingTemporaryVar(Expression expression, ExprDataType dataType) {
        VarDeclStmt temp = temporaryVarGenerator.createTempVarStmt(dataType);

        // create ref
        ValuedReferenceExpr ref = BahnUtil.createVarRef(temp);

        // update
        BahnUtil.replaceEObject(expression, ref);

        // set assignment to temp
        BahnUtil.assignExpression(temp, expression);

        return temp;
    }

    protected abstract Collection<Statement> processExpr(Expression expr);
}

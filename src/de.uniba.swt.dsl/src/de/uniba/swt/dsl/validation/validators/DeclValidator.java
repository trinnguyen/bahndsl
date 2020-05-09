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

package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.ValidationErrors;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.typing.HintDataTypeUtl;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;
import de.uniba.swt.dsl.validation.util.ValidationException;

import javax.inject.Inject;

public class DeclValidator {

    @Inject
    TypeCheckingTable typeCheckingTable;

    public void validateReturn(FuncDecl decl) throws ValidationException {
        if (decl.isReturn()) {
            // ensure having return statement
            var expectedType = new ExprDataType(decl.getReturnType(), decl.isReturnArray());
            if (!ensureReturnStmt(decl.getStmtList(), expectedType)) {
                throw new ValidationException(ValidationErrors.MissingReturn, BahnPackage.Literals.FUNC_DECL__RETURN_TYPE);
            }
        } else {
            // ensure having no return statement
            if (!ensureNoReturnStatement(decl.getStmtList())) {
                throw new ValidationException(ValidationErrors.UnexpectedReturn, BahnPackage.Literals.FUNC_DECL__STMT_LIST);
            }
        }
    }

    private boolean ensureNoReturnStatement(StatementList stmtList) {
        return stmtList.getStmts().stream().allMatch(s -> {
            if (s instanceof IterationStmt) {
                return ensureNoReturnStatement(((IterationStmt) s).getStmts());
            }

            if (s instanceof SelectionStmt) {
                SelectionStmt selectionStmt = (SelectionStmt)s;
                return ensureNoReturnStatement(selectionStmt.getThenStmts())
                        && (selectionStmt.getElseStmts() == null || ensureNoReturnStatement(selectionStmt.getElseStmts()));
            }

            return !(s instanceof ReturnStmt);
        });
    }

    private boolean ensureReturnStmt(StatementList stmtList, ExprDataType expectedType) throws ValidationException {
        for (var stmt : stmtList.getStmts()) {

            // require both branch of if having return
            if (stmt instanceof SelectionStmt) {
                SelectionStmt selectionStmt = (SelectionStmt) stmt;
                if (ensureReturnStmt(selectionStmt.getThenStmts(), expectedType)
                        && selectionStmt.getElseStmts() != null
                        && ensureReturnStmt(selectionStmt.getElseStmts(), expectedType)) {
                    return true;
                }
            }

            if (stmt instanceof ReturnStmt) {
                ExprDataType computedType = typeCheckingTable.computeDataType(((ReturnStmt) stmt).getExpr(), HintDataTypeUtl.from(expectedType.getDataType()));
                if (!typeCheckingTable.isValidType(computedType, expectedType)) {
                    throw ValidationException.createTypeException(expectedType, computedType, BahnPackage.Literals.FUNC_DECL__RETURN);
                }

                return true;
            }
        }

        return false;
    }
}

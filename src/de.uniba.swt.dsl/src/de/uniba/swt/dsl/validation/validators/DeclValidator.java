package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.typing.HintDataTypeUtl;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;
import de.uniba.swt.dsl.validation.util.ValidationException;

import javax.inject.Inject;

public class DeclValidator {

    @Inject
    TypeCheckingTable typeCheckingTable;

    public void validateFuncDecl(FuncDecl decl) throws ValidationException {
        if (decl.isReturn()) {
            // ensure having return statement
            var expectedType = new ExprDataType(decl.getReturnType(), decl.isReturnArray());
            if (!ensureReturnStmt(decl.getStmtList(), expectedType)) {
                throw new ValidationException("Missing return statement", BahnPackage.Literals.FUNC_DECL__STMT_LIST);
            }
        } else {
            // ensure having no return statement
            if (!ensureNoReturnStatement(decl.getStmtList())) {
                throw new ValidationException("Unexpected return statement", BahnPackage.Literals.FUNC_DECL__STMT_LIST);
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

            if (stmt instanceof SelectionStmt) {
                SelectionStmt selectionStmt = (SelectionStmt) stmt;
                if (ensureReturnStmt(selectionStmt.getThenStmts(), expectedType)
                    && (selectionStmt.getElseStmts() == null || ensureReturnStmt(selectionStmt.getElseStmts(), expectedType))) {
                    return true;
                }
            }

            if (stmt instanceof ReturnStmt) {
                ExprDataType computedType = typeCheckingTable.computeDataType(((ReturnStmt) stmt).getExpr(), HintDataTypeUtl.from(expectedType.getDataType()));
                if (!typeCheckingTable.isValidType(computedType, expectedType)) {
                    throw ValidationException.createTypeException(expectedType, computedType, BahnPackage.Literals.RETURN_STMT__EXPR);
                }

                return true;
            }
        }

        return false;
    }
}

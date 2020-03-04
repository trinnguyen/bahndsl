package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.typing.HintDataType;
import de.uniba.swt.dsl.validation.typing.HintDataTypeUtl;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;
import de.uniba.swt.dsl.validation.util.ValidationException;

import javax.inject.Inject;

public class StatementValidator {

    @Inject
    TypeCheckingTable typeCheckingTable;

    /**
     * validate an statment
     * @param statement statement
     */
    public void validate(Statement statement) throws ValidationException {
        // VarDeclStmt
        if (statement instanceof VarDeclStmt) {
            VarDeclStmt stmt = (VarDeclStmt) statement;
            ExprDataType declType = typeCheckingTable.getDataType(stmt.getDecl());
            checkExprType(declType, stmt.getAssignment().getExpr(), HintDataTypeUtl.from(declType.getDataType()));
            return;
        }

        // AssignmentStmt
        if (statement instanceof AssignmentStmt) {
            AssignmentStmt stmt = (AssignmentStmt) statement;
            ExprDataType declType = typeCheckingTable.computeDataType(stmt.getReferenceExpr());
            checkExprType(declType, stmt.getAssignment().getExpr(), HintDataTypeUtl.from(declType.getDataType()));
            return;
        }

        // SelectionStmt
        if (statement instanceof SelectionStmt) {
            ExprDataType exprType = typeCheckingTable.computeDataType(((SelectionStmt) statement).getExpr());
            if (!exprType.isScalarBool()) {
                throw new ValidationException("Type Error: Expected type " + ExprDataType.ScalarBool.displayTypeName(), BahnPackage.Literals.SELECTION_STMT__EXPR);
            }
        }
    }

    private void checkExprType(ExprDataType declType, Expression expr, HintDataType hintDataType) throws ValidationException {
        if (typeCheckingTable.canComputeType(expr)) {
            ExprDataType exprType = typeCheckingTable.computeDataType(expr, hintDataType);
            if (!declType.equals(exprType)) {
                throw new ValidationException(String.format("Type Error: Expected type %s, actual type: %s", declType.displayTypeName(), exprType.displayTypeName()), BahnPackage.Literals.VAR_DECL_STMT__ASSIGNMENT);
            }
        }
    }
}



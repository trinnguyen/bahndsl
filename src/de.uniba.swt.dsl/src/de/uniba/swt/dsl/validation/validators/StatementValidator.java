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
            ExprDataType exprType = typeCheckingTable.computeDataType(stmt.getAssignment().getExpr(), HintDataTypeUtl.from(declType.getDataType()));
            if (!declType.equals(exprType)) {
                throw new ValidationException("Type Error: Expression has type " + exprType.displayTypeName(), BahnPackage.Literals.VAR_DECL_STMT__ASSIGNMENT);
            }
        }

        // AssignmentStmt
        if (statement instanceof AssignmentStmt) {
            AssignmentStmt stmt = (AssignmentStmt) statement;
            ExprDataType declType = typeCheckingTable.computeDataType(stmt.getReferenceExpr());
            ExprDataType exprType = typeCheckingTable.computeDataType(stmt.getAssignment().getExpr(), HintDataTypeUtl.from(declType.getDataType()));
            if (!declType.equals(exprType)) {
                throw new ValidationException("Type Error: Expression has type " + exprType.displayTypeName(), BahnPackage.Literals.ASSIGNMENT_STMT__ASSIGNMENT);
            }
        }

        // SelectionStmt
        if (statement instanceof SelectionStmt) {
            ExprDataType exprType = typeCheckingTable.computeDataType(((SelectionStmt) statement).getExpr());
            if (!exprType.isScalarBool()) {
                throw new ValidationException("Type Error: Expected " + ExprDataType.ScalarBool.displayTypeName(), BahnPackage.Literals.SELECTION_STMT__EXPR);
            }
        }
    }
}



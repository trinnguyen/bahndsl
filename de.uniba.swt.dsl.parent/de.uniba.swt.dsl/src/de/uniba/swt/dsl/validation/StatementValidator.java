package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.*;

class StatementValidator {

    /**
     * validate an statment
     * @param statement statement
     */
    public static void validate(Statement statement) throws ValidationException {
        // VarDeclStmt
        if (statement instanceof VarDeclStmt) {
            VarDeclStmt stmt = (VarDeclStmt) statement;
            ExprDataType declType = TypeComputingHelper.getDataType(stmt.getDecl());
            ExprDataType exprType = TypeComputingHelper.computeDataType(stmt.getAssignment().getExpr());
            if (!declType.equals(exprType)) {
                throw new ValidationException("Type Error: Expression has type " + exprType.displayTypeName(), BahnPackage.Literals.VAR_DECL_STMT__ASSIGNMENT);
            }
        }

        // AssignmentStmt
        if (statement instanceof AssignmentStmt) {
            AssignmentStmt stmt = (AssignmentStmt) statement;
            ExprDataType declType = TypeComputingHelper.computeDataType(stmt.getReferenceExpr());
            ExprDataType exprType = TypeComputingHelper.computeDataType(stmt.getAssignment().getExpr());
            if (!declType.equals(exprType)) {
                throw new ValidationException("Type Error: Expression has type " + exprType.displayTypeName(), BahnPackage.Literals.ASSIGNMENT_STMT__ASSIGNMENT);
            }
        }

        // SelectionStmt
        if (statement instanceof SelectionStmt) {
            ExprDataType exprType = TypeComputingHelper.computeDataType(((SelectionStmt) statement).getExpr());
            if (!exprType.isScalarBool()) {
                throw new ValidationException("Type Error: Expected " + ExprDataType.ScalarBool.displayTypeName(), BahnPackage.Literals.SELECTION_STMT__EXPR);
            }
        }
    }
}



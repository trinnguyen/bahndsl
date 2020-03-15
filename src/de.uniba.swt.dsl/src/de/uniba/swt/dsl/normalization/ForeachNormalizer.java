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

            // index
            var indexDecl = temporaryVarGenerator.createTempVar(ExprDataType.ScalarInt);
            var declStmt = createVarDeclStmt(indexDecl, createNumLiteral(0));

            // condition and current element
            var condition = createConditionExpr(indexDecl, foreachStmt.getArrayExpr().getDecl().getName());

            // update stmt list with start and stop
            var elementStmt = createVarDeclStmt(foreachStmt.getDecl(), createArrayIndexRef(foreachStmt.getArrayExpr().getDecl().getName(), indexDecl));
            var stmtList = foreachStmt.getStmts();
            stmtList.getStmts().add(0, elementStmt);

            // stop
            var indexIncreStmt = createIndexIncrementStmt(indexDecl);
            stmtList.getStmts().add(indexIncreStmt);

            // form while stmt
            var whileStmt = BahnFactory.eINSTANCE.createIterationStmt();
            whileStmt.setExpr(condition);
            whileStmt.setStmts(foreachStmt.getStmts());

            // replace
            BahnUtil.replaceEObject(foreachStmt, whileStmt);

            // additional decl stmt
            return List.of(declStmt);
        }

        return super.normalizeStmt(stmt);
    }

    private Statement createIndexIncrementStmt(VarDecl indexDecl) {
        // op
        var opExpr = BahnFactory.eINSTANCE.createOpExpression();
        opExpr.setLeftExpr(createVarRef(indexDecl));
        opExpr.setOp(OperatorType.PLUS);
        opExpr.setRightExpr(createNumLiteral(1));

        var assignmentVariable = BahnFactory.eINSTANCE.createVariableAssignment();
        assignmentVariable.setExpr(opExpr);

        var assignmentStmt = BahnFactory.eINSTANCE.createAssignmentStmt();
        assignmentStmt.setReferenceExpr(createVarRef(indexDecl));
        assignmentStmt.setAssignment(assignmentVariable);
        return assignmentStmt;
    }

    private ValuedReferenceExpr createVarRef(VarDecl indexDecl) {
        var indexRef = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        indexRef.setDecl(indexDecl);
        return indexRef;
    }

    private Expression createArrayIndexRef(String name, VarDecl indexDecl) {
        var expr = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        expr.setDecl(arrayLookupTable.createArrayVarExpr(name).getDecl());
        expr.setIndexExpr(createVarRef(indexDecl));
        return expr;
    }

    private Expression createConditionExpr(VarDecl indexDecl, String name) {
        var expr = BahnFactory.eINSTANCE.createOpExpression();
        expr.setLeftExpr(createVarRef(indexDecl));
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

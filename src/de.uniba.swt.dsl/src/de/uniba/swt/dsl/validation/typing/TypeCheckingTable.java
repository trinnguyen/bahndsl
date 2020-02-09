package de.uniba.swt.dsl.validation.typing;

import de.uniba.swt.dsl.bahn.*;
import org.apache.log4j.Logger;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TypeCheckingTable {
    private static final Logger logger = Logger.getLogger(TypeCheckingTable.class);

    private Map<Expression, ExprDataType> typeTable = new HashMap<>();

    public ExprDataType computeDataType(DataType hintType, Expression expression) {
        if (hasDataType(expression))
            return typeTable.get(expression);

        var dataType = TypeComputingHelper.computeDataType(hintType, expression);
        return insertToTable(expression, dataType);
    }

    public ExprDataType computeValuedReferenceExpr(ValuedReferenceExpr referenceExpr) {
        if (hasDataType(referenceExpr))
            return typeTable.get(referenceExpr);

        var dataType = TypeComputingHelper.computeValuedReferenceExpr(referenceExpr);
        return insertToTable(referenceExpr, dataType);
    }

    public ExprDataType computeOpExpression(OpExpression leftExpr) {
        if (hasDataType(leftExpr))
            return typeTable.get(leftExpr);

        var dataType = TypeComputingHelper.computeOpExpression(leftExpr);
        return insertToTable(leftExpr, dataType);
    }

    private ExprDataType insertToTable(Expression expression, ExprDataType dataType) {
        typeTable.put(expression, dataType);
        return dataType;
    }

    public boolean hasDataType(Expression expression) {
        return typeTable.containsKey(expression);
    }

    public boolean isValidType(ExprDataType computedType, ExprDataType expectedType) {
        return computedType != null && computedType.equals(expectedType);
    }

    public void clear() {
        logger.debug("Clear type table");
        typeTable.clear();
    }

    public ExprDataType getDataType(VarDecl decl) {
        return TypeComputingHelper.getDataType(decl);
    }

    public ExprDataType lookup(LiteralExpr expr) {
        return typeTable.get(expr);
    }
}

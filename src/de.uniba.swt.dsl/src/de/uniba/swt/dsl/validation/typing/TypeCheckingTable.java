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

    public ExprDataType computeDataType(Expression expression) {
        return computeDataType(expression, HintDataType.NONE);
    }

    public ExprDataType computeDataType(Expression expression, HintDataType hintType) {
        if (hasDataType(expression))
            return lookup(expression);

        var dataType = TypeComputingHelper.computeExpr(expression, hintType);
        return insertToTable(expression, dataType);
    }

    private ExprDataType insertToTable(Expression expression, ExprDataType dataType) {
        typeTable.put(expression, dataType);
        return dataType;
    }

    public boolean isValidType(ExprDataType computedType, ExprDataType expectedType) {
        return computedType != null && computedType.equals(expectedType);
    }

    public void clear() {
        typeTable.clear();
    }

    public ExprDataType getDataType(VarDecl decl) {
        return TypeComputingHelper.getDataType(decl);
    }

    public boolean hasDataType(Expression expression) {
        return typeTable.containsKey(expression);
    }

    public ExprDataType lookup(Expression expr) {
        return typeTable.get(expr);
    }

    public boolean canComputeType(Expression expr) {
        return !(expr instanceof ExternalFunctionCallExpr);
    }
}

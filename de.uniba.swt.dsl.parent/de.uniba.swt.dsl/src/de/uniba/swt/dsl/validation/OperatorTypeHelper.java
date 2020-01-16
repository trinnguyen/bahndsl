package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.OperatorType;

public class OperatorTypeHelper {
    public static boolean isArithmeticOp(OperatorType op) {
        return op == OperatorType.PLUS
                || op == OperatorType.MINUS
                || op == OperatorType.MULTIPLY
                || op == OperatorType.DIVISION;
    }

    public static boolean isLogicalOp(OperatorType op) {
        return op == OperatorType.OR
                || op == OperatorType.AND;
    }

    public static boolean isEqualityOp(OperatorType op) {
        return op == OperatorType.EQUAL
                || op == OperatorType.NOT_EQUAL;
    }

    public static boolean isRelationalOp(OperatorType op) {
        return op == OperatorType.GREATER
                || op == OperatorType.LESS
                || op == OperatorType.GREATER_OR_EQUAL
                || op == OperatorType.LES_OR_EQUAL;
    }
}

package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.DataType;

public class ExprDataType {

    public static ExprDataType ScalarBool = new ExprDataType(DataType.BOOLEAN_TYPE);

    public static ExprDataType ScalarInt = new ExprDataType(DataType.INT_TYPE);

    public static ExprDataType ScalarFloat = new ExprDataType(DataType.FLOAT_TYPE);

    public static ExprDataType ScalarString = new ExprDataType(DataType.STRING_TYPE);

    public static ExprDataType Void = new ExprDataType(true);

    private boolean isVoid;

    private DataType dataType;

    private boolean isArray;

    public ExprDataType(DataType dataType) {
        this(dataType, false);
    }

    public ExprDataType(DataType dataType, boolean isArray) {
        this.dataType = dataType;
        this.isArray = isArray;
    }

    public ExprDataType(boolean isVoid) {
        this.isVoid = isVoid;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public void setVoid(boolean aVoid) {
        isVoid = aVoid;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public boolean isScalarNumber() {
        return !isVoid
                && (getDataType() == DataType.INT_TYPE || getDataType() == DataType.FLOAT_TYPE)
                && !isArray;
    }

    public boolean isScalarBool() {
        return !isVoid
                &&getDataType() == DataType.BOOLEAN_TYPE && !isArray;
    }

    public String displayTypeName() {
        if (isVoid)
            return "void";

        String res = dataType.getLiteral();
        if (isArray)
            res += "[]";

        return res;
    }

    @Override
    public String toString() {
        return displayTypeName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExprDataType dataType1 = (ExprDataType) o;

        if (isVoid != dataType1.isVoid) return false;
        if (isArray != dataType1.isArray) return false;
        return dataType == dataType1.dataType;
    }

    @Override
    public int hashCode() {
        int result = (isVoid ? 1 : 0);
        result = 31 * result + dataType.hashCode();
        result = 31 * result + (isArray ? 1 : 0);
        return result;
    }
}

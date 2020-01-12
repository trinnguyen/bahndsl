package de.uniba.swt.expr.validation;

import de.uniba.swt.expr.bahnexpr.DataType;

import java.util.Objects;

public class ExprDataType {

    public static ExprDataType ScalarBool = new ExprDataType(DataType.BOOLEAN_TYPE);

    public static ExprDataType ScalarInt = new ExprDataType(DataType.INT_TYPE);

    public static ExprDataType ScalarFloat = new ExprDataType(DataType.FLOAT_TYPE);

    public static ExprDataType ScalarString = new ExprDataType(DataType.STRING_TYPE);

    private DataType dataType;

    private boolean isArray;

    public ExprDataType(DataType dataType) {
        this(dataType, false);
    }

    public ExprDataType(DataType dataType, boolean isArray) {
        this.dataType = dataType;
        this.isArray = isArray;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExprDataType that = (ExprDataType) o;
        return isArray == that.isArray &&
                dataType == that.dataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, isArray);
    }

    @Override
    public java.lang.String toString() {
        return "ExprDataType{" +
                "dataType=" + dataType +
                ", isArray=" + isArray +
                '}';
    }

    public boolean isScalarNumber() {
        return (getDataType() == DataType.INT_TYPE || getDataType() == DataType.FLOAT_TYPE)
                && !isArray;
    }

    public String displayTypeName() {
        String res = dataType.getLiteral();
        if (isArray)
            res += "[]";

        return res;
    }

    public boolean isScalarBool() {
        return getDataType() == DataType.BOOLEAN_TYPE && !isArray;
    }
}

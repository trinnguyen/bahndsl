/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.validation.typing;

import de.uniba.swt.dsl.bahn.DataType;

public class ExprDataType {

    public static ExprDataType ScalarBool = new ExprDataType(DataType.BOOLEAN_TYPE);

    public static ExprDataType ScalarInt = new ExprDataType(DataType.INT_TYPE);

    public static ExprDataType ScalarFloat = new ExprDataType(DataType.FLOAT_TYPE);

    public static ExprDataType ScalarString = new ExprDataType(DataType.STRING_TYPE);

    public static ExprDataType ArrayBool = new ExprDataType(DataType.BOOLEAN_TYPE, true);

    public static ExprDataType ArrayInt = new ExprDataType(DataType.INT_TYPE, true);

    public static ExprDataType ArrayFloat = new ExprDataType(DataType.FLOAT_TYPE, true);

    public static ExprDataType ArrayString = new ExprDataType(DataType.STRING_TYPE, true);

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

    public boolean isScalarString() {
        return !isVoid
                &&getDataType() == DataType.STRING_TYPE && !isArray;
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

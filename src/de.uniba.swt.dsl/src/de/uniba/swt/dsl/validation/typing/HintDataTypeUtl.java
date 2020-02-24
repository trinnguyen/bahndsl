package de.uniba.swt.dsl.validation.typing;

import de.uniba.swt.dsl.bahn.DataType;

public class HintDataTypeUtl {
    public static HintDataType from(DataType dataType) {
        switch (dataType) {
            case FLOAT_TYPE:
                return HintDataType.FLOAT;
            case INT_TYPE:
                return HintDataType.INT;
            default:
                return HintDataType.NONE;
        }
    }
}

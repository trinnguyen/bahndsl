package de.uniba.swt.dsl.common.util;

public class StringUtil {
    public static boolean isNullOrEmpty(String val) {
        return val == null || val.isEmpty();
    }

    public static boolean isNotEmpty(String val) {
        return !isNullOrEmpty(val);
    }
}

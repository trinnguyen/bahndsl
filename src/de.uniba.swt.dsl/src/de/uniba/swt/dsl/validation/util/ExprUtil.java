package de.uniba.swt.dsl.validation.util;

import java.util.Arrays;

public class ExprUtil {

    public static <T> String toString(T[] arr) {
        return Arrays.toString(arr);
    }

    public static <T> boolean arrayContains(T[] array, T value) {
        if (array == null || array.length == 0)
            return false;
        return Arrays.asList(array).contains(value);
    }

    public static boolean isInteger(double val) {
        return val % 1 == 0;
    }
}

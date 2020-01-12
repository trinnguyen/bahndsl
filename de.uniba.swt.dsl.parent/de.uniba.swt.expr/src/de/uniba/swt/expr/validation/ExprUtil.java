package de.uniba.swt.expr.validation;

import java.util.Arrays;

public class ExprUtil {

    public static <T> String toString(T[] arr) {
        return Arrays.toString(arr);
    }

    public static <T> boolean arrayContains(T[] array, T value) {
        if (array == null || array.length == 0)
            return false;
        return java.util.Arrays.asList(array).contains(value);
    }
}

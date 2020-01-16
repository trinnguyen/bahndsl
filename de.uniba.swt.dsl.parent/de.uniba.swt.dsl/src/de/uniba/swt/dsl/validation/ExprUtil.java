package de.uniba.swt.dsl.validation;

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
}

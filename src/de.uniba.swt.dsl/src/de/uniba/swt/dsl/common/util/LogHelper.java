package de.uniba.swt.dsl.common.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogHelper {
    public static String printObject(Object object) {
        if (object == null)
            return "NULL";

        if (object.getClass().isArray())
            return printArray((Object[]) object);

        if (object instanceof Iterable<?>)
            return printCollection((Collection<?>)object);

        if (object instanceof Map<?,?>) {
            return printCollection(((Map<?,?>)object).entrySet());
        }

        if (object instanceof Stream<?>)
            return printStream((Stream<?>) object);

        return object.toString();
    }

    private static String printCollection(Collection<?> items) {
        if (items == null)
            return "NULL";

        return items.size() + "\n{\n" + printStream(items.stream()) + "\n}";
    }

    private static String printArray(Object[] items) {
        if (items == null)
            return "NULL";

        return items.length + "\n[\n" + printStream(Arrays.stream(items)) + "\n]";
    }

    private static String printStream(Stream<?> stream) {
        return stream.map(o -> "\t" + printObject(o)).collect(Collectors.joining("\n"));
    }
}

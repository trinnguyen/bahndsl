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

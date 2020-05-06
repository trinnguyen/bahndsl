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

package de.uniba.swt.dsl.generator.cli;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArgOptionContainer {

    private final List<ArgOption> options;

    public ArgOptionContainer(List<ArgOption> options) {
        this.options = options;
    }

    public List<ArgOption> getOptions() {
        return options;
    }

    public String formatHelp(String prefix) {
        StringBuilder builder = new StringBuilder();
        builder.append("USAGE: ").append(prefix).append(" ");
        builder.append(options.stream().map(o -> "[" + o.getFormattedKeyValue() + "]").collect(Collectors.joining(" ")));
        builder.append("\n");
        for (ArgOption option : options) {
            String tab = option.isHasValue() ? "\t" : "\t\t";
            builder.append("" +
                    "  ").append(option.getFormattedKeyValue()).append(tab).append(option.getDesc()).append("\n");
        }
        return builder.toString();
    }

    private ArgOption findOption(String key) {
        return options.stream().filter(o -> o.getFormattedName().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return formatHelp("");
    }

    public ArgParseResult parse(String[] args, int startIndex) throws Exception {
        ArgParseResult result = new ArgParseResult();
        if (args == null)
            return result;

        int index = startIndex;
        ArgOption curOption = null;
        Set<Integer> consumedIndices = new HashSet<>();
        while (index < args.length) {
            String scalar = args[index];

            // looking for arg with value
            if (curOption != null) {
                if (scalar.startsWith("-")) {
                    throw new Exception(String.format("Missing value for %s", curOption.getFormattedName()));
                }

                result.addOption(curOption, scalar);
                consumedIndices.add(index - 1);
                consumedIndices.add(index);
                curOption = null;
            } else {

                // arg without value
                if (scalar.startsWith("-")) {
                    var option = findOption(scalar);
                    if (option != null) {
                        if (!option.isHasValue()) {
                            result.addOption(option);
                            consumedIndices.add(index);
                        } else {
                            curOption = option;
                        }
                    }
                }
            }

            index++;
        }

        // check if
        if (curOption != null) {
            throw new Exception(String.format("Missing value for %s", curOption.getFormattedName()));
        }

        result.setConsumedIndices(consumedIndices);
        return result;
    }
}

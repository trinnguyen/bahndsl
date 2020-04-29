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

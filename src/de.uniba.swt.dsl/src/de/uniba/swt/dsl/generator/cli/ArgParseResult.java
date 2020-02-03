package de.uniba.swt.dsl.generator.cli;

import java.util.HashMap;
import java.util.Map;

public class ArgParseResult {

    private Map<String, String> mapValues = new HashMap<>();

    public void addOption(ArgOption option) {
        addOption(option, null);
    }

    public void addOption(ArgOption option, String value) {
        mapValues.put(option.getName(), value);
    }

    public boolean hasOption(String name) {
        return mapValues.containsKey(name);
    }

    public String getValue(String name, String defaultValue) {
        return mapValues.getOrDefault(name, defaultValue);
    }
}

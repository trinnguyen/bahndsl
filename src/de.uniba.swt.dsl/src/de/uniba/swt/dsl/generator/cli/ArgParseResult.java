package de.uniba.swt.dsl.generator.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArgParseResult {

    private final Map<String, String> mapValues = new HashMap<>();
    private Set<Integer> consumedIndices;

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

    public void setConsumedIndices(Set<Integer> consumedIndices) {
        this.consumedIndices = consumedIndices;
    }

    public Set<Integer> getConsumedIndices() {
        return consumedIndices;
    }
}

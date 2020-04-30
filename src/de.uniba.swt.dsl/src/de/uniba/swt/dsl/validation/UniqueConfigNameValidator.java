package de.uniba.swt.dsl.validation;


import java.util.HashSet;
import java.util.Set;

public class UniqueConfigNameValidator {
    private final Set<String> ids = new HashSet<>();

    public void clear() {
        ids.clear();
    }

    public boolean lookup(String id) {
        return ids.contains(id);
    }

    public void insert(String id) {
        ids.add(id.toLowerCase());
    }
}


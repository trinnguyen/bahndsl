package de.uniba.swt.dsl.validation;


import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class UniqueIdValidator {
    private final Set<String> ids = new HashSet<>();

    public void clear() {
        ids.clear();
    }

    public boolean lookup(String id) {
        return ids.contains(id);
    }

    public void insert(String id) {
        ids.add(id);
    }
}

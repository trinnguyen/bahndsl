package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.FuncDecl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UniqueVarIdValidator {
    private final Map<String, Set<String>> mapFuncVars = new HashMap<>();

    public void clear() {
        mapFuncVars.clear();
    }

    public boolean lookup(FuncDecl funcDecl, String id) {
        var funcName = funcDecl.getName().toLowerCase();
        return mapFuncVars.containsKey(funcName) && mapFuncVars.get(funcName).contains(id.toLowerCase());
    }

    public void insert(FuncDecl funcDecl, String id) {
        var funcName = funcDecl.getName().toLowerCase();
        if (!mapFuncVars.containsKey(funcName)) {
            mapFuncVars.put(funcName, new HashSet<>());
        }

        mapFuncVars.get(funcName).add(id.toLowerCase());
    }

    public boolean lookupFunc(String name) {
        return mapFuncVars.containsKey(name.toLowerCase());
    }

    public void insertFunc(String name) {
        if (!mapFuncVars.containsKey(name.toLowerCase())) {
            mapFuncVars.put(name.toLowerCase(), new HashSet<>());
        }
    }
}

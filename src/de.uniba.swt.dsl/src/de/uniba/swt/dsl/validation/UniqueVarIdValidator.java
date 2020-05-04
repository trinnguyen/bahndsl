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

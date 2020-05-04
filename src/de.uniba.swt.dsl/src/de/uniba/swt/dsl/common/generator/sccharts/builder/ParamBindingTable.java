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

package de.uniba.swt.dsl.common.generator.sccharts.builder;

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.ValuedReferenceExpr;
import de.uniba.swt.dsl.common.generator.sccharts.models.SuperState;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

@Singleton
public class ParamBindingTable {
    private final Stack<Map<String, String>> bindingStack = new Stack<>();

    public void reset() {
        bindingStack.clear();
    }

    public void addBindingMappingIfNeeded(SuperState superState) {
        if (superState.getReferenceBindingExprs().size() > 0) {
            Map<String, String> map = new HashMap<>();

            var decls = superState.getDeclarations().stream().filter(d -> d.isInput() || d.isOutput()).collect(Collectors.toList());
            for (int i = 0; i < superState.getReferenceBindingExprs().size(); i++) {
                if (decls.size() > i) {
                    var param = superState.getReferenceBindingExprs().get(i);

                    // add one entry
                    if (param instanceof ValuedReferenceExpr) {
                        map.put(decls.get(i).getName(), ((ValuedReferenceExpr) param).getDecl().getName());
                    }
                }
            }

            bindingStack.push(map);
        }
    }

    public void removeBindingMappingIfNeeded(SuperState superState) {
        if (superState.getReferenceBindingExprs().size() > 0) {
            bindingStack.pop();
        }
    }

    public String lookupBindingName(String name) {
        // find binding name if needed
        for (Map<String, String> stringStringMap : bindingStack) {
            if (stringStringMap.containsKey(name)) {
                return stringStringMap.get(name);
            }
        }

        return name;
    }
}

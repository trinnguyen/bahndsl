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

package de.uniba.swt.dsl.normalization;

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ArrayLookupTable {
    private final Map<String, Tuple<RefVarDecl, RefVarDecl>> mapArray = new HashMap<>();
    private String functionName;
    private int counter = 0;

    public void resetFunc(String name) {
        this.mapArray.clear();
        this.functionName = name.toLowerCase();
        this.counter = 0;
    }

    public void insert(RefVarDecl decl, RefVarDecl lenDecl) {

        // add to map
        mapArray.put(decl.getName(), Tuple.of(decl, lenDecl));
    }

    public String generateTempLenVar(String name) {
        return String.format("_%s_%s_cnt_%d", functionName, name, ++counter);
    }

    public RefVarDecl lookupVarDecl(String name) {
        var tuple = mapArray.get(name);
        return tuple != null ? tuple.getFirst() : null;
    }

    public RefVarDecl lookupLengthDecl(String name) {
        var tuple = mapArray.get(name);
        return tuple != null ? tuple.getSecond() : null;
    }

    public ValuedReferenceExpr createArrayVarExpr(String name) {
        var ref = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        ref.setLength(false);
        ref.setIndexExpr(null);
        ref.setDecl(lookupVarDecl(name));
        return ref;
    }

    public ValuedReferenceExpr createLenVarExpr(String name) {
        var ref = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        ref.setLength(false);
        ref.setIndexExpr(null);
        ref.setDecl(lookupLengthDecl(name));
        return ref;
    }
}

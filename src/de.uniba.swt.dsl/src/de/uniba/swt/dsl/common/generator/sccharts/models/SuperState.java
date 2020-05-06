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

package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.Expression;
import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperState extends State {
    private List<State> states = new ArrayList<>();
    private List<SVarDeclaration> declarations = new ArrayList<>();
    private Set<String> hostCodeReferences = new HashSet<>();
    private String joinTargetId;
    private final List<LocalAction> localActions = new ArrayList<>();
    private List<Expression> referenceBindingExprs = new ArrayList<>();

    public SuperState(SuperState other, List<Expression> params) {
        super(other);
        this.states = other.states;
        this.declarations = other.declarations;
        this.hostCodeReferences = other.hostCodeReferences;
        this.joinTargetId = other.joinTargetId;

        // update params
        if (params != null)
            this.referenceBindingExprs.addAll(params);
    }

    public List<State> getStates() {
        return states;
    }

    public List<SVarDeclaration> getDeclarations() {
        return declarations;
    }

    public Set<String> getHostCodeReferences() {
        return hostCodeReferences;
    }

    public String getJoinTargetId() {
        return joinTargetId;
    }

    public void setJoinTargetId(String joinTargetId) {
        this.joinTargetId = joinTargetId;
    }

    public SuperState(String id) {
        super(id);
    }

    public List<Expression> getReferenceBindingExprs() {
        return referenceBindingExprs;
    }

    public List<LocalAction> getLocalActions() {
        return localActions;
    }

    @Override
    public String toString() {
        return "SuperState{" +
                "states=" + states +
                ", declarations=" + declarations +
                ", hostCodeReferences=" + hostCodeReferences +
                '}';
    }
}

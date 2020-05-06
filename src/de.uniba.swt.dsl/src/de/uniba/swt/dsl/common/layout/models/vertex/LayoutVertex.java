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

package de.uniba.swt.dsl.common.layout.models.vertex;

import java.util.*;
import java.util.stream.Collectors;

public class LayoutVertex {
    private final String id = UUID.randomUUID().toString();
    private List<AbstractVertexMember> members = new ArrayList<>();

    public LayoutVertex() {
    }

    public String getId() {
        return id;
    }

    public LayoutVertex(List<AbstractVertexMember> members) {
        this.members = members;
    }

    public List<AbstractVertexMember> getMembers() {
        return members;
    }

    public boolean addIfNeeded(AbstractVertexMember member) {
        if (members.stream()
                .noneMatch(r -> r.getKey().equalsIgnoreCase(member.getKey()))) {
            members.add(member);
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayoutVertex that = (LayoutVertex) o;

        return Objects.equals(members, that.members);
    }

    @Override
    public int hashCode() {
        return members != null ? members.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "{" + members + "}";
    }

    public Optional<AbstractVertexMember> findMember(String key) {
        return getMembers().stream().filter(member -> member.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Optional<AbstractVertexMember> findMemberByName(String name) {
        return getMembers().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
    }

}


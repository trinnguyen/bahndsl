package de.uniba.swt.dsl.common.layout.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LayoutVertex {
    private List<VertexMember> members = new ArrayList<>();

    public LayoutVertex() {
    }

    public LayoutVertex(List<VertexMember> members) {
        this.members = members;
    }

    public List<VertexMember> getMembers() {
        return members;
    }

    public void setMembers(List<VertexMember> members) {
        this.members = members;
    }

    public boolean addIfNeeded(VertexMember member) {
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
}


package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SignalElement;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.models.Signal;

import java.util.*;

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

    /**
     * Check if 2 vertices having the same block, same signal or same switch
     * @param vertex
     * @param ignoreMemberName
     * @return
     */
    public boolean isConflict(LayoutVertex vertex, String ignoreMemberName) {
        return getMembers().stream()
                .filter(m -> !m.getName().equalsIgnoreCase(ignoreMemberName))
                .anyMatch(m -> vertex.findMemberByName(m.getName()).isPresent());
    }
}


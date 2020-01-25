package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.VertexMember;
import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.*;

public class NetworkLayout {
    private List<LayoutVertex> vertices = new ArrayList<>();
    private Map<String, LayoutVertex> mapVertices = new TreeMap<>();

    public NetworkLayout() {
    }

    public void clear() {
        vertices.clear();
        mapVertices.clear();
    }

    public List<LayoutVertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<LayoutVertex> vertices) {
        this.vertices = vertices;
    }

    public Map<String, LayoutVertex> getMapVertices() {
        return mapVertices;
    }

    public void setMapVertices(Map<String, LayoutVertex> mapVertices) {
        this.mapVertices = mapVertices;
    }

    public void addMembersToVertex(List<VertexMember> members, LayoutVertex vertex) {
        members.forEach(member -> {
            if (vertex.addIfNeeded(member)) {
                mapVertices.put(member.getKey(), vertex);
            }
        });
    }

    public LayoutVertex addNewVertex() {
        var vertex = new LayoutVertex();
        vertices.add(vertex);
        return vertex;
    }

    public LayoutVertex findVertex(VertexMember member) {
        var key = member.getKey();
        if (mapVertices.containsKey(key)) {
            return mapVertices.get(key);
        }

        return null;
    }

    @Override
    public String toString() {
        return "NetworkLayout {" + "\n" +
                "vertices=" + LogHelper.printObject(vertices) + "\n" +
                ", mapVertices=" + LogHelper.printObject(mapVertices) + "\n" +
                '}';
    }
}

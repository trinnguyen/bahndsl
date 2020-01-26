package de.uniba.swt.dsl.common.layout;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.layout.models.graph.*;
import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.*;
import java.util.stream.Collectors;

public class NetworkLayout implements LayoutGraph {
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

    public LayoutVertex findVertex(String memberKey) {
        if (mapVertices.containsKey(memberKey)) {
            return mapVertices.get(memberKey);
        }

        return null;
    }

    public boolean hasVertex(String memberKey) {
        return mapVertices.containsKey(memberKey);
    }

    @Override
    public String toString() {
        return "NetworkLayout {" + "\n" +
                "vertices=" + LogHelper.printObject(vertices) + "\n" +
                ", mapVertices=" + LogHelper.printObject(mapVertices) + "\n" +
                '}';
    }

    @Override
    public Set<LayoutVertex> adjacentVertices(LayoutVertex vertex) {
        return incidentEdges(vertex).stream().map(AbstractEdge::getDestVertex).collect(Collectors.toSet());
    }

    @Override
    public Set<AbstractEdge> incidentEdges(LayoutVertex vertex) {
        return vertex.getMembers()
                .stream()
                .filter(member -> member.isSegmentBlock() || member.isPoint())
                .map(member -> findEdges(vertex, member))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public ValueGraph<LayoutVertex, AbstractEdge> generateGraph() {
        MutableValueGraph<LayoutVertex, AbstractEdge> graph = ValueGraphBuilder
                .undirected()
                .allowsSelfLoops(false)
                .build();

        Map<String, Boolean> visitedEdges = new HashMap<>();
        dfsGraph(graph, visitedEdges, getVertices().get(0));
        return graph;
    }

    private void dfsGraph(MutableValueGraph<LayoutVertex, AbstractEdge> graph, Map<String, Boolean> visitedEdges, LayoutVertex vertex) {
        for (AbstractEdge edge : incidentEdges(vertex)) {
            // check if visited
            if (visitedEdges.containsKey(edge.getKey()))
                continue;
            visitedEdges.put(edge.getKey(), true);

            // put
            graph.putEdgeValue(edge.getSrcVertex(), edge.getDestVertex(), edge);

            // go deep
            dfsGraph(graph, visitedEdges, edge.getDestVertex());
        }
    }

    private Set<AbstractEdge> findEdges(LayoutVertex vertex, VertexMember member) {
        // segment block: platform, block
        if (member.isSegmentBlock()) {
            var blockMember = (BlockVertexMember) member;
            var newEndpoint = blockMember.getEndpoint() == BlockVertexMember.BlockEndpoint.Up ?
                    BlockVertexMember.BlockEndpoint.Down :
                    BlockVertexMember.BlockEndpoint.Up;
            var dest = findVertex(blockMember.generateKey(newEndpoint));
            return Set.of(new BlockEdge(blockMember.getBlock(), vertex, dest));
        }

        // point: switch
        if (member.getType() == VertexMemberType.Switch) {
            var switchMember = (SwitchVertexMember) member;
            var connectedEndpoints = switchMember.getConnectedEndpoints();
            return connectedEndpoints.stream()
                    .map(e -> {
                        var v = findVertex(switchMember.generateKey(e));
                        return new SwitchEdge(switchMember.getBlock(),
                                getAspect(switchMember.getEndpoint(), e),
                                vertex,
                                v);
                    })
                    .collect(Collectors.toSet());
        }

        throw new RuntimeException("Member is not supported for edge: " + member);
    }

    private SwitchEdge.Aspect getAspect(SwitchVertexMember.PointEndpoint srcEndpoint, SwitchVertexMember.PointEndpoint destEndPoint) {
        var set = Set.of(srcEndpoint, destEndPoint);
        if (set.contains(SwitchVertexMember.PointEndpoint.Stem)) {
            if (set.contains(SwitchVertexMember.PointEndpoint.Normal)) {
                return SwitchEdge.Aspect.Normal;
            }

            return SwitchEdge.Aspect.Reverse;
        }

        return null;
    }

    public void addMissingBlockVertices() {
        Set<LayoutVertex> set = new HashSet<>();
        for (var vertex : this.getVertices()) {
            for (var member : vertex.getMembers()) {
                if (member.isSegmentBlock()) {
                    var blockVertexMember = (BlockVertexMember) member;
                    var endpoint = blockVertexMember.getEndpoint() == BlockVertexMember.BlockEndpoint.Up ?
                            BlockVertexMember.BlockEndpoint.Down :
                            BlockVertexMember.BlockEndpoint.Up;

                    if (!hasVertex(blockVertexMember.generateKey(endpoint))) {
                        var newVertex = new LayoutVertex();
                        set.add(newVertex);

                        this.addMembersToVertex(List.of(blockVertexMember.generateMember(endpoint)), newVertex);
                    }
                }
            }
        }
        this.getVertices().addAll(set);
    }
}

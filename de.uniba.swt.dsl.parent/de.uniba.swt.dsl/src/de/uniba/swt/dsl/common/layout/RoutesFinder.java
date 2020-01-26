package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.SignalVertexMember;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.graph.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.graph.SwitchEdge;

import java.util.*;

public class RoutesFinder {

    private NetworkLayout networkLayout;
    private SignalVertexMember srcMember;
    private SignalVertexMember destMember;
    private LayoutVertex srcSignal;
    private LayoutVertex destSignal;

    private Stack<AbstractEdge> currentEdges = new Stack<>();
    private Stack<LayoutVertex> currentVertices = new Stack<>();
    private Set<Stack<AbstractEdge>> paths = new HashSet<>();
    private Map<LayoutVertex, Boolean> flagsOnPath = new HashMap<>();

    public Set<Stack<AbstractEdge>> findAllRoutes(NetworkLayout networkLayout, String srcSignalKey, String destSignalKey) {

        this.networkLayout = networkLayout;
        this.srcSignal = networkLayout.findVertex(srcSignalKey);
        this.destSignal = networkLayout.findVertex(destSignalKey);

        // find member
        this.srcMember = (SignalVertexMember)this.srcSignal.findMember(srcSignalKey).get();
        this.destMember = (SignalVertexMember)this.destSignal.findMember(destSignalKey).get();

        // clear
        currentEdges.clear();
        currentVertices.clear();
        paths.clear();

        // start finding
        dfs(srcSignal, null);
        return paths;
    }

    private void dfs(LayoutVertex vertex, AbstractEdge edge) {
        // ensure doesn't travel in the same point or back to current block
        if (edge != null) {

            // check same switch
            if (!currentEdges.isEmpty()) {
                var prevEdge = currentEdges.peek();
                if (isSameSwitch(prevEdge, edge))
                    return;
            }

            // check attached signal: out
            if (edge.getEdgeType() == AbstractEdge.EdgeType.Block) {
                BlockEdge blockEdge = (BlockEdge) edge;

                // skip if outgoing edge is the block edge in which the signal attach to
                if (currentVertices.peek().equals(srcSignal)) {
                    if (srcMember.getConnectedBlock().equals(blockEdge.getBlockElement()))
                        return;
                }

                // skip if incoming edge is not the block edge in which the signal attach to
                if (vertex.equals(destSignal)) {
                    if (!destMember.getConnectedBlock().equals(blockEdge.getBlockElement()))
                        return;
                }
            }

            // add edge
            currentEdges.add(edge);
        }

        // add to the path
        currentVertices.push(vertex);
        flagsOnPath.put(vertex, true);

        // terminate when same
        if (vertex.equals(destSignal)) {
            terminateCurrentPath();
        } else {
            for (var e : networkLayout.incidentEdges(vertex)) {
                var w = e.getDestVertex();
                if (!flagsOnPath.containsKey(w)) {
                    dfs(w, e);
                }
            }
        }

        // finish
        currentVertices.pop();
        if (!currentEdges.isEmpty()) {
            currentEdges.pop();
        }
        flagsOnPath.put(vertex, false);
    }

    private boolean isSameSwitch(AbstractEdge prevEdge, AbstractEdge edge) {
        if (prevEdge.getEdgeType() == AbstractEdge.EdgeType.Switch &&
                edge.getEdgeType() == AbstractEdge.EdgeType.Switch)
            return ((SwitchEdge)prevEdge).getBlockElement().equals(((SwitchEdge)edge).getBlockElement());

        return false;
    }

    private void terminateCurrentPath() {
        paths.add((Stack<AbstractEdge>) currentEdges.clone());
    }
}

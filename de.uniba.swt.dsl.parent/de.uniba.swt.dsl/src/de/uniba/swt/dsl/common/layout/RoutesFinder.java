package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.SignalVertexMember;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.graph.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.graph.SwitchEdge;
import org.apache.log4j.Logger;

import java.util.*;

public class RoutesFinder {

    private final static Logger logger = Logger.getLogger(RoutesFinder.class);

    private NetworkLayout networkLayout;
    private SignalVertexMember srcMember;
    private SignalVertexMember destMember;
    private LayoutVertex srcSignal;
    private LayoutVertex destSignal;

    private Stack<AbstractEdge> currentEdges = new Stack<>();
    private Stack<LayoutVertex> currentVertices = new Stack<>();
    private Set<Route> routes = new HashSet<>();
    private Set<LayoutVertex> flagsOnPath = new HashSet<>();

    public Set<Route> findAllRoutes(NetworkLayout networkLayout, String srcSignalKey, String destSignalKey) {
        this.networkLayout = networkLayout;
        this.srcSignal = networkLayout.findVertex(srcSignalKey);
        this.destSignal = networkLayout.findVertex(destSignalKey);

        // find member
        this.srcMember = (SignalVertexMember)this.srcSignal.findMember(srcSignalKey).get();
        this.destMember = (SignalVertexMember)this.destSignal.findMember(destSignalKey).get();

        // clear
        currentEdges.clear();
        currentVertices.clear();
        routes.clear();

        // start finding
        dfs(srcSignal, null);
        return routes;
    }

    private void dfs(LayoutVertex vertex, AbstractEdge edge) {
        // ensure doesn't travel in the same point or back to current block
        if (edge != null) {

            // check same switch
            if (!currentEdges.isEmpty()) {
                var prevEdge = currentEdges.peek();
                if (isSameSwitch(prevEdge, edge)) {
                    return;
                }
            }

            // check attached signal: out
            if (currentVertices.peek().equals(srcSignal) && edge.getEdgeType() == AbstractEdge.EdgeType.Block) {
                if (srcMember.getConnectedBlock().equals(((BlockEdge)edge).getBlockElement())) {
                    return;
                }
            }

            // skip if incoming edge is not the block edge in which the signal attach to
            if (vertex.equals(destSignal)) {
                // skip if reaching signal via point
                if (edge.getEdgeType() != AbstractEdge.EdgeType.Block ||
                        !destMember.getConnectedBlock().equals(((BlockEdge)edge).getBlockElement())
                ) {
                    return;
                }
            }

            // add edge
            currentEdges.add(edge);
        }

        // add to the path
        currentVertices.push(vertex);
        flagsOnPath.add(vertex);

        // terminate when same
        if (vertex.equals(destSignal)) {
            terminateCurrentPath();
        } else {
            for (var e : networkLayout.incidentEdges(vertex)) {
                var w = e.getDestVertex();
                if (!flagsOnPath.contains(w)) {
                    dfs(w, e);
                }
            }
        }

        // finish
        currentVertices.pop();
        if (!currentEdges.isEmpty()) {
            currentEdges.pop();
        }
        flagsOnPath.remove(vertex);
    }

    private boolean isSameSwitch(AbstractEdge prevEdge, AbstractEdge edge) {
        if (prevEdge.getEdgeType() == AbstractEdge.EdgeType.Switch &&
                edge.getEdgeType() == AbstractEdge.EdgeType.Switch)
            return ((SwitchEdge)prevEdge).getBlockElement().equals(((SwitchEdge)edge).getBlockElement());

        return false;
    }

    private void terminateCurrentPath() {
        routes.add(new Route(srcMember.getName(), destMember.getName(), (Stack<AbstractEdge>) currentEdges.clone()));
    }
}

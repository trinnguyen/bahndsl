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

package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.BlockDirection;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.edge.DoubleSlipSwitchEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.SignalVertexMember;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.edge.StandardSwitchEdge;
import org.apache.log4j.Logger;

import java.util.*;

public class RoutesFinder {

    private final static Logger logger = Logger.getLogger(RoutesFinder.class);

    private NetworkLayout networkLayout;
    private SignalVertexMember srcMember;
    private SignalVertexMember destMember;
    private LayoutVertex srcSignal;
    private LayoutVertex destSignal;

    private final Stack<AbstractEdge> currentEdges = new Stack<>();
    private final Stack<LayoutVertex> currentVertices = new Stack<>();
    private final Set<Route> routes = new HashSet<>();
    private final Set<LayoutVertex> flagsOnPath = new HashSet<>();

    public Set<Route> findRoutes(NetworkLayout networkLayout, String srcSignalKey, String destSignalKey) {
        this.networkLayout = networkLayout;
        this.srcSignal = networkLayout.findVertex(srcSignalKey);
        this.destSignal = networkLayout.findVertex(destSignalKey);

        // check if having layout
        if (srcSignal == null || destSignal == null) {
            logger.warn("Source or destination signal is not modelled in the layout: " + srcSignalKey + " -> " + destSignalKey);
            return null;
        }

        // find member
        this.srcMember = (SignalVertexMember)this.srcSignal.findMember(srcSignalKey).get();
        this.destMember = (SignalVertexMember)this.destSignal.findMember(destSignalKey).get();

        // clear
        currentEdges.clear();
        currentVertices.clear();
        routes.clear();

        // start finding
        if (NetworkLayoutUtil.validateSignalDirection(networkLayout, srcSignal, srcMember)) {
            dfs(srcSignal, null);
        } else {
            logger.debug(String.format("No route from signal %s due to direction constraint (%s)",
                    srcMember.getName(),
                    srcMember.getConnectedBlock().getName()));
        }
        return routes;
    }

    private void dfs(LayoutVertex vertex, AbstractEdge edge) {
        // ensure doesn't travel in the same point or back to current block
        if (edge != null) {

            if (!validateEdge(vertex, edge))
                return;

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

    private boolean validateEdge(LayoutVertex vertex, AbstractEdge edge) {
        // check attached signal: out
        if (currentVertices.peek().equals(srcSignal)) {
            // prevent going back to the attached block
            if (edge instanceof BlockEdge) {
                if (srcMember.getConnectedBlock().equals(((BlockEdge) edge).getBlockElement())) {
                    return false;
                }
            }
        }

        // ensure block edge direction is allowed
        if (edge instanceof BlockEdge) {
            var blockEdge = (BlockEdge) edge;
            var direction = networkLayout.getBlockDirection(blockEdge.getBlockElement().getName());
            if (direction != BlockDirection.Bidirectional && direction != blockEdge.getDirection()) {
                return false;
            }
        }

        // check same switch
        if (!currentEdges.isEmpty()) {
            var prevEdge = currentEdges.peek();
            if (isSamePoint(prevEdge, edge)) {
                return false;
            }
        }

        // skip if incoming edge is not the block edge in which the signal attach to
        if (vertex.equals(destSignal)) {
            // skip if reaching signal via point
            return edge instanceof BlockEdge &&
                    destMember.getConnectedBlock().equals(((BlockEdge) edge).getBlockElement());
        }

        return true;
    }

    private boolean isSamePoint(AbstractEdge prevEdge, AbstractEdge edge) {
        if (prevEdge.getEdgeType() == AbstractEdge.EdgeType.SingleSwitch &&
                edge.getEdgeType() == AbstractEdge.EdgeType.SingleSwitch)
            return ((StandardSwitchEdge)prevEdge).getPointElement().equals(((StandardSwitchEdge)edge).getPointElement());

        if (prevEdge.getEdgeType() == AbstractEdge.EdgeType.DoubleSlipSwitch &&
                edge.getEdgeType() == AbstractEdge.EdgeType.DoubleSlipSwitch)
            return ((DoubleSlipSwitchEdge)prevEdge).getPointElement().equals(((DoubleSlipSwitchEdge)edge).getPointElement());

        return false;
    }

    private void terminateCurrentPath() {
        Stack<AbstractEdge> clonedEdges = new Stack<>();
        // List<String> immediateSignals = new ArrayList<>();
        for (AbstractEdge edge : currentEdges) {
            clonedEdges.push(edge);

            /*
            if (edge instanceof BlockEdge) {
                var blockEdge = (BlockEdge) edge;

                // find signal on destination
                blockEdge.getDestVertex()
                        .getMembers()
                        .stream()
                        .filter(m -> m instanceof SignalVertexMember && !((SignalVertexMember) m).getSignal().equals(destMember.getSignal()))
                        .findFirst().ifPresent(signal -> immediateSignals.add(signal.getName()));
            }
             */
        }
        routes.add(new Route(srcMember.getName(), destMember.getName(), clonedEdges));
    }
}

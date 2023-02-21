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

import de.uniba.swt.dsl.bahn.CrossingElement;
import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.CrossingEdge;
import de.uniba.swt.dsl.common.layout.models.edge.DoubleSlipSwitchEdge;
import de.uniba.swt.dsl.common.layout.models.edge.StandardSwitchEdge;

import java.util.*;

public class NetworkRoutesExplorer {
    private final RoutesFinder routesFinder = new RoutesFinder();

    // When routeType is simple: Finds all possible routes a pair of signals that follow each other.
    // When routeType is extended: Finds all possible routes between all possible source and destination signals.
    public List<Route> findAllRoutes(NetworkLayout networkLayout, Set<String> signals, String routeType) {
        int id = 0;
        List<Route> routes = new ArrayList<>();

        for (var src : signals) {
            if (networkLayout.findVertex(src) == null)
                continue;

            for (var dest : signals) {
                if (src.equalsIgnoreCase(dest))
                    continue;

                if (networkLayout.findVertex(dest) == null)
                    continue;

                var paths = routesFinder.findRoutes(networkLayout, src, dest, routeType);
                if (paths != null && !paths.isEmpty()) {
                    for (Route path : paths) {
                        path.setId(String.valueOf(id++));
                        routes.add(path);
                    }
                }
            }
        }

        // update conflicts
        for (var route : routes) {
            System.out.println(route.getId());
            updateConflicts(route, routes);
        }

        return routes;
    }

    private void updateConflicts(Route route, List<Route> routes) {
        for (var tmpRoute : routes) {
            // Conflict: Routes have the same source signal or destination signal.
            if (route.getSrcSignal().equals(tmpRoute.getSrcSignal())) {
                route.getConflictRouteIds().add(tmpRoute.getId());
                tmpRoute.getConflictRouteIds().add(route.getId());
            }

            if (route.getConflictRouteIds().contains(tmpRoute.getId()))
                continue;

            // Conflict: Routes have at least one edge in common.
            Set<Object> routeEdgeReferences = getEdgeReferences(route);
            Set<Object> tmpouteEdgeReferences = getEdgeReferences(tmpRoute);
            if (!Collections.disjoint(routeEdgeReferences, tmpouteEdgeReferences)) {
                route.getConflictRouteIds().add(tmpRoute.getId());
                tmpRoute.getConflictRouteIds().add(route.getId());
            }
        }
    }

    private Set<Object> getEdgeReferences(Route route) {
        Set<Object> references = new HashSet<>();
        for (AbstractEdge edge : route.getEdges()) {
            if (edge instanceof StandardSwitchEdge) {
                PointElement point = ((StandardSwitchEdge) edge).getPointElement();
                references.add(point);
            } else if (edge instanceof DoubleSlipSwitchEdge) {
                PointElement point = ((DoubleSlipSwitchEdge) edge).getPointElement();
                references.add(point);
            } else if (edge instanceof CrossingEdge) {
                CrossingElement crossing = ((CrossingEdge) edge).getCrossing();
                references.add(crossing);
            } else {
                references.add(edge);
            }
        }

        return references;
    }
}

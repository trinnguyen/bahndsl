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
import java.util.stream.Collectors;

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
        updateConflicts(routes);

        return routes;
    }

    private void addConflict(Route route1, Set<Object> conflicts1, Route route2, Set<Object> conflicts2) {
        conflicts1.add(route2);
        conflicts2.add(route1);
    }

    // Returns an array of sets of edges references: {point, double-slip switch, crossing, block}
    private List<Set<Object>> getEdgeReferences(Route route) {
        List<Set<Object>> references = new ArrayList<>(Arrays.asList(
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>()
        ));

        for (AbstractEdge edge : route.getEdges()) {
            if (edge instanceof StandardSwitchEdge) {
                PointElement point = ((StandardSwitchEdge) edge).getPointElement();
                references.get(0).add(point);
            } else if (edge instanceof DoubleSlipSwitchEdge) {
                PointElement point = ((DoubleSlipSwitchEdge) edge).getPointElement();
                references.get(1).add(point);
            } else if (edge instanceof CrossingEdge) {
                CrossingElement crossing = ((CrossingEdge) edge).getCrossing();
                references.get(2).add(crossing);
            } else {
                references.get(3).add(edge);
            }
        }

        return references;
    }

    private void updateConflicts(List<Route> routes) {
        Map<Route, List<Set<Object>>> routeConflictData = new LinkedHashMap<>();
        for (var route : routes) {
            // Array of sets of edges references: { 0:point, 1:double-slip switch, 2:crossing, 3:block, 4:conflicts }
            List<Set<Object>> routeData = getEdgeReferences(route);
            routeData.add(new HashSet<>());

            routeConflictData.put(route, routeData);
        }

        for (Map.Entry<Route, List<Set<Object>>> route1 : routeConflictData.entrySet()) {
            System.out.println(route1.getKey().getId());
            for (Map.Entry<Route, List<Set<Object>>> route2 : routeConflictData.entrySet()) {
                // Ignore the route itself.
                if (route1.getKey() == route2.getKey()) {
                    continue;
                }

                // Check whether both routes have already been identified as being conflicts.
                if (route1.getValue().get(4).contains(route2.getKey())) {
                    continue;
                }

                // Conflict: Routes have the same source signal or same destination signal.
                if (route1.getKey().getSrcSignal() == route2.getKey().getSrcSignal()
                        || route1.getKey().getDestSignal() == route2.getKey().getDestSignal()) {
                    addConflict(route1.getKey(), route1.getValue().get(4), route2.getKey(), route2.getValue().get(4));
                    continue;
                }

                // Conflict: Routes have at least one edge in common.
                for (var i : Arrays.asList(0, 1, 2, 3)) {
                    if (!Collections.disjoint(route1.getValue().get(i), route2.getValue().get(i))) {
                        addConflict(route1.getKey(), route1.getValue().get(4), route2.getKey(), route2.getValue().get(4));
                        break;
                    }
                }
            }
        }

        for (Map.Entry<Route, List<Set<Object>>> route : routeConflictData.entrySet()) {
            route.getKey().getConflictRouteIds().addAll(route.getValue().get(4).stream().map(conflict -> ((Route)conflict).getId()).collect(Collectors.toList()));
        }
    }
}

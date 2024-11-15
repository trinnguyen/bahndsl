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
import org.eclipse.xtext.xbase.lib.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NetworkRoutesExplorer {
    private final RoutesFinder routesFinder = new RoutesFinder();

    // When routeType is simple: Finds all possible routes a pair of signals that follow each other.
    // When routeType is extended: Finds all possible routes between all possible source and destination signals.
    public List<Route> findAllRoutes(NetworkLayout networkLayout, Set<String> signals, String routeType) {
        int id = 0;
        List<Route> routes = new ArrayList<>();

        System.out.println("Finding routes for " + signals.size() + " source signals");
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
                        path.setId(id++);
                        routes.add(path);
                    }
                }
            }
        }

        // update conflicts
        updateConflicts(routes);

        return routes;
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
        // Prepare progress feedback
        System.out.print("Computing conflicts for " + routes.size() + " routes ...0%");
        Stack<Pair<Integer, String>> progress = new Stack<>();
        progress.push(new Pair<>(routes.size() * 3 / 4, "...75%"));
        progress.push(new Pair<>(routes.size() / 2, "...50%"));
        progress.push(new Pair<>(routes.size() / 4, "...25%"));

        // Sort the list of routes based on their id.
        routes.sort(Comparator.comparingInt(Route::getId));

        // 2D matrix indexed by the route ids to mark the routes that conflict with each other.
        boolean[][] hasConflictMatrix = new boolean[routes.size()][routes.size()];

        // For each route, store each edge type as a set of references: { 0:point, 1:double-slip switch, 2:crossing, 3:block }
        List<List<Set<Object>>> routesToEdges = routes.stream().map(this::getEdgeReferences).collect(Collectors.toList());

        // For each route in routes.
        AtomicInteger numberOfRoutesProcessed = new AtomicInteger();
        IntStream.range(0, routes.size()).parallel().forEach(route1 -> {
            var edgesRoute1 = routesToEdges.get(route1);

            // Compare current route with the remaining routes.
            for (var route2 = route1 + 1; route2 < routes.size(); ++route2) {
                // Conflict: Routes have the same source signal or same destination signal.
                if (routes.get(route1).getSrcSignal() == routes.get(route2).getSrcSignal()
                        || routes.get(route1).getDestSignal() == routes.get(route2).getDestSignal()) {
                    hasConflictMatrix[route1][route2] = true;
                    hasConflictMatrix[route2][route1] = true;
                    continue;
                }

                // Conflict: Routes have at least one edge in common.
                for (var i = 0; i <  edgesRoute1.size(); ++i) {
                    if (!Collections.disjoint(edgesRoute1.get(i), routesToEdges.get(route2).get(i))) {
                        hasConflictMatrix[route1][route2] = true;
                        hasConflictMatrix[route2][route1] = true;
                        break;
                    }
                }
            }

            synchronized (progress) {
                if (!progress.empty() && progress.peek().getKey() == numberOfRoutesProcessed.incrementAndGet()) {
                    System.out.print(progress.pop().getValue());
                }
            }
        });
        System.out.print("...90%");

        // Transfer the conflicts into each route object.
        IntStream.range(0, routes.size()).parallel().forEach(routeId -> {
            routes.get(routeId).setHasConflicts(hasConflictMatrix[routeId]);
        });

        System.out.println("...100%");
    }
}

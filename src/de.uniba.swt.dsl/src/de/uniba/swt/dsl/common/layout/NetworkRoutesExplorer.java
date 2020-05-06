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
import de.uniba.swt.dsl.common.layout.models.vertex.DoubleSlipSwitchVertexMember;

import java.util.*;

public class NetworkRoutesExplorer {
    private final RoutesFinder routesFinder = new RoutesFinder();

    public List<Route> findAllRoutes(NetworkLayout networkLayout, Set<String> signals) {
        int id = 0;
        List<Route> routes = new ArrayList<>();

        // find all
        for (var src : signals) {
            if (networkLayout.findVertex(src) == null)
                continue;

            //TODO check signal direction

            for (var dest : signals) {
                if (src.equalsIgnoreCase(dest))
                    continue;

                if (networkLayout.findVertex(dest) == null)
                    continue;

                var paths = routesFinder.findRoutes(networkLayout, src, dest);
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
            updateConflicts(route, routes);
        }

        return routes;
    }

    private void updateConflicts(Route route, List<Route> routes) {
        for (var tmpRoute : routes) {
            if (route.equals(tmpRoute))
                continue;

            if (route.getConflictRouteIds().contains(tmpRoute.getId()))
                continue;

            if (isConflict(route, tmpRoute)) {
                route.getConflictRouteIds().add(tmpRoute.getId());
                tmpRoute.getConflictRouteIds().add(route.getId());
            }
        }
    }

    private boolean isConflict(Route route1, Route route2) {
        for (AbstractEdge edge : route1.getEdges()) {
            // same edge
            if (route2.getEdges().contains(edge)) {
                return true;
            }

            // same standard switch
            if (edge instanceof StandardSwitchEdge) {
                if (hasPoint(route2, ((StandardSwitchEdge) edge).getPointElement()))
                    return true;
            }

            // same double slip switch
            if (edge instanceof DoubleSlipSwitchEdge) {
                if (hasPoint(route2, ((DoubleSlipSwitchEdge) edge).getPointElement()))
                    return true;
            }

            // same crossing
            if (edge instanceof CrossingEdge) {
                if (hasCrossing(route2, ((CrossingEdge) edge).getCrossing()))
                    return true;
            }
        }
        return false;
    }

    private boolean hasCrossing(Route route, CrossingElement crossing) {
        for (AbstractEdge edge : route.getEdges()) {
            if (edge instanceof CrossingEdge) {
                if (((CrossingEdge) edge).getCrossing().equals(crossing))
                    return true;
            }
        }

        return false;
    }

    private boolean hasPoint(Route route, PointElement point) {
        for (AbstractEdge edge : route.getEdges()) {
            if (edge instanceof StandardSwitchEdge) {
                if (((StandardSwitchEdge) edge).getPointElement().equals(point))
                    return true;
            }

            if (edge instanceof DoubleSlipSwitchEdge) {
                if (((DoubleSlipSwitchEdge) edge).getPointElement().equals(point))
                    return true;
            }
        }

        return false;
    }
}

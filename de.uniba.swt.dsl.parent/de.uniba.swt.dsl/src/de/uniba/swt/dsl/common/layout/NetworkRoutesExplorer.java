package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;

import java.util.*;

public class NetworkRoutesExplorer {
    private RoutesFinder routesFinder = new RoutesFinder();

    public List<Route> findAllRoutes(NetworkLayout networkLayout, Set<String> signals) {
        int id = 1;
        List<Route> routes = new ArrayList<>();

        // find all
        for (var src : signals) {
            if (networkLayout.findVertex(src) == null)
                continue;

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
            updateConflicts(routes, route);
        }

        return routes;
    }

    private void updateConflicts(List<Route> routes, Route route) {
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
            if (route2.getEdges().contains(edge)) {
                return true;
            }
        }
        return false;
    }
}

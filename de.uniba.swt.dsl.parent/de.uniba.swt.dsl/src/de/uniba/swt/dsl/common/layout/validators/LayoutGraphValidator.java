package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.common.layout.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.LayoutVertex;

import java.util.HashMap;
import java.util.Map;

public class LayoutGraphValidator {
    private NetworkLayout networkLayout;
    private Map<LayoutVertex, Boolean> visitedVertices = new HashMap<>();

    public boolean isStrongConnected(NetworkLayout networkLayout) {
        this.networkLayout = networkLayout;
        visitedVertices.clear();

        // start with first
        dfs(networkLayout.getVertices().get(0));

        // check
        for (LayoutVertex vertex : networkLayout.getVertices()) {
            if (!isVisited(vertex))
                return false;
        }

        return true;
    }

    private void dfs(LayoutVertex vertex) {
        if (isVisited(vertex))
            return;
        markVisited(vertex);

        networkLayout.adjacentVertices(vertex).forEach(this::dfs);
    }

    private void markVisited(LayoutVertex vertex) {
        visitedVertices.put(vertex, true);
    }

    private boolean isVisited(LayoutVertex vertex) {
        return visitedVertices.containsKey(vertex);
    }
}

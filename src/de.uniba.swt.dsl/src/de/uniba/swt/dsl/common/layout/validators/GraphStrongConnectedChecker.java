/*
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BahnDSL.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 */

package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.HashMap;
import java.util.Map;

public class GraphStrongConnectedChecker {
    private NetworkLayout networkLayout;
    private final Map<LayoutVertex, Boolean> visitedVertices = new HashMap<>();

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

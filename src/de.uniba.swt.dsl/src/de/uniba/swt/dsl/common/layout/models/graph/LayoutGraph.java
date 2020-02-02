package de.uniba.swt.dsl.common.layout.models.graph;

import com.google.common.graph.ValueGraph;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.Set;

public interface LayoutGraph {
    Set<LayoutVertex> adjacentVertices(LayoutVertex vertex);

    Set<AbstractEdge> incidentEdges(LayoutVertex vertex);

    ValueGraph<LayoutVertex, AbstractEdge> generateGraph();
}

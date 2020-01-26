package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.LayoutVertex;

import java.util.Set;

public interface LayoutGraph {
    Set<LayoutVertex> adjacentVertices(LayoutVertex vertex);
}

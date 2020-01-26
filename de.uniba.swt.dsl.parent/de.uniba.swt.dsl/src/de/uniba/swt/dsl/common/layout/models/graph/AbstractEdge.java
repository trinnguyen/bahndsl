package de.uniba.swt.dsl.common.layout.models.graph;

public abstract class AbstractEdge {
    public enum EdgeType {
        Block,
        Platform,
        Switch,
        Crossing
    }

    private LayoutVertex srcVertex;
    private LayoutVertex destVertex;

    public AbstractEdge(LayoutVertex srcVertex, LayoutVertex destVertex) {
        this.srcVertex = srcVertex;
        this.destVertex = destVertex;
    }

    public LayoutVertex getSrcVertex() {
        return srcVertex;
    }

    public void setSrcVertex(LayoutVertex srcVertex) {
        this.srcVertex = srcVertex;
    }

    public LayoutVertex getDestVertex() {
        return destVertex;
    }

    public void setDestVertex(LayoutVertex destVertex) {
        this.destVertex = destVertex;
    }

    public abstract EdgeType getEdgeType();

    public boolean isSegmentBlock() {
        return getEdgeType() == EdgeType.Block
                || getEdgeType() == EdgeType.Platform;
    }

    public boolean isPoint() {
        return getEdgeType() == EdgeType.Switch
                || getEdgeType() == EdgeType.Crossing;
    }
}

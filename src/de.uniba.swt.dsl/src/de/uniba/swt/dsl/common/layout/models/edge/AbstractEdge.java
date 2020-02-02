package de.uniba.swt.dsl.common.layout.models.edge;

import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.List;
import java.util.Objects;

public abstract class AbstractEdge {
    public enum EdgeType {
        Block,
        Platform,
        SingleSwitch,
        DoubleSlipSwitch,
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
        return getEdgeType() == EdgeType.SingleSwitch
                || getEdgeType() == EdgeType.DoubleSlipSwitch;
    }

    public abstract String getKey();

    public abstract List<SegmentElement> getSegments();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEdge)) return false;

        AbstractEdge that = (AbstractEdge) o;

        if (!Objects.equals(srcVertex, that.srcVertex)) return false;
        return Objects.equals(destVertex, that.destVertex);
    }

    @Override
    public int hashCode() {
        int result = srcVertex != null ? srcVertex.hashCode() : 0;
        result = 31 * result + (destVertex != null ? destVertex.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getKey();
    }
}

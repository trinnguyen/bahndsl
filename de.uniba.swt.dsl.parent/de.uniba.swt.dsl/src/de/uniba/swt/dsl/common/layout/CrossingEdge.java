package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutVertex;

public class CrossingEdge extends AbstractEdge {
    public enum Aspect {
        Normal1,
        Normal2,
        Reverse1,
        Reverse2,
    }

    private PointElement pointElement;
    private Aspect aspect;

    public CrossingEdge(PointElement pointElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.pointElement = pointElement;
        this.aspect = aspect;
    }

    public PointElement getPointElement() {
        return pointElement;
    }

    public void setpointElement(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossingEdge that = (CrossingEdge) o;

        if (pointElement != null ? !pointElement.equals(that.pointElement) : that.pointElement != null) return false;
        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = pointElement != null ? pointElement.hashCode() : 0;
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.Crossing;
    }

    @Override
    public String getKey() {
        return (pointElement.getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public String toString() {
        return getKey();
    }
}

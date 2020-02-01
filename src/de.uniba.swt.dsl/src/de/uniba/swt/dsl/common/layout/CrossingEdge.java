package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractPointEdge;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutVertex;

public class CrossingEdge extends AbstractPointEdge {
    public enum Aspect {
        Normal1,
        Normal2,
        Reverse1,
        Reverse2,
    }

    private Aspect aspect;

    public CrossingEdge(PointElement pointElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(pointElement, srcVertex, destVertex);
        this.aspect = aspect;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.Crossing;
    }

    @Override
    public String getKey() {
        return (getPointElement().getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrossingEdge)) return false;
        if (!super.equals(o)) return false;

        CrossingEdge that = (CrossingEdge) o;

        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }
}

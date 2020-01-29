package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.PointElement;

import java.util.Objects;

public class SwitchEdge extends AbstractEdge {

    public enum Aspect {
        Normal,
        Reverse
    }

    private PointElement pointElement;
    private Aspect aspect;

    public SwitchEdge(PointElement pointElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.pointElement = pointElement;
        this.aspect = aspect;
    }

    public PointElement getPointElement() {
        return pointElement;
    }

    public void setPointElement(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.Switch;
    }

    @Override
    public String getKey() {
        return (pointElement.getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwitchEdge that = (SwitchEdge) o;

        if (!Objects.equals(pointElement, that.pointElement)) return false;
        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = pointElement != null ? pointElement.hashCode() : 0;
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return pointElement.getName() + "." + aspect.toString().toLowerCase();
    }
}

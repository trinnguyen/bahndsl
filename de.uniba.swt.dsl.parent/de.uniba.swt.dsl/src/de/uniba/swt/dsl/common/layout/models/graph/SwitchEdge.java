package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.bahn.TrackSection;

import java.util.Objects;

public class SwitchEdge extends AbstractPointEdge {

    public enum Aspect {
        Normal,
        Reverse
    }

    private Aspect aspect;

    public SwitchEdge(PointElement pointElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
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
        return EdgeType.Switch;
    }

    @Override
    public String getKey() {
        return (getPointElement().getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SwitchEdge)) return false;
        if (!super.equals(o)) return false;

        SwitchEdge that = (SwitchEdge) o;

        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }
}

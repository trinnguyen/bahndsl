package de.uniba.swt.dsl.common.layout.models.edge;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

public class StandardSwitchEdge extends AbstractPointEdge {

    public enum Aspect {
        Normal,
        Reverse
    }

    private Aspect aspect;

    public StandardSwitchEdge(PointElement pointElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
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
        return EdgeType.SingleSwitch;
    }

    @Override
    public String getKey() {
        return (getPointElement().getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StandardSwitchEdge)) return false;
        if (!super.equals(o)) return false;

        StandardSwitchEdge that = (StandardSwitchEdge) o;

        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }

    @Override
    public String formatAspect() {
        return aspect.toString().toLowerCase();
    }
}

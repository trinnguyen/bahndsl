package de.uniba.swt.dsl.common.layout.models.edge;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

public class DoubleSlipSwitchEdge extends AbstractPointEdge {

    public enum Aspect {
        Normal1,
        Normal2,
        Reverse1,
        Reverse2,
    }

    private Aspect aspect;

    public DoubleSlipSwitchEdge(PointElement pointElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
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
        return EdgeType.DoubleSlipSwitch;
    }

    @Override
    public String getKey() {
        return (getPointElement().getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleSlipSwitchEdge)) return false;
        if (!super.equals(o)) return false;

        DoubleSlipSwitchEdge that = (DoubleSlipSwitchEdge) o;

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
        return (aspect == Aspect.Normal1 || aspect == Aspect.Normal2)
                ? "normal"
                : "reverse";
    }
}

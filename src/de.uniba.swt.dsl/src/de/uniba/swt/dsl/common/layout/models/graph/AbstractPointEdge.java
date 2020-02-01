package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.bahn.TrackSection;

import java.util.List;
import java.util.Objects;

public abstract class AbstractPointEdge extends AbstractEdge {
    private PointElement pointElement;

    public AbstractPointEdge(PointElement pointElement, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.pointElement = pointElement;
    }

    public PointElement getPointElement() {
        return pointElement;
    }

    public void setPointElement(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    @Override
    public List<SegmentElement> getSegments() {
        return List.of(getPointElement().getMainSeg());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPointEdge)) return false;
        if (!super.equals(o)) return false;

        AbstractPointEdge that = (AbstractPointEdge) o;

        return Objects.equals(pointElement, that.pointElement);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (pointElement != null ? pointElement.hashCode() : 0);
        return result;
    }
}

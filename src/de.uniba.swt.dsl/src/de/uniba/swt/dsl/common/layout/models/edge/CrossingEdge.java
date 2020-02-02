package de.uniba.swt.dsl.common.layout.models.edge;

import de.uniba.swt.dsl.bahn.CrossingElement;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.List;

public class CrossingEdge extends AbstractEdge {

    private final CrossingElement crossing;
    private final Aspect aspect;

    public CrossingEdge(CrossingElement crossingElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.crossing = crossingElement;
        this.aspect = aspect;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public CrossingElement getCrossing() {
        return crossing;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.Crossing;
    }

    @Override
    public String getKey() {
        return (getCrossing().getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public List<SegmentElement> getSegments() {
        return List.of(crossing.getMainSeg());
    }

    public enum Aspect {
        Line1, // down1 -> up2
        Line2 // down2 -> up1
    }
}

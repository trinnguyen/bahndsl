package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.PointElement;

public class SwitchEdge extends AbstractEdge {

    public enum Aspect {
        Normal,
        Reverse
    }

    private BlockElement blockElement;
    private Aspect aspect;

    public SwitchEdge(BlockElement blockElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.blockElement = blockElement;
        this.aspect = aspect;
    }

    public BlockElement getBlockElement() {
        return blockElement;
    }

    public void setBlockElement(BlockElement blockElement) {
        this.blockElement = blockElement;
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
    public String toString() {
        return blockElement.getName() + "." + aspect.toString().toLowerCase();
    }
}

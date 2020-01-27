package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.graph.SwitchEdge;

public class CrossingEdge extends AbstractEdge {
    public enum Aspect {
        Normal1,
        Normal2,
        Reverse1,
        Reverse2,
    }

    private BlockElement blockElement;
    private Aspect aspect;

    public CrossingEdge(BlockElement blockElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossingEdge that = (CrossingEdge) o;

        if (blockElement != null ? !blockElement.equals(that.blockElement) : that.blockElement != null) return false;
        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = blockElement != null ? blockElement.hashCode() : 0;
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.Crossing;
    }

    @Override
    public String getKey() {
        return (blockElement.getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public String toString() {
        return getKey();
    }
}

package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.PointElement;

import java.util.Objects;

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
    public String getKey() {
        return (blockElement.getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwitchEdge that = (SwitchEdge) o;

        if (!Objects.equals(blockElement, that.blockElement)) return false;
        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = blockElement != null ? blockElement.hashCode() : 0;
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return blockElement.getName() + "." + aspect.toString().toLowerCase();
    }
}

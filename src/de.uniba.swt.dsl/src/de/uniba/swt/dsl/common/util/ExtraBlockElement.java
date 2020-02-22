package de.uniba.swt.dsl.common.util;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.common.layout.models.BlockDirection;

import java.util.Set;

public class ExtraBlockElement {
    private BlockElement blockElement;
    private BlockDirection direction;
    private Set<String> signals;

    public ExtraBlockElement(BlockElement blockElement, BlockDirection direction, Set<String> signals) {
        this.blockElement = blockElement;
        this.direction = direction;
        this.signals = signals;
    }

    public BlockElement getBlockElement() {
        return blockElement;
    }

    public BlockDirection getDirection() {
        return direction;
    }

    public Set<String> getSignals() {
        return signals;
    }
}

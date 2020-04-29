package de.uniba.swt.dsl.common.util;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.common.layout.models.BlockDirection;

import java.util.List;

public class ExtraBlockElement {
    private final BlockElement blockElement;
    private final BlockDirection direction;
    private final List<String> signals;

    public ExtraBlockElement(BlockElement blockElement, BlockDirection direction, List<String> signals) {
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

    public List<String> getSignals() {
        return signals;
    }
}

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SegmentElement;

import java.util.Map;
import java.util.stream.Collectors;

class BlockElementYamlExporter extends AbstractElementYamlExporter<BlockElement> {
    @Override
    protected String getId(BlockElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(BlockElement element) {
        return Map.of("main", element.getMainSeg().getName(),
                "overlaps", element.getOverlaps().stream().map(SegmentElement::getName).collect(Collectors.toList()));
    }
}

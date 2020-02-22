package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BoardElement;

import java.util.Map;

class BoardElementYamlExporter extends AbstractElementYamlExporter<BoardElement> {
    @Override
    protected String getId(BoardElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(BoardElement element) {
        return Map.of("unique-id", element.getUniqueId(), "features", element.getFeatures());
    }
}


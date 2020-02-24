package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BoardFeatureElement;

import java.util.Map;

class BoardFeatureElementYamlExporter extends AbstractElementYamlExporter<BoardFeatureElement> {

    @Override
    protected String getIdName() {
        return "number";
    }

    @Override
    protected String getId(BoardFeatureElement element) {
        return element.getNumber();
    }

    @Override
    protected Map<String, Object> getProps(BoardFeatureElement element) {
        return Map.of("value", element.getValue());
    }
}

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.CrossingElement;

import java.util.Map;

class CrossingElementYamlExporter extends AbstractElementYamlExporter<CrossingElement> {
    @Override
    protected String getId(CrossingElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(CrossingElement element) {
        return Map.of("segment", element.getMainSeg().getName());
    }
}

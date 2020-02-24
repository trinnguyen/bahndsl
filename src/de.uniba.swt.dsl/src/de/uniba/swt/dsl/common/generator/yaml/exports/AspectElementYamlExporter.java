package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.AspectElement;

import java.util.Map;

class AspectElementYamlExporter extends AbstractElementYamlExporter<AspectElement> {

    @Override
    protected String getId(AspectElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(AspectElement element) {
        return Map.of("value", element.getValue());
    }
}

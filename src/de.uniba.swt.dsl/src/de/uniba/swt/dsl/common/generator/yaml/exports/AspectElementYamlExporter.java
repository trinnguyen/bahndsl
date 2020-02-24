package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.AspectElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
import java.util.Map;

class AspectElementYamlExporter extends AbstractElementYamlExporter<AspectElement> {

    @Override
    protected String getId(AspectElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(AspectElement element) {
        return List.of(Tuple.of("value", element.getValue()));
    }
}

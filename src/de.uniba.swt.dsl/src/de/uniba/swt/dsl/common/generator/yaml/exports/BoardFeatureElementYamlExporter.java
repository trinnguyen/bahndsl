package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BoardFeatureElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
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
    protected List<Tuple<String, Object>> getProps(BoardFeatureElement element) {
        return List.of(Tuple.of("value", element.getValue()));
    }
}

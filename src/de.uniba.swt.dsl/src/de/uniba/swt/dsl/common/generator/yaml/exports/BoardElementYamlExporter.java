package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BoardElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
import java.util.Map;

class BoardElementYamlExporter extends AbstractElementYamlExporter<BoardElement> {
    @Override
    protected String getId(BoardElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(BoardElement element) {
        return List.of(Tuple.of("unique-id", element.getUniqueId()),
                Tuple.of("features", element.getFeatures()));
    }
}


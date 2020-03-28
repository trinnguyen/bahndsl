package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.CrossingElement;
import de.uniba.swt.dsl.bahn.SignalType;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
import java.util.Map;

class CrossingElementYamlExporter extends AbstractElementYamlExporter<CrossingElement> {
    @Override
    protected String getId(CrossingElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(CrossingElement element) {
        return List.of(Tuple.of("segment", element.getMainSeg().getName()));
    }
}
package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.CompositionSignalElement;
import de.uniba.swt.dsl.bahn.RegularSignalElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CompositionSignalElementYamlExporter extends AbstractElementYamlExporter<CompositionSignalElement> {

    @Override
    protected String getId(CompositionSignalElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(CompositionSignalElement element) {
        List<Tuple<String, Object>> items = new ArrayList<>();
        for (RegularSignalElement signal : element.getSignals()) {
            items.add(Tuple.of(signal.getType().getName(), signal.getName()));
        }
        return items;
    }
}

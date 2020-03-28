package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.SignalType;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;

class SignalTypeYamlExporter extends AbstractElementYamlExporter<SignalType> {

    @Override
    protected String getId(SignalType element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(SignalType element) {
        return List.of(Tuple.of("aspects", element.getItems()),
                Tuple.of("initial", element.getInitial().getName()));
    }
}
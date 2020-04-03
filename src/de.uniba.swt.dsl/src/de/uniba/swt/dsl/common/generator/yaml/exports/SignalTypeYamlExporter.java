package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.AspectElement;
import de.uniba.swt.dsl.bahn.SignalType;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class SignalTypeYamlExporter extends AbstractElementYamlExporter<SignalType> {

    @Override
    protected String getId(SignalType element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(SignalType element) {
        List<String> aspectIds = element.getItems() != null ? element.getItems().stream().map(AspectElement::getName).collect(Collectors.toList()) : new ArrayList<>();
        return List.of(Tuple.of("aspects", aspectIds));
    }
}
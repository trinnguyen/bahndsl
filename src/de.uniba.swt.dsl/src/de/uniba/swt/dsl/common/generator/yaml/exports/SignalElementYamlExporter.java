package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.Tuple;
import org.eclipse.emf.common.util.EList;

import java.util.List;
import java.util.Map;

class SignalElementYamlExporter extends AbstractElementYamlExporter<SignalElement> {

    @Override
    protected String getId(SignalElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(SignalElement element) {
        return List.of(Tuple.of("number", element.getNumber()),
                Tuple.of("aspects", element.getType().getItems()),
                Tuple.of("initial", element.getType().getInitial().getName()),
                Tuple.of("type", element.getType().getName()));
    }
}

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.Length;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
import java.util.Map;

class SegmentElementYamlExporter extends AbstractElementYamlExporter<SegmentElement> {

    @Override
    protected String getId(SegmentElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(SegmentElement element) {
        return List.of(Tuple.of("address", element.getAddress()),
                Tuple.of("length", CommonFormatter.formatLength(element.getLength())));
    }
}

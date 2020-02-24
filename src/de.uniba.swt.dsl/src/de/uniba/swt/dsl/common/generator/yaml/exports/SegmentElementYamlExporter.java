package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.Length;
import de.uniba.swt.dsl.bahn.SegmentElement;

import java.util.Map;

class SegmentElementYamlExporter extends AbstractElementYamlExporter<SegmentElement> {

    @Override
    protected String getId(SegmentElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(SegmentElement element) {
        return Map.of("address", element.getAddress(), "length", CommonFormatter.formatLength(element.getLength()));
    }
}

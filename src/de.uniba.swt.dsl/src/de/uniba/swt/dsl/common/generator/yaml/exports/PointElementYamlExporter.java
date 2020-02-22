package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.PointAspectType;
import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.util.PointAspect;

import java.util.List;
import java.util.Map;

public class PointElementYamlExporter extends AbstractElementYamlExporter<PointElement> {
    @Override
    protected String getId(PointElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(PointElement element) {
        var aspects = List.of(
                new PointAspect(PointAspectType.NORMAL, element.getNormalValue()),
                new PointAspect(PointAspectType.REVERSE, element.getReverseValue()));
        return Map.of("number", element.getNumber(),
                "aspects", aspects,
                "initial", element.getInitial().getName().toLowerCase(),
                "segment", element.getMainSeg().getName());
    }
}

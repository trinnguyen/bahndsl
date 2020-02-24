package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.common.util.PointAspect;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
import java.util.Map;

class PointAspectYamlExporter extends AbstractElementYamlExporter<PointAspect> {
    @Override
    protected String getId(PointAspect element) {
        return element.getAspectType().getName().toLowerCase();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(PointAspect element) {
        return List.of(Tuple.of("value", element.getHexValue()));
    }
}

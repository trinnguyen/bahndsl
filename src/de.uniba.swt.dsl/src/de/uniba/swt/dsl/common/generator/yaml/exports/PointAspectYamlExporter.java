package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.common.util.PointAspect;

import java.util.Map;

class PointAspectYamlExporter extends AbstractElementYamlExporter<PointAspect> {
    @Override
    protected String getId(PointAspect element) {
        return element.getAspectType().getName().toLowerCase();
    }

    @Override
    protected Map<String, Object> getProps(PointAspect element) {
        return Map.of("value", element.getHexValue());
    }
}

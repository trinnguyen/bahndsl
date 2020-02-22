package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.util.ExtraBlockElement;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class ExtraBlockElementYamlExporter extends AbstractElementYamlExporter<ExtraBlockElement> {
    @Override
    protected String getId(ExtraBlockElement element) {
        return element.getBlockElement().getName();
    }

    @Override
    protected Map<String, Object> getProps(ExtraBlockElement element) {
        Map<String, Object> map = new HashMap<>();
        map.put("main", element.getBlockElement().getMainSeg().getName());
        map.put("overlaps", element.getBlockElement().getOverlaps().stream().map(SegmentElement::getName).collect(Collectors.toList()));
        map.put("direction", element.getDirection().toString().toLowerCase());
        map.put("trains", element.getBlockElement().getTrainTypes().stream().map(trainType -> trainType.getName().toLowerCase()).collect(Collectors.toList()));
        if (element.getSignals() != null) {
            map.put("signals", element.getSignals());
        }

        return map;
    }
}

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.LengthUnit;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.util.ExtraBlockElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        List<String> overlaps = new ArrayList<>();

        double length = element.getBlockElement().getMainSeg().getLength().getValue();
        LengthUnit unit = element.getBlockElement().getMainSeg().getLength().getUnit();
        for (SegmentElement overlap : element.getBlockElement().getOverlaps()) {
            overlaps.add(overlap.getName());
            length += overlap.getLength().getValue();
        }

        map.put("length", String.format("%.2f%s", length, unit.getLiteral().toLowerCase()));
        map.put("main", element.getBlockElement().getMainSeg().getName());
        map.put("overlaps", overlaps);
        map.put("direction", element.getDirection().toString().toLowerCase());
        map.put("trains", element.getBlockElement().getTrainTypes().stream().map(trainType -> trainType.getName().toLowerCase()).collect(Collectors.toList()));
        if (element.getSignals() != null) {
            map.put("signals", element.getSignals());
        }

        return map;
    }
}

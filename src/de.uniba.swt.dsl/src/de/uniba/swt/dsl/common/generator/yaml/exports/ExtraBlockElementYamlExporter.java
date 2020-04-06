package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.LengthUnit;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.util.ExtraBlockElement;
import de.uniba.swt.dsl.common.util.Tuple;

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
    protected List<Tuple<String, Object>> getProps(ExtraBlockElement element) {
        List<Tuple<String, Object>> list = new ArrayList<>();
        List<String> overlaps = new ArrayList<>();

        double length = element.getBlockElement().getMainSeg().getLength().getValue();
        LengthUnit unit = element.getBlockElement().getMainSeg().getLength().getUnit();
        for (SegmentElement overlap : element.getBlockElement().getOverlaps()) {
            overlaps.add(overlap.getName());
            length += overlap.getLength().getValue();
        }

        list.add(Tuple.of("length", String.format("%.2f%s", length, unit.getLiteral().toLowerCase())));
        list.add(Tuple.of("main", element.getBlockElement().getMainSeg().getName()));
        list.add(Tuple.of("overlaps", overlaps));
        if (element.getDirection() != null) {
            list.add(Tuple.of("direction", element.getDirection().toString().toLowerCase()));
        }

        list.add(Tuple.of("trains", element.getBlockElement().getTrainTypes().stream().map(trainType -> trainType.getName().toLowerCase()).collect(Collectors.toList())));
        if (element.getSignals() != null) {
            list.add(Tuple.of("signals", element.getSignals()));
        }

        return list;
    }
}

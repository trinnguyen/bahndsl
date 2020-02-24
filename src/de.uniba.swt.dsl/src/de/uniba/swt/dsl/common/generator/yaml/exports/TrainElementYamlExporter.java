package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.TrainElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrainElementYamlExporter extends AbstractElementYamlExporter<TrainElement> {
    @Override
    protected String getId(TrainElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(TrainElement element) {
        var list = new ArrayList<Tuple<String, Object>>();
        list.add(Tuple.of("dcc-address", element.getAddress()));
        list.add(Tuple.of("dcc-speed-steps", element.getSteps()));
        list.add(Tuple.of("calibration", element.getCalibrations()));
        list.add(Tuple.of("peripherals", element.getPeripherals()));
        if (element.getWeight() != null)
            list.add(Tuple.of("weight", CommonFormatter.formatWeight(element.getWeight())));
        if (element.getLength() != null)
            list.add(Tuple.of("length", CommonFormatter.formatLength(element.getLength())));
        if (element.getType() != null)
            list.add(Tuple.of("type", element.getType().getName().toLowerCase()));
        return list;
    }
}

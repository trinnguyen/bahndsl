package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.*;
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

        // go through prop
        for (ConfigProp prop : element.getProps()) {
            if (prop.getValue() instanceof Length) {
                list.add(Tuple.of("length", CommonFormatter.formatLength((Length) prop.getValue())));
            } else if (prop.getValue() instanceof Weight) {
                list.add(Tuple.of("weight", CommonFormatter.formatWeight((Weight) prop.getValue())));
            } else if (prop.getValue() instanceof TrainTypeValue) {
                list.add(Tuple.of("type", ((TrainTypeValue)prop.getValue()).getType().getName().toLowerCase()));
            }
        }

        return list;
    }
}

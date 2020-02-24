package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.TrainElement;

import java.util.HashMap;
import java.util.Map;

class TrainElementYamlExporter extends AbstractElementYamlExporter<TrainElement> {
    @Override
    protected String getId(TrainElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(TrainElement element) {
        var map = new HashMap<String, Object>();
        map.put("dcc-address", element.getAddress());
        map.put("dcc-speed-steps", element.getSteps());
        map.put("calibration", element.getCalibrations());
        map.put("peripherals", element.getPeripherals());
        if (element.getWeight() != null)
            map.put("weight", CommonFormatter.formatWeight(element.getWeight()));
        if (element.getLength() != null)
            map.put("length", CommonFormatter.formatLength(element.getLength()));
        if (element.getType() != null)
            map.put("type", element.getType().getName().toLowerCase());
        return map;
    }
}

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.TrainPeripheral;

import java.util.Map;

public class TrainPeripheralYamlExporter extends AbstractElementYamlExporter<TrainPeripheral> {
    @Override
    protected String getId(TrainPeripheral element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(TrainPeripheral element) {
        return Map.of("bit", element.getBit(), "initial", element.getInitial());
    }
}

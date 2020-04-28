package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.TrainPeripheral;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
import java.util.Map;

class TrainPeripheralYamlExporter extends AbstractElementYamlExporter<TrainPeripheral> {
    @Override
    protected String getId(TrainPeripheral element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(TrainPeripheral element) {
        return List.of(Tuple.of("bit", element.getBit()),
                Tuple.of("initial", element.getInitial()));
    }
}

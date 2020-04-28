package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.RootModule;
import de.uniba.swt.dsl.bahn.TrainElement;
import de.uniba.swt.dsl.bahn.TrainsProperty;
import de.uniba.swt.dsl.common.generator.yaml.exports.ElementExporterFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class TrainYamlExporter extends AbstractBidibYamlExporter {
    @Override
    protected String getHeaderComment() {
        return "Train configuration";
    }

    @Override
    protected void exportContent(RootModule rootModule) {
        List<TrainElement> trains = rootModule.getProperties().stream().filter(p -> p instanceof TrainsProperty).map(p -> ((TrainsProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toList());
        appendLine("trains:");
        for (TrainElement train : trains) {
            increaseLevel();
            ElementExporterFactory.build(this, train);
            decreaseLevel();
        }
    }
}

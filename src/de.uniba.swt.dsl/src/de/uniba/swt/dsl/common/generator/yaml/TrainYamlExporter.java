package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.yaml.exports.ElementExporterFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class TrainYamlExporter extends AbstractBidibYamlExporter {
    @Override
    protected String getHeaderComment() {
        return "Train configuration";
    }

    @Override
    protected void exportContent(RootModule rootModule) {
        Set<TrainElement> trains = rootModule.getProperties().stream().filter(p -> p instanceof TrainsProperty).map(p -> ((TrainsProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toSet());
        appendLine("trains:");
        for (TrainElement train : trains) {
            increaseLevel();
            ElementExporterFactory.build(this, train);
            decreaseLevel();
        }
    }
}

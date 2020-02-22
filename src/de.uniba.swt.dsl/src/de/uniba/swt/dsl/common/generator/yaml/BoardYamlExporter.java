package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.BoardElement;
import de.uniba.swt.dsl.bahn.BoardFeatureElement;
import de.uniba.swt.dsl.bahn.BoardsProperty;
import de.uniba.swt.dsl.bahn.RootModule;
import de.uniba.swt.dsl.common.generator.yaml.exports.ElementExporterFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

class BoardYamlExporter extends AbstractBidibYamlExporter {

    @Override
    protected String getHeaderComment() {
        return "BiDiB board configuration";
    }

    @Override
    protected void exportContent(RootModule rootModule) {
        Set<BoardElement> boards = rootModule.getProperties().stream().filter(p -> p instanceof BoardsProperty).map(p -> ((BoardsProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toSet());
        appendLine("boards:");

        for (BoardElement board : boards) {
            increaseLevel();
            ElementExporterFactory.build(this, board);
            decreaseLevel();
        }
    }
}
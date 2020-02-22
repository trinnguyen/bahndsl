package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.*;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtrasYamlExporter extends AbstractBidibYamlExporter {
    @Override
    protected String getHeaderComment() {
        return "Block layout configuration";
    }

    @Override
    protected void exportContent(RootModule rootModule) {
        Set<BlockElement> blocks = rootModule.getProperties().stream().filter(p -> p instanceof BlocksProperty).map(p -> ((BlocksProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toSet());
        Set<BlockElement> platforms = rootModule.getProperties().stream().filter(p -> p instanceof PlatformsProperty).map(p -> ((PlatformsProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toSet());
        Set<CrossingElement> crossings = rootModule.getProperties().stream().filter(p -> p instanceof CrossingsProperty).map(p -> ((CrossingsProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toSet());

        appendLine("extras:");
        increaseLevel();

        // blocks
        exportSection("blocks:", blocks);
        exportSection("platforms:", platforms);
        exportSection("crossings:", crossings);

        decreaseLevel();
    }
}

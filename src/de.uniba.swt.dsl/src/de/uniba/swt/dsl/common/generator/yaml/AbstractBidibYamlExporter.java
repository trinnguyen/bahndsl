package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.RootModule;
import de.uniba.swt.dsl.common.generator.yaml.exports.ElementExporterFactory;
import de.uniba.swt.dsl.common.util.YamlExporter;

import java.util.Collection;

abstract class AbstractBidibYamlExporter extends YamlExporter {

    public String export(RootModule rootModule) {
        reset();

        // comment
        appendLine("# %s: %s", getHeaderComment(), rootModule.getName());

        // build content
        exportContent(rootModule);
        return build();
    }

    protected void exportSection(String section, Collection<?> items) {
        appendLine(section);
        increaseLevel();
        for (Object item : items) {
            ElementExporterFactory.build(this, item);
        }
        decreaseLevel();
    }

    protected abstract String getHeaderComment();

    protected abstract void exportContent(RootModule rootModule);
}
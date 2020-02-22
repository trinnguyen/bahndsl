package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.RootModule;
import de.uniba.swt.dsl.common.util.YamlExporter;

abstract class AbstractBidibYamlExporter extends YamlExporter {

    public String export(RootModule rootModule) {
        reset();

        // comment
        appendLine("# %s: %s", getHeaderComment(), rootModule.getName());

        // build content
        exportContent(rootModule);
        return build();
    }

    protected abstract String getHeaderComment();

    protected abstract void exportContent(RootModule rootModule);
}
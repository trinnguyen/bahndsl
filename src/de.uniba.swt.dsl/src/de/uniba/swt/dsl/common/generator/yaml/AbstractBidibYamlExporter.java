/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.RootModule;
import de.uniba.swt.dsl.common.generator.yaml.exports.ElementExporterFactory;
import de.uniba.swt.dsl.common.util.YamlExporter;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.IOException;
import java.util.Collection;

abstract class AbstractBidibYamlExporter extends YamlExporter {

    public void export(IFileSystemAccess2 fsa, String filename, RootModule rootModule) {
        try {
            reset(fsa, filename);

            // comment
            appendLine("# %s: %s", getHeaderComment(), rootModule.getName());

            // build content
            exportContent(rootModule);
            flush();
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
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

package de.uniba.swt.dsl.common.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.*;

public class YamlExporter {

    private static final String SPACE = "  ";
    protected int indentLevel;
    private String indent = "";

    private StringBuilder builder = new StringBuilder();
    private OutputStream stream;
    private Writer writer;

    protected void reset(IFileSystemAccess2 fsa, String filename) {
        URI fileUri = fsa.getURI(filename);
        try {
            stream = new ExtensibleURIConverterImpl().createOutputStream(fileUri);
            writer = new OutputStreamWriter(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        builder = new StringBuilder();
        indentLevel = 0;
        updateIndent();
    }

    protected void close() {
        try {
            flush();
            writer.close();
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        try {
            writer.write(builder.toString());
            builder.setLength(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendLine(String text, Object... args) {
        builder.append(indent)
               .append(String.format(text, args))
               .append(System.lineSeparator());
    }

    public void increaseLevel() {
        indentLevel++;
        updateIndent();
    }

    public void decreaseLevel() {
        indentLevel--;
        updateIndent();
    }

    private void updateIndent() {
        indent = SPACE.repeat(Math.max(0, indentLevel));
    }
}

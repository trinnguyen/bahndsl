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

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class YamlExporter {

    private static final String SPACE = "  ";
    protected int indentLevel;

    private OutputStream stream;

    protected List<String> itemsToWrite = new LinkedList<>();

    protected void reset(IFileSystemAccess2 fsa, String filename) throws IOException {
        URI fileUri = fsa.getURI(filename);
        stream = new ExtensibleURIConverterImpl().createOutputStream(fileUri);
        indentLevel = 0;
        itemsToWrite.clear();
    }

    protected void close() throws IOException {
        stream.close();
    }

    public void appendLine(String text, Object... args) {
        itemsToWrite.add(SPACE.repeat(Math.max(0, indentLevel)) + String.format(text, args));
    }

    public void flush() throws IOException {
        stream.write(itemsToWrite.stream().collect(Collectors.joining(System.lineSeparator())).getBytes());
        stream.write(System.lineSeparator().getBytes());
        itemsToWrite.clear();
    }

    public void increaseLevel()
    {
        indentLevel++;
    }

    public void decreaseLevel()
    {
        indentLevel--;
    }

}

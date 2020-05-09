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

package de.uniba.swt.dsl.generator.externals;

import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.util.RuntimeIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class HeaderFileUtil {

    private HeaderFileUtil(){}

    private final static Logger logger = Logger.getLogger(HeaderFileUtil.class);

    public final static String IncludeHeaderText = "#include \"tick_wrapper.h\"";

    /**
     * Remove enum Thread status and include tick wrapper for header file
     * @param fsa
     * @param filename
     * @param oldName
     * @param newName
     */
    public static void updateThreadStatus(IFileSystemAccess2 fsa, String filename, String oldName, String newName) {

        // find the typedef enum and remove
        CharSequence text = null;
        try {
            text = fsa.readTextFile(filename);
        } catch (RuntimeIOException ex) {
            logger.error("Can not read header file to fix status: " + ex.getMessage());
        }

        if (text == null || text.length() == 0) {
            return;
        }

        var lines = Arrays.asList(text.toString().split(System.lineSeparator()));
        var end = findEnumNameLine(oldName, lines);
        if (end >= 0) {
            var start = findTypeDefLine(lines, end);
            if (start >= 0) {
                // remove
                var newLines = removeAndReplaceLines(lines, start, end, oldName, newName);

                // write to file
                fsa.generateFile(filename, mergeString(newLines));
            }
        }
    }

    private static CharSequence mergeString(List<String> newLines) {
        return String.join(System.lineSeparator(), newLines);
    }

    private static List<String> removeAndReplaceLines(List<String> lines, int start, int end, String oldName, String newName) {
        List<String> newLines = new ArrayList<>();
        newLines.add(IncludeHeaderText);
        for (int i = 0; i < lines.size(); i++) {
            if (i >= start && i<= end) {
                continue;
            }

            // replace
            newLines.add(lines.get(i).replace(oldName, newName));
        }

        return newLines;
    }

    private static int findTypeDefLine(List<String> lines, int end) {
        var regex = "^\\s*typedef\\s*enum\\s*";
        var pattern = Pattern.compile(regex);
        for (int i = end; i >= 0; i--) {
            if (pattern.matcher(lines.get(i)).find()) {
                // remove comment if needed
                if (i - 1 >= 0 && lines.get(i - 1).startsWith("//")) {
                    return i -1;
                }

                return i;
            }
        }

        return -1;
    }

    private static int findEnumNameLine(String name, List<String> lines) {
        var regex = String.format("^\\s*}\\s*%s\\s*;", name);
        var pattern = Pattern.compile(regex);
        for (int i = 0; i < lines.size(); i++) {
            if (pattern.matcher(lines.get(i)).find()) {
                return i;
            }
        }

        return -1;
    }
}

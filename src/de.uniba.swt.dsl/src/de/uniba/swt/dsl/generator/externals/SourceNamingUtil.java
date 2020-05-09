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

import de.uniba.swt.dsl.common.util.StringUtil;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.util.RuntimeIOException;

public class SourceNamingUtil {
    private final static Logger logger = Logger.getLogger(SourceNamingUtil.class);

    public static void updateNaming(IFileSystemAccess2 fsa, String filename) {
        updateHeaderNaming(fsa, filename + ".h", filename);
        updateSourceNaming(fsa, filename + ".c", filename);
    }

    private static void updateHeaderNaming(IFileSystemAccess2 fsa, String filename, String prefix) {
        // load text
        String text;
        try {
            text = fsa.readTextFile(filename).toString();
        } catch (RuntimeIOException ex) {
            logger.error("Can not read header file to fix status: " + ex.getMessage());
            return;
        }

        // replace IFace
        String newFace = getNewIface(prefix);
        text = text.replace("Iface;", newFace + ";") // typedef struct
                .replace("Iface* iface;", newFace + "* iface;") // regionR0Context
                .replace("Iface iface;", newFace + " iface;"); // TickData

        // replace TickData
        String newTickData = getNewTickData(prefix);
        text = text.replace("TickData;", newTickData + ";") // typedef struct
                .replace("TickData *", newTickData + "*"); // TickData

        // replace tick and reset
        text = text.replace("void reset", "void " + getNewReset(prefix)) // reset
                .replace("void tick", "void " + getNewTick(prefix)); // tick

        // update back
        fsa.generateFile(filename, text);
    }

    private static void updateSourceNaming(IFileSystemAccess2 fsa, String filename, String prefix) {
        // load text
        String text;
        try {
            text = fsa.readTextFile(filename).toString();
        } catch (RuntimeIOException ex) {
            logger.error("Can not read header file to fix status: " + ex.getMessage());
            return;
        }

        // replace TickData
        text = text.replace("TickData *", getNewTickData(prefix) + "*"); // TickData

        // replace tick and reset
        text = text.replace("void reset", "void " + getNewReset(prefix)) // reset
                .replace("void tick", "void " + getNewTick(prefix)); // tick

        // update back
        fsa.generateFile(filename, text);
    }

    private static String getNewIface(String prefix) {
        return StringUtil.capitalize(prefix) + "_Iface";
    }

    private static String getNewTickData(String prefix) {
        return StringUtil.capitalize(prefix) + "_TickData";
    }

    private static String getNewReset(String prefix) {
        return "intern_" + prefix + "_reset";
    }

    private static String getNewTick(String prefix) {
        return "intern_" + prefix + "_tick";
    }
}

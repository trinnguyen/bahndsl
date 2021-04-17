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

import de.cau.cs.kieler.sccharts.cli.SCChartsCompilerCLI;
import de.uniba.swt.dsl.common.fsa.FsaUtil;
import de.uniba.swt.dsl.common.util.BahnConstants;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;

public class EmbeddedSccLowLevelCodeExternalGenerator {

    public static final String SCC_GEN_SYSTEM = "de.cau.cs.kieler.sccharts.statebased.lean.c.template";

    private static final Logger logger = Logger.getLogger(EmbeddedSccLowLevelCodeExternalGenerator.class);

    public boolean generate(IFileSystemAccess2 fsa) {
        return executeFile(fsa, new String[] { BahnConstants.REQUEST_ROUTE_SCTX, BahnConstants.DRIVE_ROUTE_SCTX });
    }

    private boolean executeFile(IFileSystemAccess2 fsa, String[] inputs) {

        // ensure exist
        for (String input : inputs) {
            if (!fsa.isFile(input)) {
                logger.debug("File is not exist: " + input);
                return false;
            }
        }

        // start code generation
        var outputPath = FsaUtil.getFolderPath(fsa);
        var args = new ArrayList<String>();
        args.add("-s");
        args.add(SCC_GEN_SYSTEM);
        args.add("-o");
        args.add(outputPath);
        for (String input : inputs) {
            args.add(FsaUtil.getFile(fsa, input).getAbsolutePath());
        }

        return executeScc(args.toArray(String[]::new));
    }

    private boolean executeScc(String[] args) {
        logger.info("executeScc: " + Arrays.toString(args));
        try {
            var level = Logger.getRootLogger().getLevel();
            var cl = new CommandLine(new SCChartsCompilerCLI());
            Logger.getRootLogger().setLevel(level);
            return cl.execute(args) == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

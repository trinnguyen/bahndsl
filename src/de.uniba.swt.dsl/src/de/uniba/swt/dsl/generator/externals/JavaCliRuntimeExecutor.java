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

import de.uniba.swt.dsl.common.util.BahnUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaCliRuntimeExecutor extends CliRuntimeExecutor {

    private static final Logger logger = Logger.getLogger(CliRuntimeExecutor.class);

    @Override
    protected boolean internalExecuteCli(String command, String[] args, String workingDir) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(getPrefixIfNeeded()).append(command);
            if (args != null) {
                builder.append(" ");
                builder.append(String.join(" ", args));
            }

            var dir = new File(workingDir);
            var strCmd = builder.toString();

            // log
            logger.info(String.format("Working directory: %s", dir.getAbsolutePath()));
            logger.info(String.format("Execute: %s", strCmd));

            // execute
            var process = Runtime.getRuntime().exec(strCmd, null, dir);

            String s;
            // monitor result
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((s = stdInput.readLine()) != null) {
                    logger.info(s);
                }
            }

            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
            }

            return process.waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            logger.warn(e.getMessage(), e);
        }

        return false;
    }

    private static String getPrefixIfNeeded() {
        return BahnUtil.isWindows() ? "cmd /c " : "";
    }
}
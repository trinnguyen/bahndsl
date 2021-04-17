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

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.StringUtil;
import de.uniba.swt.dsl.common.util.Tuple;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaCliRuntimeExecutor extends CliRuntimeExecutor {

    private static final Logger logger = Logger.getLogger(CliRuntimeExecutor.class);

    @Override
    protected boolean internalExecuteCli(String command, String[] args, String workingDir) {
        try {
            var lst = new ArrayList<String>();
            var path = getCommandPath(command);
            if (StringUtil.isNullOrEmpty(path))
                return false;

            lst.add(path);
            if (args != null) {
                lst.addAll(Arrays.asList(args));
            }

            // log
            var dir = new File(workingDir);
            logger.info(String.format("Execute: '%s' in %s", String.join(" ", lst), dir.getAbsolutePath()));

            // return
            return executeCommand(lst.toArray(String[]::new), dir).getFirst();
        } catch (IOException | InterruptedException e) {
            logger.warn(e.getMessage(), e);
        }

        return false;
    }

    private static String getCommandPath(String command) throws IOException, InterruptedException {
        if (BahnUtil.isWindows()) {
            var filePair = executeCommand(new String[] {"where", command}, null);
            return filePair.getFirst() ? filePair.getSecond() : null;
        }

        return command;
    }


    private static Tuple<Boolean, String> executeCommand(String[] args, File dir) throws IOException, InterruptedException {
        String KeyStdOut = "stdout";
        // execute
        var process = Runtime.getRuntime().exec(args, null, dir);
        var resultStr = Stream.of(Tuple.of("stdout", process.getInputStream()), Tuple.of("stderr", process.getErrorStream())).parallel().map(tuple -> {
            try {
                var text = CharStreams.toString(new InputStreamReader(tuple.getSecond(), Charsets.UTF_8));
                return Tuple.of(tuple.getFirst(), text);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Tuple.of(tuple.getFirst(), "");
        }).collect(Collectors.toList());


        String output = null;
        for (Tuple<String, String> tuple : resultStr) {
            var text = tuple.getSecond().trim();
            if (tuple.getFirst().equals(KeyStdOut)) {
                output = text;
            }
            logger.info(text);
        }

        return Tuple.of(process.waitFor() == 0, output);
    }
}
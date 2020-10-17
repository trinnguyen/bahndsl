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

import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.StringUtil;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryExternalGenerator extends ExternalGenerator {

    private static final String ThreadStatusName = "ThreadStatus";

    private static final String WrapperThreadStatusName = "wrapper_thread_status";

    private static final String TemporaryObjFolderName = "obj";

    private static final String SharedLibNamePrefix = "libinterlocking";

    private static final Logger logger = Logger.getLogger(LibraryExternalGenerator.class);

    private final List<Tuple<String, String>> resources;

    private String sourceFileName;

    public LibraryExternalGenerator() {
        resources = List.of(
                Tuple.of("tick_wrapper_header.code", "tick_wrapper.h"),
                Tuple.of("drive_route_wrapper.code", "drive_route_wrapper.c"),
                Tuple.of("request_route_wrapper.code", "request_route_wrapper.c"),
                Tuple.of("bahn_data_util.code", "bahn_data_util.h"));
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    @Override
    protected String[] supportedTools() {
        return new String[] {"cc", "clang", "gcc"};
    }

    @Override
    protected boolean execute(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec) {

        var genModels = new String[]{
                StringUtil.capitalize(BahnConstants.REQUEST_ROUTE_FUNC_NAME),
                StringUtil.capitalize(BahnConstants.DRIVE_ROUTE_FUNC_NAME)
        };

        // process header
        preprocessHeaders(fsa, genModels);

        // list all c files in the folder
        List<String> fileNames = new ArrayList<>();
        for (String genModel : genModels) {
            fileNames.add(genModel + ".c");
        }

        // generate temporary files
        List<String> tmpFiles = generateTempResources(fsa);
        if (tmpFiles != null) {
            logger.debug(String.format("Copied resource files to: %s", String.join(", ", tmpFiles)));
            fileNames.addAll(tmpFiles);
        } else {
            logger.warn("Failed to copy resource files to temp folder");
        }

        // start code generation
        List<String> args = new ArrayList<>();
        args.add("-shared");
        args.add("-fPIC");

        // custom depend on os
        if (BahnUtil.isMacOS()) {
            args.add("-undefined");
            args.add("dynamic_lookup");
        }

        args.addAll(fileNames.stream().filter(f -> f.endsWith(".c")).collect(Collectors.toList()));
        args.add("-I.");
        args.add("-I" + TemporaryObjFolderName);
        args.add("-o");
        args.add(getOutputFileName());
        return executeArgs(args.toArray(new String[0]), fsa, runtimeExec);
    }

    private void preprocessHeaders(IFileSystemAccess2 fsa, String[] genModels) {
        for (String genModel : genModels) {
            logger.debug(String.format("Process tick header for: %s", genModel));

            // update ThreadStatus
            HeaderFileUtil.updateThreadStatus(fsa, genModel + ".h", ThreadStatusName, WrapperThreadStatusName);

            // update logic, tick and reset
            SourceNamingUtil.updateNaming(fsa, genModel);
        }
    }

    @Override
    protected String[] generatedFileNames() {
        return new String[] { getOutputFileName() };
    }

    private List<String> generateTempResources(IFileSystemAccess2 fsa) {
        // clean
        List<String> tempFiles = resources.stream().map(r -> getTempPath(r.getSecond())).collect(Collectors.toList());
        cleanTemp(fsa, tempFiles);

        // generate
        try {
            List<String> result = new ArrayList<>();
            for (Tuple<String, String> resource : resources) {
                try (var stream = StandardLibHelper.class.getClassLoader().getResourceAsStream(resource.getFirst())) {
                    if (stream != null) {
                        var filePath = getTempPath(resource.getSecond());
                        fsa.generateFile(filePath, stream);
                        result.add(filePath);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            logger.warn("Failed to generate temporary resources", e);
        }

        return null;
    }

    private static String getTempPath(String filname) {
        return Path.of(TemporaryObjFolderName, filname).toString();
    }

    private String getOutputFileName() {
        return String.format("%s_%s.%s", SharedLibNamePrefix, sourceFileName, getOsLibExt());
    }

    private static String getOsLibExt() {
        if (BahnUtil.isMacOS())
            return "dylib";

        if (BahnUtil.isWindows())
            return "dll";

        return "so";
    }

    private static void cleanTemp(IFileSystemAccess2 fsa, List<String> tmpFiles) {
        if (tmpFiles != null) {
            for (String tmpFile : tmpFiles) {
                fsa.deleteFile(tmpFile);
            }
        }

        fsa.deleteFile(TemporaryObjFolderName);
    }
}

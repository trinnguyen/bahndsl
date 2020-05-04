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

package de.uniba.swt.dsl.generator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.generator.externals.LibraryExternalGenerator;
import de.uniba.swt.dsl.generator.externals.LowLevelCodeExternalGenerator;
import de.uniba.swt.dsl.generator.externals.CliRuntimeExecutor;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.generator.*;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StandaloneApp {

    public static final String MODE_DEFAULT = "default";

    public static final String MODE_C_CODE = "c-code";

    public static final String MODE_LIBRARY = "library";

    private static final Logger logger = Logger.getLogger(StandaloneApp.class);

    @Inject
    private Provider<ResourceSet> resourceSetProvider;

    @Inject
    private IResourceValidator validator;

    @Inject
    private GeneratorDelegate generator;

    @Inject
    private LowLevelCodeExternalGenerator lowLevelCodeExternalGenerator;

    @Inject
    private LibraryExternalGenerator libraryGenerator;

    public boolean runGenerator(String filePath, AbstractFileSystemAccess2 fsa, String outputPath, String mode, CliRuntimeExecutor runtimeExec) {
        var resource = loadResource(filePath);
        if (resource == null) {
            System.err.println("Invalid input file: " + filePath);
            return false;
        }

        return runGenerator(resource, filePath, fsa, outputPath, mode, runtimeExec);
    }

    public boolean runGenerator(Resource resource, String filePath, AbstractFileSystemAccess2 fsa, String outputPath, String mode, CliRuntimeExecutor runtimeExec) {
        // load
        File file = new File(filePath);
        var out = outputPath;
        if (out == null || out.isEmpty()) {
            out = Paths.get(file.getAbsoluteFile().getParent(), "src-gen").toAbsolutePath().toString();
        }
        fsa.setOutputPath(out);

        // Validate the resource
        if (validateTheResource(resource))
            return false;

        logger.info(String.format("Code generation mode: %s", mode));

        // Configure and start the generator
        logger.info("Start generating network layout and SCCharts models");
        // prepare
        GeneratorContext context = new GeneratorContext();
        context.setCancelIndicator(CancelIndicator.NullImpl);

        // step 1: generate default artifacts
        generator.generate(resource, fsa, context);

        // step 2: generate low-level C-Code and dynamic library
        boolean genLibrary = MODE_LIBRARY.equalsIgnoreCase(mode);
        boolean genLowLevelCode = genLibrary || MODE_C_CODE.equalsIgnoreCase(mode);

        if (genLowLevelCode) {
            logger.info("Start generating low-level code");
            if (!lowLevelCodeExternalGenerator.generate(fsa, runtimeExec))
                return false;
        }

        if (genLibrary) {
            libraryGenerator.setSourceFileName(BahnUtil.getNameWithoutExtension(file.getName()));
            logger.info("Start generating dynamic library");
            if (!libraryGenerator.generate(fsa, runtimeExec))
                return false;
        }

        System.out.println(String.format("Code generation finished: %s", out));
        return true;
    }

    private boolean validateTheResource(Resource resource) {
        List<Issue> list = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
        if (list == null || list.isEmpty())
            return false;

        boolean anyError = false;
        for (Issue issue : list) {
            if (issue.getSeverity() == Severity.ERROR) {
                System.err.println(issue);
                anyError = true;
            } else {
                System.out.println(issue);
            }

        }

        // stop
        return anyError;
    }

    private Resource loadResource(String filePath) {
        // validate
        if (filePath == null || !Files.exists(Path.of(filePath)))
            return null;

        // load resource
        return resourceSetProvider.get().getResource(URI.createFileURI(filePath), true);
    }
}

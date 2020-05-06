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
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.generator.externals.LibraryExternalGenerator;
import de.uniba.swt.dsl.generator.externals.LowLevelCodeExternalGenerator;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestConstants;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import de.uniba.swt.dsl.tests.helpers.TestCliRuntimeExecutor;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class StandaloneAppTest {

    @Inject
    StandaloneApp standaloneApp;

    @Inject
    ResourceHelper resourceHelper;

    @Inject
    TestHelper testHelper;

    @Inject
    InMemoryFileSystemAccess fsa;

    @Inject
    TestCliRuntimeExecutor runtimeExecutor;

    @BeforeEach
    void prepare() {
        runtimeExecutor.clear();
    }

    @Test
    void runLowLevelGeneratorSuccess() throws Exception {
        var res = resourceHelper.resource(TestConstants.SampleRequestRouteForeach);
        var result = standaloneApp.runGenerator(res, "test.bahn", fsa, "test-gen", "c-code", runtimeExecutor);
        assertTrue( result, "generate sample request route in library mode");

        // ensure 2 call for two file
        assertEquals(2, runtimeExecutor.getCommands().size());

        // ensure all cli works with statebased
        ensureArgsExist(runtimeExecutor.getCommands().get(0), "kico", List.of(LowLevelCodeExternalGenerator.SCC_GEN_SYSTEM, BahnConstants.REQUEST_ROUTE_SCTX));
        ensureArgsExist(runtimeExecutor.getCommands().get(1), "kico", List.of(LowLevelCodeExternalGenerator.SCC_GEN_SYSTEM, BahnConstants.DRIVE_ROUTE_SCTX));
    }

    @Test
    void runLibraryGeneratorSuccess() throws Exception {
        var res = resourceHelper.resource(TestConstants.SampleRequestRouteForeach);
        var result = standaloneApp.runGenerator(res, "test.bahn", fsa, "test-gen", "library", runtimeExecutor);

        // check last one is cc
        assertTrue(result, "Generate library mode");
        assertTrue(runtimeExecutor.getCommands().size() > 0, "3 commands must be called for sccharts and c-compiler");

        // check last one
        // fix lib name because the server would find the lib based on name
        var cmd = runtimeExecutor.getCommands().get(runtimeExecutor.getCommands().size() - 1);
        ensureArgsExist(cmd, "cc", List.of("-shared", "-I", "-o", "libinterlocking"));
    }

    @Test
    void runLibraryGeneratorFailedNoRequestRoute() throws Exception {
        var res = resourceHelper.resource(TestConstants.SampleDriveRoute);
        var result = standaloneApp.runGenerator(res, "test.bahn", fsa, "test-gen", "library", runtimeExecutor);
        assertFalse(result, "No request_route found");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "config_empty.bahn",
            "default.bahn",
            "drive_only.bahn",
            "empty_config_request_drive.bahn",
            "empty_request_route.bahn",
            "lite.bahn",
            "request_only.bahn",
            "standard.bahn"
    })
    void successRunResourceFiles(String filename) throws Exception {
        var res = testHelper.readFromResourcePath(filename);
        var result = standaloneApp.runGenerator(res, filename, fsa, "test-gen", null, runtimeExecutor);
        assertTrue(result, "Expected run standalone again resource to be success: " + filename);
    }

    private void ensureArgsExist(Tuple<String, String[]> command, String expectedCmd, List<String> expectedArgs) {
        // check cli
        assertEquals(expectedCmd, command.getFirst());

        // check args
        var msg = Arrays.toString(command.getSecond());
        for (String expectedArg : expectedArgs) {
            if (Arrays.stream(command.getSecond()).noneMatch(a -> a.contains(expectedArg))) {
                fail(String.format("Actual args: %s doesn't contain expected arg: %s", msg, expectedArgs));
            }
        }
    }
}
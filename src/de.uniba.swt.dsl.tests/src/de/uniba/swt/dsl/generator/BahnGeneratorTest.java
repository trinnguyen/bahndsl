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
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestConstants;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class BahnGeneratorTest {

    @Inject
    InMemoryFileSystemAccess fsa;

    @Inject
    TestHelper testHelper;

    @Inject
    BahnGenerator generator;

    private final static String RequestRouteFilename = "request_route_sccharts.sctx";

    private final static String DriveRouteFilename = "drive_route_sccharts.sctx";

    @ParameterizedTest
    @ValueSource(strings = {
            "module test end",
            TestConstants.SampleEmptyConfig,
            TestConstants.SampleLayoutConfig,
            TestConstants.SampleLayoutDoubleSlipConfig,
            TestConstants.SampleLayoutCrossingConfig,
            TestConstants.SampleLiteConfig,
            TestConstants.SampleStandardConfig,
    })
    void testGenerate4YamlFiles(String src) throws Exception {
        // perform
        invokeGenerate(src);

        // verify
        var expectedNames = List.of("bidib_board_config.yml",
                "bidib_track_config.yml",
                "bidib_train_config.yml",
                "extras_config.yml");

        var files = fsa.getTextFiles();
        var msgNames = String.join(",", files.keySet());
        for (String expectedName : expectedNames) {
            var inList = files.entrySet().stream().filter(entry -> entry.getKey().endsWith(expectedName)).findFirst();
            assertTrue(inList.isPresent(), "Expected having " + expectedName + " generated. Actual list: " + msgNames);
            assertTrue(inList.get().getValue().length() > 0);
        }
    }

    @Test
    void testGenerateBoards() throws Exception {

        // perform
        invokeGenerate("module test " +
                "boards " +
                "   master 0x00 features 0x01:0x02 end " +
                "   onecontrol 0x01 " +
                "end " +
                "end");

        // check file exist
        ensureFileContent("bidib_board_config.yml", List.of("boards:", "- id: master", "- id: onecontrol"));
    }

    @Test
    void testGenerateTrackSegments() throws Exception {

        // perform
        invokeGenerate("module test " +
                "boards master 0x00 end " +
                "segments master seg1 0x00 length 11cm end " +
                "end");

        // check file exist
        ensureFileContent("bidib_track_config.yml", List.of("boards:", "- id: master", "segments:", "- id: seg1", "address: 0x00"));
    }

    @Test
    void testGenerateTrackPoints() throws Exception {

        // perform
        invokeGenerate("module test " +
                "boards master 0x00 end " +
                "segments master seg1 0x00 length 11cm end " +
                "points master point1 0x03 segment seg1 normal 0x01 reverse 0x00 initial normal end " +
                "end");

        // verify
        ensureFileContent("bidib_track_config.yml", List.of("points-board:", "point1", "- id: point1", "number: 0x03", "aspects:", "segment: seg1"));
    }

    @Test
    void testGenerateTrainsOrder1() throws Exception {

        // perform
        invokeGenerate(TestConstants.SampleTrainConfig1);

        // verify
        ensureValidTrainOutput();
    }

    @Test
    void testGenerateTrainsOrder2() throws Exception {

        // perform
        invokeGenerate(TestConstants.SampleTrainConfig2);

        // verify
        ensureValidTrainOutput();
    }

    private void ensureValidTrainOutput() throws Exception {
        ensureFileContent("bidib_train_config.yml", List.of("trains:", "- id: cargo_db", "dcc-address: 0x0001", "weight: 100.0g", "length: 7.0cm", "type: cargo"));
    }

    @Test
    void testGenerateTrackSignals() throws Exception {

        // perform
        invokeGenerate(TestConstants.SampleConfigSignals);

        // check content
        var files = fsa.getTextFiles();
        ensureFileContent("bidib_track_config.yml", List.of("signals-board:", "- id: signal1", "number: 0x03", "type: entry", "- id: signal2", "number: 0x04", "type: distant"));

        // check composite
        ensureFileContent("extras_config.yml", List.of("compositions:", "- id: signal100", "entry: signal1", "distant: signal2"));
    }

    @Test
    void testLayoutGenerateInterlocking() throws Exception {
        invokeGenerate(TestConstants.SampleLayoutConfig);

        // ensure having interlocking
        ensureFileContent("interlocking_table.yml", List.of("interlocking-table"));

        // ensure having 2 routes
        ensureFileContent("interlocking_table.yml", List.of("- id: 0 #route0", "source: sig6", "destination: sig7"));
        ensureFileContent("interlocking_table.yml", List.of("- id: 1 #route1", "source: sig5", "destination: sig7"));

        // ensure having DOT diagram file with block
        ensureFileContent("layout_diagram.dot", List.of(
                "digraph G",
                "label=\"block2\"",
                "label=\"block3\"",
                "label=\"block4\"",
                "label=\"point1.reverse\"",
                "label=\"point1.normal\""));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            TestConstants.SampleRequestRouteForeach,
            TestConstants.SampleRequestRoute,
            TestConstants.SampleRequestRouteForeach + "\n" + TestConstants.SampleDriveRoute,

            TestConstants.SampleRequestRouteEmpty,
            TestConstants.SampleInterlockingEmpty,
    })
    void testGenerateSCCharts(String src) throws Exception {
        invokeGenerate(src);

        // ensure request route is exist (mandatory)
        ensureFileContent(RequestRouteFilename, List.of("scchart Request_route"));

        // ensure drive_route is exist (optional)
        ensureFileContent(DriveRouteFilename, List.of("scchart Drive_route"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            TestConstants.SampleDriveRouteEmpty,
            TestConstants.SampleDriveRoute,
    })
    void errorGenerateSCChartsMissingRequestRoute(String src) throws Exception {
        invokeGenerate(src);

        // ensure no
        assertNull(TestHelper.getFileContent(fsa, RequestRouteFilename), "Expected request route sccharts not generated because missing required func");
        assertNull(TestHelper.getFileContent(fsa, DriveRouteFilename), "Expected drive route sccharts not generated because missing required func");
    }

    private void ensureFileContent(String name, List<String> items) throws Exception {
        TestHelper.ensureFileContent(fsa, name, items);
    }

    private void invokeGenerate(String src) throws Exception {
        Resource input = testHelper.parseValid(src);

        GeneratorContext context = new GeneratorContext();
        try {
            generator.beforeGenerate(input, fsa, context);
            generator.doGenerate(input, fsa, context);
        } finally {
            generator.afterGenerate(input, fsa, context);
        }
    }
}
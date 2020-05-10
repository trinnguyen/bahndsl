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

package cli;

import cli.util.ExternalTest;
import cli.util.ExternalTestConfig;
import de.uniba.swt.dsl.common.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SCChartsExternalTest extends ExternalTest {

    private final static List<String> SCChartsFiles = List.of(
            "request_route_sccharts.sctx",
            "drive_route_sccharts.sctx");

    @ParameterizedTest
    @ValueSource(strings = {
            "default.bahn",
            "request_only.bahn",
            "empty_request_route.bahn",
            "empty_config_request_drive.bahn"
    })
    void test2SCChartsFiles(String name) throws Exception {
        var out = TestOutputName;
        execute(List.of(getSourcePath(name), "-o", out));

        // ensure file
        ensureOutput(out, SCChartsFiles, this::validateSCChartsContent);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "config_empty.bahn",
            "standard.bahn",
            "lite.bahn"
    })
    void testNoSCCharts(String name) {
        execute(List.of(getSourcePath(name)));

        for (String filename : SCChartsFiles) {
            var path = Paths.get(ExternalTestConfig.ResourcesFolder, DefaultOutputFolderName, filename);

            // ensure files exists
            Assertions.assertFalse(Files.exists(path), "Expected file not exists " + filename);
        }
    }

    private void validateSCChartsContent(Tuple<String, String> pair) {
        var filename = pair.getFirst();
        var content = pair.getSecond();
        try {
            ensureTextContent(content, List.of("#hostcode \"#include \\\"bahn_data_util.h\\\"\""));
            if (SCChartsFiles.indexOf(filename) == 0) {
                ensureTextContent(content, List.of("scchart Request_route {",
                        "input string _src_signal_id",
                        "input string _dst_signal_id",
                        "input string _train_id",
                        "output string __out"));
            }

            if (SCChartsFiles.indexOf(filename) == 1) {
                ensureTextContent(content, List.of("scchart Drive_route {",
                        "input string _route_id",
                        "input string _train_id",
                        "input string _segment_ids ["));
            }
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }
}

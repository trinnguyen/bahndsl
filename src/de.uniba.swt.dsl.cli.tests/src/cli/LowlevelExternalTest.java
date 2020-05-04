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

import cli.util.RuntimeExternalTestHelper;
import cli.util.ExternalTest;
import de.uniba.swt.dsl.common.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

public class LowlevelExternalTest extends ExternalTest {

    private final static List<String> SourceFiles = List.of(
            "request_route.h",
            "request_route.c",
            "drive_route.h",
            "drive_route.c");

    @ParameterizedTest
    @ValueSource(strings = {
            "default.bahn",
            "request_only.bahn",
            "empty_request_route.bahn",
            "empty_config_request_drive.bahn"
    })
    void testCCodeGenerated(String name) throws Exception {
        var out = TestOutputName;
        execute(name, out);

        // ensure file
        ensureOutput(out, SourceFiles, this::validateCSourceFile);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "config_empty.bahn",
            "standard.bahn",
            "lite.bahn"
    })
    void testNoSourceGenerated(String name) {
        var res = RuntimeExternalTestHelper.execute(List.of(getSourcePath(name), "-m", "c-code"));
        Assertions.assertFalse(res, "Expected bahnc to be failed: " + name);
    }

    private void execute(String name, String out) {
        execute(List.of(getSourcePath(name), "-o", out, "-m", "c-code"));
    }

    private void validateCSourceFile(Tuple<String, String> stringStringTuple) {
    }
}

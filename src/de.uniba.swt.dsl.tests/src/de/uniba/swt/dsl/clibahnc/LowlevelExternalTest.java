package de.uniba.swt.dsl.clibahnc;

import de.uniba.swt.dsl.clibahnc.util.ExternalTest;
import de.uniba.swt.dsl.clibahnc.util.ExternalTestConfig;
import de.uniba.swt.dsl.clibahnc.util.RuntimeExternalTestHelper;
import de.uniba.swt.dsl.common.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Paths;
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

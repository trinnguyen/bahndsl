package de.uniba.swt.dsl.clibahnc;

import de.uniba.swt.dsl.clibahnc.util.ExternalTest;
import de.uniba.swt.dsl.clibahnc.util.ExternalTestConfig;
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
                ensureTextContent(content, List.of("scchart request_route {",
                        "input string src_signal_id",
                        "input string dst_signal_id",
                        "input string train_id",
                        "output string _out"));
            }

            if (SCChartsFiles.indexOf(filename) == 1) {
                ensureTextContent(content, List.of("scchart drive_route {",
                        "input string route_id",
                        "input string train_id",
                        "input string segment_ids ["));
            }
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage());
        }
    }
}

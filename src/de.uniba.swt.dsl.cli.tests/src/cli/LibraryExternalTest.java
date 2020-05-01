package cli;

import cli.util.ExternalTest;
import cli.util.ExternalTestConfig;
import cli.util.RuntimeExternalTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LibraryExternalTest extends ExternalTest {

    private final static String LibNameFormat = "libinterlocking_%s.%s";

    @ParameterizedTest
    @ValueSource(strings = {
            "default.bahn",
            "request_only.bahn",
            "empty_request_route.bahn",
            "empty_config_request_drive.bahn"
    })
    void testLibraryGenerated(String name) {
        var out = TestOutputName;
        execute(name, out);

        // ensure file
        var path = GetLibPath(name, out);
        Assertions.assertTrue(Files.isRegularFile(path), "Expected file exists " + path);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "config_empty.bahn",
            "standard.bahn",
            "lite.bahn"
    })
    void testNoSourceGenerated(String name) {
        var res = RuntimeExternalTestHelper.execute(List.of(getSourcePath(name), "-m", "library"));
        Assertions.assertFalse(res, "Expected bahnc to be failed: " + name);

        var path = GetLibPath(name, DefaultOutputFolderName);
        Assertions.assertFalse(Files.exists(path), "Expected file not exists " + path);
    }

    private void execute(String name, String out) {
        execute(List.of(getSourcePath(name), "-o", out, "-m", "library"));
    }

    private Path GetLibPath(String name,String out) {
        return Paths.get(ExternalTestConfig.ResourcesFolder, out, String.format(LibNameFormat, name.replace(".bahn", ""), getOsLibExt()));
    }

    private static String getOsLibExt() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac"))
            return "dylib";

        if (os.contains("win"))
            return "dll";

        return "so";
    }
}

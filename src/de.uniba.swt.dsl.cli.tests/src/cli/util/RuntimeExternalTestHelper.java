package cli.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class RuntimeExternalTestHelper {

    private RuntimeExternalTestHelper(){}

    public static String lastOutput = null;

    public static Boolean execute(List<String> args) {
        lastOutput = null;

        var cmdPath = ExternalTestConfig.PathToBahnC.toAbsolutePath();
        if (!Files.exists(cmdPath)) {
            fail("bahnc does not exist in path: " + cmdPath.toString());
        }

        // build args
        var cmds = buildCmdArgs(cmdPath.toString(), args);
        var dirFile = Paths.get(ExternalTestConfig.ResourcesFolder).toFile();
        try {
            var process = Runtime.getRuntime().exec(cmds, null, dirFile);

            StringBuilder builder = new StringBuilder();
            // monitor result
            String s;
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((s = stdInput.readLine()) != null) {
                    builder.append(s).append(System.lineSeparator());
                }
            }

            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while ((s = stdError.readLine()) != null) {
                    builder.append(s).append(System.lineSeparator());
                }
            }
            lastOutput = builder.toString();
            return process.waitFor() == 0;

        } catch (IOException | InterruptedException e) {
            fail(String.format("Failed to execute cli: %s, error: %s", cmdPath, e.getMessage()));
        }

        return false;
    }

    private static String[] buildCmdArgs(String cmdPath, List<String> args) {
        var list = new ArrayList<String>();
        list.add(cmdPath);
        list.addAll(args);
        var res = new String[list.size()];
        list.toArray(res);
        return res;
    }
}

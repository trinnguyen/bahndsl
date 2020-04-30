package intergrationtests.util;

import de.uniba.swt.dsl.common.util.Tuple;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TestBase {
    protected Tuple<Boolean, String> execute(String[] args) {
        var cmdPath = TestConfig.PathToBahnC.toAbsolutePath();
        if (!Files.exists(cmdPath)) {
            Assertions.fail("bahnc does not exist in path: " + cmdPath.toString());
        }

        // build args
        var cmds = buildCmdArgs(cmdPath.toString(), args);
        var dirFile = Paths.get(TestConfig.ResourcesFolder).toFile();
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
            var success = process.waitFor() == 0;
            return Tuple.of(success, builder.toString());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(String.format("Failed to execute cli: %s, error: %s", cmdPath, e.getMessage()));
        }

        return Tuple.of(false, null);
    }

    private String[] buildCmdArgs(String cmdPath, String[] args) {
        var list = new ArrayList<String>();
        list.add(cmdPath);
        list.addAll(Arrays.asList(args));
        var res = new String[list.size()];
        list.toArray(res);
        return res;
    }

    protected String getSourcePath(String name) {
        return Paths.get(TestConfig.ResourcesFolder, name).toFile().getAbsolutePath();
    }

    protected void ensureOutput(String folderName, List<String> filenames, Consumer<Tuple<String, String>> contentConsumer) throws IOException {

        for (String filename : filenames) {
            var path = Paths.get(TestConfig.ResourcesFolder, folderName, filename);

            // ensure files exists
            Assertions.assertTrue(Files.isRegularFile(path), "Expected file exists " + filename);

            // ensure not empty
            var content = Files.readString(path);
            Assertions.assertTrue(content != null && !content.isBlank(), String.format("Expected having content but file is empty: %s", filename));

            // valid file
            if (contentConsumer != null) {
                contentConsumer.accept(Tuple.of(filename, content));
            }
        }
    }

    /**
     * Ensure text contains all string items in the list
     * @param content
     * @param list
     * @throws Exception
     */
    protected void ensureTextContent(String content, List<String> list) throws Exception {
        if (content == null) {
            throw new Exception("content is null");
        }

        for (String s : list) {
            if (!content.contains(s)) {
                throw new Exception(String.format("%s \n does not contain %s", content, s));
            }
        }
    }
}

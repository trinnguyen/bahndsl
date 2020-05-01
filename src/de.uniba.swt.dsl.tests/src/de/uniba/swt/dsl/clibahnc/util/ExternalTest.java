package de.uniba.swt.dsl.clibahnc.util;

import de.uniba.swt.dsl.common.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ExternalTest {

    protected final static String DefaultOutputFolderName = "src-gen";

    protected final static String TestOutputName = "test-gen";

    protected void execute(List<String> args) {
        // execute
        var res = RuntimeExternalTestHelper.execute(args);

        // verify
        Assertions.assertTrue(res, RuntimeExternalTestHelper.lastOutput);
    }

    @BeforeEach
    void setup() {
        try {
            Files.deleteIfExists(Paths.get(DefaultOutputFolderName));
            Files.deleteIfExists(Paths.get(TestOutputName));
        } catch (IOException e) {
            System.out.println("Warning: " + e.getMessage());
        }
    }

    protected String getSourcePath(String name) {
        return Paths.get(ExternalTestConfig.ResourcesFolder, name).toFile().getAbsolutePath();
    }

    protected void ensureOutput(String folderName, List<String> filenames, Consumer<Tuple<String, String>> contentConsumer) throws IOException {

        for (String filename : filenames) {
            var path = Paths.get(ExternalTestConfig.ResourcesFolder, folderName, filename);

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

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

package cli.util;

import de.uniba.swt.dsl.common.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            deleteFolder(DefaultOutputFolderName);
            deleteFolder(TestOutputName);
        } catch (Exception e) {
            System.out.println("Warning: " + e.getMessage());
        }
    }

    private void deleteFolder(String name) {
        var file = Path.of(ExternalTestConfig.ResourcesFolder, name).toFile();
        if (file.isDirectory()) {
            var files = file.listFiles();
            if (files != null) {
                for (File item : files) {
                    item.delete();
                }
            }

            file.delete();
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

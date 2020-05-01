package de.uniba.swt.dsl.tests.helpers;

import com.google.inject.Inject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestHelper {

    @Inject
    ResourceHelper resourceHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    /**
     * Parse source code and ensure no parsing or validation error
     * @param src
     * @return
     * @throws Exception
     */
    public Resource parseValid(CharSequence src) throws Exception {
        Resource input = resourceHelper.resource(src);
        validationTestHelper.assertNoErrors(input);
        return input;
    }

    /**
     * Ensure file contains all string items in the list
     * @param fsa
     * @param fileName
     * @param list
     * @throws Exception
     */
    public static void ensureFileContent(InMemoryFileSystemAccess fsa, String fileName, List<String> list) throws Exception {
        var content = getFileContent(fsa, fileName);
        ensureTextContent(content, list);
    }

    /**
     * Ensure text contains all string items in the list
     * @param content
     * @param list
     * @throws Exception
     */
    public static void ensureTextContent(String content, List<String> list) throws Exception {
        if (content == null) {
            throw new Exception("content is null");
        }

        for (String s : list) {
            if (!content.contains(s)) {
                throw new Exception(String.format("%s \n does not contain %s", content, s));
            }
        }
    }

    /**
     * Read file content by name
     * @param fsa
     * @param name
     * @return
     */
    public static String getFileContent(InMemoryFileSystemAccess fsa, String name) {
        for (String s : fsa.getTextFiles().keySet()) {
            if (s.endsWith(name))
                return fsa.getTextFiles().get(s).toString();
        }

        return null;
    }

    public Resource readFromResourcePath(String filename) throws Exception {
        var path = Paths.get("resources", filename);
        try {
            var content = Files.readString(path);
            return resourceHelper.resource(content);
        } catch (IOException e) {
            throw new Exception("Failed to read file: " + e.getMessage());
        }

    }
}

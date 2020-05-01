package de.uniba.swt.dsl.generator.external;

import de.uniba.swt.dsl.generator.externals.HeaderFileUtil;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

public class HeaderFileUtilTest {

    InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();

    @ParameterizedTest
    @CsvSource(value = {
            "request_route.h, intern_request_route_logic, ThreadStatus",
            "drive_route.h, intern_drive_route_logic, ThreadStatus",
    })
    void testHeaderReplaceThreadEnum(String filename, String oldPrefix, String oldSuffix) throws Exception {
        String oldEnumName = oldPrefix + oldSuffix;
        var newName = "wrapper_thread_status";

        // prepare file
        var srcFmt = "typedef enum {\n" +
                "  TERMINATED,\n" +
                "  RUNNING,\n" +
                "  READY,\n" +
                "  PAUSING\n" +
                "} %s; \n" +
                "void (%s status);";
        var src = String.format(srcFmt, oldEnumName, oldEnumName);
        fsa.generateFile(filename, src);

        // invoke
        HeaderFileUtil.updateThreadStatus(fsa, filename, oldPrefix, oldSuffix, newName);

        // verify by checking if the tickwrapper header is included
        var content = TestHelper.getFileContent(fsa, filename);
        TestHelper.ensureTextContent(content, List.of("#include", "tick_wrapper.h", String.format("void (%s status)", newName)));

        // ensure enum are gone
        Assertions.assertFalse(content.contains(oldEnumName), String.format("Expected enum '%s' to be removed", oldEnumName));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "request_route.h, intern_request_route_logic, ThreadStatus",
            "drive_route.h, intern_drive_route_logic, ThreadStatus",
    })
    void testHeaderReplaceEmptyFile(String filename, String oldPrefix, String oldSuffix) throws Exception {
        var newName = "wrapper_thread_status";
        var src = "void test();";
        fsa.generateFile(filename, src);

        // invoke
        HeaderFileUtil.updateThreadStatus(fsa, filename, oldPrefix, oldSuffix, newName);

        // ensure empty
        Assertions.assertEquals(src, TestHelper.getFileContent(fsa, filename));
    }
}
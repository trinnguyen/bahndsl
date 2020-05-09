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
            "request_route.h, ThreadStatus",
            "drive_route.h, ThreadStatus",
    })
    void testHeaderReplaceThreadEnum(String filename, String oldEnumName) throws Exception {
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
        HeaderFileUtil.updateThreadStatus(fsa, filename, oldEnumName, newName);

        // verify by checking if the tickwrapper header is included
        var content = TestHelper.getFileContent(fsa, filename);
        TestHelper.ensureTextContent(content, List.of("#include", "tick_wrapper.h", String.format("void (%s status)", newName)));

        // ensure enum are gone
        Assertions.assertFalse(content.contains(oldEnumName), String.format("Expected enum '%s' to be removed", oldEnumName));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "request_route.h, ThreadStatus",
            "drive_route.h, ThreadStatus",
    })
    void testHeaderReplaceEmptyFile(String filename, String oldName) throws Exception {
        var newName = "wrapper_thread_status";
        var src = "void test();";
        fsa.generateFile(filename, src);

        // invoke
        HeaderFileUtil.updateThreadStatus(fsa, filename, oldName, newName);

        // ensure empty
        Assertions.assertEquals(src, TestHelper.getFileContent(fsa, filename));
    }
}
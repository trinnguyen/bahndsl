package de.uniba.swt.dsl.generator.cli;

import de.uniba.swt.dsl.generator.StandaloneApp;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArgOptionContainerTest {

    @Test
    public void testArgsHelp() {
        var container = createContainer();
        var help = container.formatHelp("test");

        // verify
        assertTrue(help.contains("USAGE:"));
        assertTrue(help.contains("-o"));
        assertTrue(help.contains("-m"));
        assertTrue(help.contains("-v"));
        assertTrue(help.contains("-d"));
    }

    @Test
    public void testParse() throws Exception {
        var container = createContainer();
        ArgParseResult result = container.parse(new String[]{"-o", "test-gen"}, 0);
        assertTrue(result.hasOption("o"));
        assertEquals("test-gen", result.getValue("o", ""));

        // not found
        assertEquals("", result.getValue("v", ""));
    }

    private static ArgOptionContainer createContainer() {
        var modeDesc = String.format("code generation mode (%s, %s, %s)", StandaloneApp.MODE_DEFAULT, StandaloneApp.MODE_C_CODE, StandaloneApp.MODE_LIBRARY);
        return new ArgOptionContainer(List.of(
                new ArgOption("o", "output folder", true, "path"),
                new ArgOption("m", modeDesc, true, "mode"),
                new ArgOption("v", "verbose output"),
                new ArgOption("d", "debug output")));
    }
}
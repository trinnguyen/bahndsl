package de.uniba.swt.dsl.generator;

import com.google.inject.Inject;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class MainTest {
    @Inject
    TestHelper testHelper;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    @Test
    void testMainNoArgs() throws Exception {
        // perform
        Main.main(new String[]{});

        // show help since no params
        testHelper.ensureTextContent(outContent.toString(), List.of("OVERVIEW: Bahn compiler", "USAGE:", "EXAMPLE:"));
    }

    @Test
    void testMainMissingFile() throws Exception {
        // perform
        Main.main(new String[]{"-v", "-o", "test-gen"});

        // show help since no params
        testHelper.ensureTextContent(outContent.toString(), List.of("OVERVIEW: Bahn compiler", "USAGE:", "EXAMPLE:"));
    }
}
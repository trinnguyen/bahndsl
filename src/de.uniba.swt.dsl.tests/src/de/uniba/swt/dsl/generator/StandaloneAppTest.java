package de.uniba.swt.dsl.generator;

import com.google.inject.Inject;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.generator.externals.LibraryExternalGenerator;
import de.uniba.swt.dsl.generator.externals.LowLevelCodeExternalGenerator;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestConstants;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import de.uniba.swt.dsl.tests.helpers.TestCliRuntimeExecutor;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class StandaloneAppTest {

    @Inject
    StandaloneApp standaloneApp;

    @Inject
    ResourceHelper resourceHelper;

    @Inject
    TestHelper testHelper;

    @Inject
    InMemoryFileSystemAccess fsa;

    @Inject
    TestCliRuntimeExecutor runtimeExecutor;

    @BeforeEach
    void prepare() {
        runtimeExecutor.clear();
    }

    @Test
    void runLowLevelGeneratorSuccess() throws Exception {
        var res = resourceHelper.resource(TestConstants.SampleRequestRouteForeach);
        var result = standaloneApp.runGenerator(res, "test.bahn", fsa, "test-gen", "c-code", runtimeExecutor);
        assertTrue( result, "generate sample request route in library mode");

        // ensure 2 call for two file
        assertEquals(2, runtimeExecutor.getCommands().size());

        // ensure all cli works with statebased
        ensureArgsExist(runtimeExecutor.getCommands().get(0), "kico", List.of(LowLevelCodeExternalGenerator.SCC_GEN_SYSTEM, BahnConstants.REQUEST_ROUTE_SCTX));
        ensureArgsExist(runtimeExecutor.getCommands().get(1), "kico", List.of(LowLevelCodeExternalGenerator.SCC_GEN_SYSTEM, BahnConstants.DRIVE_ROUTE_SCTX));
    }

    @Test
    void runLibraryGeneratorSuccess() throws Exception {
        var res = resourceHelper.resource(TestConstants.SampleRequestRouteForeach);
        var result = standaloneApp.runGenerator(res, "test.bahn", fsa, "test-gen", "library", runtimeExecutor);

        // check last one is cc
        assertTrue(result, "Generate library mode");
        assertTrue(runtimeExecutor.getCommands().size() > 0, "3 commands must be called for sccharts and c-compiler");

        // check last one
        // fix lib name because the server would find the lib based on name
        var cmd = runtimeExecutor.getCommands().get(runtimeExecutor.getCommands().size() - 1);
        ensureArgsExist(cmd, "cc", List.of("-shared", "-I", "-o", "libinterlocking"));
    }

    private void ensureArgsExist(Tuple<String, String[]> command, String expectedCmd, List<String> expectedArgs) {
        // check cli
        assertEquals(expectedCmd, command.getFirst());

        // check args
        var msg = Arrays.toString(command.getSecond());
        for (String expectedArg : expectedArgs) {
            if (Arrays.stream(command.getSecond()).noneMatch(a -> a.contains(expectedArg))) {
                fail(String.format("Actual args: %s doesn't contain expected arg: %s", msg, expectedArgs));
            }
        }
    }
}
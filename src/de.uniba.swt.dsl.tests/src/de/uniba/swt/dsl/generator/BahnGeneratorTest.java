package de.uniba.swt.dsl.generator;

import com.google.inject.Inject;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class BahnGeneratorTest {

    @Inject
    ResourceHelper resourceHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    @Inject
    BahnGenerator generator;

    @Inject
    InMemoryFileSystemAccess fsa;

    @Test
    void testGenerate4YamlFiles() throws Exception {
        // perform
        invokeGenerate("module test end");

        // verify
        var expectedNames = List.of("bidib_board_config.yml",
                "bidib_track_config.yml",
                "bidib_train_config.yml",
                "extras_config.yml");
        var msgNames = String.join(",", expectedNames);

        // ensure all 4 files are exist with non-empty data
        var files = fsa.getTextFiles();
        for (var entry : files.entrySet()) {
            var inList = expectedNames.stream().anyMatch(i -> entry.getKey().endsWith(i));
            assertTrue(inList, () -> "Actual " + entry.getKey() + ", expected one of the: " + msgNames);
            assertTrue(entry.getValue().length() > 0);
        }
    }

    @Test
    void testGenerateBoards() throws Exception {

        // perform
        invokeGenerate("module test " +
                "boards " +
                "   master 0x00 features 0x01:0x02 end " +
                "   onecontrol 0x01 " +
                "end " +
                "end");

        // check file exist
        var files = fsa.getTextFiles();
        var boardContent = getFile(files, "bidib_board_config.yml");
        ensureContains(boardContent, List.of("boards:", "- id: master", "- id: onecontrol"));
    }

    @Test
    void testGenerateTrackSegments() throws Exception {

        // perform
        invokeGenerate("module test " +
                "boards master 0x00 end " +
                "segments master seg1 0x00 length 11cm end " +
                "end");

        // check file exist
        var files = fsa.getTextFiles();
        var content = getFile(files, "bidib_track_config.yml");
        ensureContains(content, List.of("boards:", "- id: master", "segments:", "- id: seg1", "address: 0x00"));
    }

    @Test
    void testGenerateTrackPoints() throws Exception {

        // perform
        invokeGenerate("module test " +
                "boards master 0x00 end " +
                "segments master seg1 0x00 length 11cm end " +
                "points master point1 0x03 segment seg1 normal 0x01 reverse 0x00 initial normal end " +
                "end");

        // check file exist
        var files = fsa.getTextFiles();
        var content = getFile(files, "bidib_track_config.yml");
        ensureContains(content, List.of("points-board:", "point1", "- id: point1", "number: 0x03", "aspects:", "segment: seg1"));
    }

    @Test
    void testGenerateTrackSignals() throws Exception {

        // perform
        invokeGenerate("module test " +
                "boards master 0x00 end " +
                "signals master" +
                "   entry signal1 0x03 " +
                "   distant signal2 0x04 " +
                "   composite signal100 signals signal1 signal2 end " +
                "end " +
                "end");

        // check content
        var files = fsa.getTextFiles();
        var content = getFile(files, "bidib_track_config.yml");
        ensureContains(content, List.of("signals-board:", "- id: signal1", "number: 0x03", "type: entry", "- id: signal2", "number: 0x04", "type: distant"));

        // check composite
        var contentExtras = getFile(files, "extras_config.yml");
        ensureContains(contentExtras, List.of("compositions:", "- id: signal100", "entry: signal1", "distant: signal2"));
    }

    @Test
    void testGenerateSCChartsRequestRoute() throws Exception {
        invokeGenerate("def request_route(string src_signal_id, string dst_signal_id, string train_id): string return \"\" end");

        var files = fsa.getTextFiles();
        var contentReq = getFile(files, "request_route_sccharts.sctx");
        ensureContains(contentReq, List.of("scchart request_route"));
    }

    @Test
    void testGenerateSCChartsDriveRoute() throws Exception {
        invokeGenerate("def request_route(string src_signal_id, string dst_signal_id, string train_id): string return \"\" end " +
                "def drive_route(string route_id, string train_id, string segment_ids[]) end");

        var files = fsa.getTextFiles();

        // ensure request route is exist (mandatory)
        var contentReq = getFile(files, "request_route_sccharts.sctx");
        ensureContains(contentReq, List.of("scchart request_route"));

        // ensure drive_route is exist (optional)
        var contentDrive = getFile(files, "drive_route_sccharts.sctx");
        ensureContains(contentDrive, List.of("scchart drive_route"));
    }

    private String getFile(Map<String, CharSequence> files, String name) {
        for (String s : files.keySet()) {
            if (s.endsWith(name))
                return files.get(s).toString();
        }

        return null;
    }

    private void invokeGenerate(String src) throws Exception {
        Resource input = resourceHelper.resource(src);
        validationTestHelper.assertNoErrors(input);

        GeneratorContext context = new GeneratorContext();
        try {
            generator.beforeGenerate(input, fsa, context);
            generator.doGenerate(input, fsa, context);
        } finally {
            generator.afterGenerate(input, fsa, context);
        }
    }

    private static void ensureContains(String content, List<String> list) throws Exception {
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
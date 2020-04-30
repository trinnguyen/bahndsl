package intergrationtests;

import de.uniba.swt.dsl.common.util.Tuple;
import intergrationtests.util.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConfigTest extends TestBase {

    private final static List<String> YamlFiles = List.of(
            "bidib_board_config.yml",
            "bidib_track_config.yml",
            "bidib_train_config.yml");

    private final static String DefaultOutputFolderName = "src-gen";

    @ParameterizedTest
    @ValueSource(strings = {
            "config_empty.bahn",
            "standard.bahn",
            "lite.bahn",
    })
    void test3YamlFileValid(String name) throws Exception {
        var res = execute(new String[]{getSourcePath(name)});

        // success
        Assertions.assertTrue(res.getFirst(), res.getSecond());

        // ensure file
        ensureOutput(DefaultOutputFolderName, YamlFiles, this::validateYamlContent);
    }

    private void validateYamlContent(Tuple<String, String> pair) {
        Yaml yaml = new Yaml();
        Map<Object, Object> document = yaml.load(pair.getSecond());

        // ensure valid yaml
        Assertions.assertNotNull(document);

        String filename = pair.getFirst();
        // board
        if (YamlFiles.indexOf(filename) == 0) {
            Assertions.assertTrue(document.containsKey("boards"));
        }

        // track
        if (YamlFiles.indexOf(filename) == 1) {
            Assertions.assertTrue(document.containsKey("boards"));
        }

        // train
        if (YamlFiles.indexOf(filename) == 2) {
            Assertions.assertTrue(document.containsKey("trains"));
        }
    }
}

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

package cli;

import cli.util.ExternalTest;
import de.uniba.swt.dsl.common.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;


public class ConfigExternalTest extends ExternalTest {

    private final static List<String> YamlFiles = List.of(
            "bidib_board_config.yml",
            "bidib_track_config.yml",
            "bidib_train_config.yml");

    @ParameterizedTest
    @ValueSource(strings = {
            "config_empty.bahn",
            "empty_config_request_drive.bahn",
            "standard.bahn",
            "lite.bahn",
    })
    void test3YamlFileValid(String name) throws Exception {
        execute(List.of(getSourcePath(name)));

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

package de.uniba.swt.dsl.common.generator.yaml;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.RootModule;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class YamlConfigGenerator implements GeneratorProvider {
    @Inject
    BoardYamlExporter boardYamlExporter;

    @Inject
    TrackYamlExporter trackYamlExporter;

    @Inject
    TrainYamlExporter trainYamlExporter;

    @Inject
    BlockYamlExporter blockYamlExporter;

    @Override
    public void run(IFileSystemAccess2 fsa, RootModule rootModule) {
        // bidib_board_config
        fsa.generateFile("bidib_board_config.yml", boardYamlExporter.export(rootModule));

        // bidib_track_config
        fsa.generateFile("bidib_track_config.yml", trackYamlExporter.export(rootModule));

        // bidib_train_config
        fsa.generateFile("bidib_train_config.yml", trainYamlExporter.export(rootModule));

        // block
        fsa.generateFile("block_config.yml", blockYamlExporter.export(rootModule));
    }
}

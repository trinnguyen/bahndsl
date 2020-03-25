package de.uniba.swt.dsl.common.generator.yaml;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.BahnModel;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.util.BahnUtil;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class YamlConfigGenerator extends GeneratorProvider {

    private static final String BoardFileName = "bidib_board_config.yml";

    private static final String TrackFileName = "bidib_track_config.yml";

    private static final String TrainFileName = "bidib_train_config.yml";

    private static final String ExtrasConfigFileName = "extras_config.yml";

    @Inject
    BoardYamlExporter boardYamlExporter;

    @Inject
    TrackYamlExporter trackYamlExporter;

    @Inject
    TrainYamlExporter trainYamlExporter;

    @Inject
    ExtrasYamlExporter extrasYamlExporter;
    private NetworkLayout networkLayout;

    @Override
    protected void execute(IFileSystemAccess2 fsa, BahnModel bahnModel) {
        var rootModule = BahnUtil.getRootModule(bahnModel);
        if (rootModule == null)
            return;

        // bidib_board_config
        fsa.generateFile(BoardFileName, boardYamlExporter.export(rootModule));

        // bidib_track_config
        fsa.generateFile(TrackFileName, trackYamlExporter.export(rootModule));

        // bidib_train_config
        fsa.generateFile(TrainFileName, trainYamlExporter.export(rootModule));

        // block
        fsa.generateFile(ExtrasConfigFileName, extrasYamlExporter.export(rootModule, networkLayout));
    }

    public void setNetworkLayout(NetworkLayout networkLayout) {
        this.networkLayout = networkLayout;
    }

    @Override
    protected String[] generatedFileNames() {
        return new String[]{ BoardFileName, TrackFileName, TrainFileName, ExtrasConfigFileName };
    }
}

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

package de.uniba.swt.dsl.common.generator.yaml;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.BahnModel;
import de.uniba.swt.dsl.common.fsa.FsaUtil;
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
    protected void execute(IFileSystemAccess2 fsa, BahnModel bahnModel, String routeType) {
        var rootModule = BahnUtil.getRootModule(bahnModel);
        if (rootModule == null)
            return;

        final String path = FsaUtil.getFolderPath(fsa);

        // bidib_board_config
        boardYamlExporter.export(path, BoardFileName, rootModule);

        // bidib_track_config
        trackYamlExporter.export(path, TrackFileName, rootModule);

        // bidib_train_config
        trainYamlExporter.export(path, TrainFileName, rootModule);

        // extras_config
        extrasYamlExporter.export(path, ExtrasConfigFileName, rootModule, networkLayout);
    }

    public void setNetworkLayout(NetworkLayout networkLayout) {
        this.networkLayout = networkLayout;
    }

    @Override
    protected String[] generatedFileNames() {
        return new String[]{ BoardFileName, TrackFileName, TrainFileName, ExtrasConfigFileName };
    }
}

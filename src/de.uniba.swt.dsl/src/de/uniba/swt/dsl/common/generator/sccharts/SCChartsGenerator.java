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

package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.generator.sccharts.builder.RootStateBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.RootState;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.validators.SwtBahnFuncValidator;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class SCChartsGenerator extends GeneratorProvider {

    private static final Logger logger = Logger.getLogger(SCChartsGenerator.class);

    @Inject SCChartsTextualBuilder builder;

    @Inject
    SwtBahnFuncValidator swtBahnFuncValidator;

    @Override
    protected String[] generatedFileNames() {
        return new String[]{ BahnConstants.REQUEST_ROUTE_SCTX, BahnConstants.DRIVE_ROUTE_SCTX};
    }

    @Override
    protected void execute(IFileSystemAccess2 fsa, BahnModel bahnModel) {
        // get decls
        FuncDecl declRequest = null;
        FuncDecl declDrive = null;
        for (Component component : bahnModel.getComponents()) {
            if (component instanceof FuncDecl && component.getName().equalsIgnoreCase(BahnConstants.REQUEST_ROUTE_FUNC_NAME)) {
                declRequest = (FuncDecl) component;
                continue;
            }

            if (component instanceof FuncDecl && component.getName().equalsIgnoreCase(BahnConstants.DRIVE_ROUTE_FUNC_NAME)) {
                declDrive = (FuncDecl) component;
            }

            if (declRequest != null && declDrive != null)
                break;
        }

        // generate request and drive models
        generateModel(fsa, declRequest, BahnConstants.REQUEST_ROUTE_SCTX);

        if (declDrive != null) {
            generateModel(fsa, declDrive, BahnConstants.DRIVE_ROUTE_SCTX);
        }
    }

    private void generateModel(IFileSystemAccess2 fsa, FuncDecl decl, String fileName) {
        List<RootState> refStates = generateReferencedStates(decl);
        var scchart = createRootState(decl);

        // print
        logger.debug("Generate SCCharts for " + scchart.getId());

        fsa.generateFile(fileName, builder.buildString(scchart, refStates));
    }

    private List<RootState> generateReferencedStates(FuncDecl decl) {
        var set = new HashSet<FuncDecl>();
        BahnUtil.findCalledFunctions(set, decl);
        return set.stream().map(this::createRootState).collect(Collectors.toList());
    }

    private RootState createRootState(FuncDecl decl) {
        RootStateBuilder rootStateBuilder = new RootStateBuilder(decl);
        rootStateBuilder.build();
        return rootStateBuilder.getRootState();
    }
}

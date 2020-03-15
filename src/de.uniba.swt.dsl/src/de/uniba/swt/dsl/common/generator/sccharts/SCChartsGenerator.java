package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartModelBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.normalization.ArrayNormalizer;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class SCChartsGenerator implements GeneratorProvider {

    private static final Logger logger = Logger.getLogger(SCChartsGenerator.class);

    @Inject SCChartsTextualBuilder builder;

    @Inject
    ArrayNormalizer normalizer;

    @Inject
    SCChartModelBuilder modelBuilder;

    @Override
    public void run(IFileSystemAccess2 fsa, RootModule rootModule) {
        SCCharts models = modelBuilder.createModel(rootModule);
        logger.debug(models);
        fsa.generateFile("interlocking_sccharts.sctx", builder.buildString(models));
    }
}

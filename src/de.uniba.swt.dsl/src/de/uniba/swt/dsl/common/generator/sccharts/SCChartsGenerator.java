package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartModelBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnException;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SCChartsGenerator {

    private static final Logger logger = Logger.getLogger(SCChartsGenerator.class);

    @Inject SCChartsTextualBuilder builder;

    @Inject
    SCChartsNormalizer normalizer;

    @Inject
    SCChartModelBuilder modelBuilder;


    public String generate(RootModule rootModule) {
        SCCharts models = modelBuilder.buildModel(normalizer.normalizeModule(rootModule));
        logger.debug(models);
        return builder.buildString(models);
    }
}

package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartModelBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.normalization.ArrayNormalizer;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.List;

public class SCChartsGenerator {

    @Inject SCChartsTextualBuilder builder;

    @Inject
    SCChartModelBuilder modelBuilder;

    public void run(IFileSystemAccess2 fsa, List<FuncDecl> decls) {
        if (decls == null || decls.size() == 0)
            return;

        SCCharts models = modelBuilder.createModel(decls);
        RootState state = models.getRootStates().stream().filter(s -> s.getId().equalsIgnoreCase(BahnConstants.GEN_REQUEST_ROUTE_DYNAMIC_NAME)).findFirst().orElse(null);
        if (state != null)
            fsa.generateFile(BahnConstants.GEN_SCCHARTS_FILE_NAME, builder.buildString(state));
    }
}

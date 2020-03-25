package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartModelBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.validators.SwtBahnFuncValidator;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class SCChartsGenerator extends GeneratorProvider {

    private static Logger logger = Logger.getLogger(SCChartsGenerator.class);

    @Inject SCChartsTextualBuilder builder;

    @Inject
    SCChartModelBuilder modelBuilder;

    @Inject
    SwtBahnFuncValidator swtBahnFuncValidator;

    @Override
    protected String[] generatedFileNames() {
        return new String[]{ BahnConstants.REQUEST_ROUTE_SCTX, BahnConstants.DRIVE_ROUTE_SCTX};
    }

    @Override
    protected void execute(IFileSystemAccess2 fsa, BahnModel bahnModel) {
        var result = swtBahnFuncValidator.hasRequestAndDriveRoute(bahnModel);
        if (!result.getFirst()) {
            logger.warn("Missing function for requesting route. SCCharts code generation is skipped.");
            return;
        }

        if (!result.getSecond()) {
            logger.debug("Adding empty driving route function");
            addEmptyDriveRoute(bahnModel);
        }

        var decls = BahnUtil.getDecls(bahnModel.eResource().getResourceSet());
        if (decls == null || decls.size() == 0)
            return;

        SCCharts models = modelBuilder.createModel(decls);
        for (RootState state : models.getRootStates()) {

            if (state.getId().equals(BahnConstants.REQUEST_ROUTE_FUNC_NAME)) {
                logger.debug("Generate SCCharts for " + state.getId());
                fsa.generateFile(BahnConstants.REQUEST_ROUTE_SCTX, builder.buildString(state));
                continue;
            }

            if (state.getId().equals(BahnConstants.DRIVE_ROUTE_FUNC_NAME)) {
                logger.debug("Generate SCCharts for " + state.getId());
                fsa.generateFile(BahnConstants.DRIVE_ROUTE_SCTX, builder.buildString(state));
            }
        }
    }

    private void addEmptyDriveRoute(BahnModel bahnModel) {
        FuncDecl decl = swtBahnFuncValidator.generateDriveRouteFuncDecl();
        bahnModel.getComponents().add(decl);
    }
}

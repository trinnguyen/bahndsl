package de.uniba.swt.dsl.common.generator.sccharts;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.generator.sccharts.builder.RootStateBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.builder.SCChartsTextualBuilder;
import de.uniba.swt.dsl.common.generator.sccharts.models.RootState;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.validation.validators.SwtBahnFuncValidator;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class SCChartsGenerator extends GeneratorProvider {

    private static Logger logger = Logger.getLogger(SCChartsGenerator.class);

    @Inject SCChartsTextualBuilder builder;

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

        // generate
        var rootRequest = createRootState(declRequest);
        logger.debug("Generate SCCharts for " + rootRequest.getId());
        fsa.generateFile(BahnConstants.REQUEST_ROUTE_SCTX, builder.buildString(rootRequest));

        // drive
        if (declDrive != null) {
            var rootDrive = createRootState(declDrive);
            logger.debug("Generate SCCharts for " + rootDrive.getId());
            fsa.generateFile(BahnConstants.DRIVE_ROUTE_SCTX, builder.buildString(rootDrive));
        }
    }

    private RootState createRootState(FuncDecl decl) {
        RootStateBuilder rootStateBuilder = new RootStateBuilder(decl);
        rootStateBuilder.build();
        return rootStateBuilder.getRootState();
    }
}

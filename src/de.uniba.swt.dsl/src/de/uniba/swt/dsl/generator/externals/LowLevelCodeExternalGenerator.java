package de.uniba.swt.dsl.generator.externals;

import de.uniba.swt.dsl.common.util.BahnConstants;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

public class LowLevelCodeExternalGenerator extends ExternalGenerator {

    private static final String SCC_GEN_SYSTEM = "de.cau.cs.kieler.sccharts.statebased";

    private static final Logger logger = Logger.getLogger(LowLevelCodeExternalGenerator.class);

    @Override
    protected String[] supportedTools() {
        return new String[]{ "kico", "scc" };
    }

    @Override
    protected boolean execute(String outputPath) {
        return executeFile(outputPath, BahnConstants.REQUEST_ROUTE_SCTX)
                && executeFile(outputPath, BahnConstants.DRIVE_ROUTE_SCTX);
    }

    private boolean executeFile(String outputPath, String input) {
        var path = Paths.get(outputPath, input);
        if (!Files.exists(path)) {
            logger.debug("File is not exist: " + input);
            return false;
        }

        // start code generation
        var args = new String[]{"-s", SCC_GEN_SYSTEM, "-o", ".", input};
        return executeArgs(args, outputPath);
    }

    @Override
    protected String[] generatedFileNames() {
        return new String[] {
                BahnConstants.REQUEST_ROUTE_FUNC_NAME + ".h",
                BahnConstants.REQUEST_ROUTE_FUNC_NAME + ".c",
                BahnConstants.DRIVE_ROUTE_FUNC_NAME + ".h",
                BahnConstants.DRIVE_ROUTE_FUNC_NAME + ".c",
        };
    }
}

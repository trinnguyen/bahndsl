package de.uniba.swt.dsl.generator.externals;

import de.uniba.swt.dsl.common.util.BahnConstants;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class LowLevelCodeExternalGenerator extends ExternalGenerator {

    public static final String SCC_GEN_SYSTEM = "de.cau.cs.kieler.sccharts.statebased";

    private static final Logger logger = Logger.getLogger(LowLevelCodeExternalGenerator.class);

    @Override
    protected String[] supportedTools() {
        return new String[]{ "kico", "scc" };
    }

    @Override
    protected boolean execute(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec) {
        return executeFile(fsa, runtimeExec, BahnConstants.REQUEST_ROUTE_SCTX)
                && executeFile(fsa, runtimeExec, BahnConstants.DRIVE_ROUTE_SCTX);
    }

    private boolean executeFile(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec, String input) {
        if (!fsa.isFile(input)) {
            logger.debug("File is not exist: " + input);
            return false;
        }

        // start code generation
        var args = new String[]{"-s", SCC_GEN_SYSTEM, "-o", ".", input};
        return executeArgs(args, fsa, runtimeExec);
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

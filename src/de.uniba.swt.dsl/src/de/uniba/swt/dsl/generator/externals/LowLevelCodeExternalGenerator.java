package de.uniba.swt.dsl.generator.externals;

import de.uniba.swt.dsl.common.util.BahnConstants;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LowLevelCodeExternalGenerator extends AbstractExternalGenerator {

    private final String GENERATION_SYSTEM = "de.cau.cs.kieler.sccharts.statebased";

    private static Logger logger = Logger.getLogger(LowLevelCodeExternalGenerator.class);

    @Override
    protected String[] supportedTools() {
        return new String[]{ "kico", "scc" };
    }

    @Override
    public boolean generate(String outputPath) {
        String inputFileName = BahnConstants.GEN_SCCHARTS_FILE_NAME;
        var path = Paths.get(outputPath, inputFileName);
        if (!Files.exists(path)) {
            logger.error(String.format("Generated SCCharts file is not exist: %s", inputFileName));
            return false;
        }

        // start code generation
        var args = new String[]{"-s", GENERATION_SYSTEM, "-o", ".", inputFileName};
        return executeArgs(args, outputPath);
    }
}

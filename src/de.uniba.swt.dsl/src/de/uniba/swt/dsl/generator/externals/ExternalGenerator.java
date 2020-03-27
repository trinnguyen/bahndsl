package de.uniba.swt.dsl.generator.externals;

import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public abstract class ExternalGenerator {

    private static Logger logger = Logger.getLogger(ExternalGenerator.class);

    protected abstract String[] supportedTools();

    protected boolean executeArgs(String[] args, String outputPath) {
        for (String cli : supportedTools()) {
            try {
                int res = internalExecuteCli(cli, args, outputPath);
                return res == 0;
            } catch (IOException e) {
                logger.debug(String.format("Failed to execute %s, error: %s", cli, e.getMessage()));
            }
        }

        System.err.println(String.format("None of the command lines exist: %s", Arrays.toString(supportedTools())));
        return false;
    }

    protected int internalExecuteCli(String command, String[] args, String workingDir) throws IOException {
        try {
            var argLen = args != null ? args.length : 0;
            String[] cmd = new String[argLen + 1];
            cmd[0] = command;
            if (args != null)
                System.arraycopy(args, 0, cmd, 1, args.length);

            logger.info(String.format("Execute: %s", String.join(" ", cmd)));

            // execute
            var dir = workingDir != null ? new File(workingDir) : null;
            var process = Runtime.getRuntime().exec(cmd, null, dir);

            String s;
            // monitor result
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((s = stdInput.readLine()) != null) {
                    logger.info(s);
                }
            }

            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
            }

            return process.waitFor();
        } catch (InterruptedException e) {
            logger.warn(e.getMessage(), e);
        }

        return -1;
    }

    public boolean generate(String outputPath) {
        cleanUp(outputPath);
        return execute(outputPath);
    }

    protected abstract boolean execute(String outputPath);

    /**
     * Remove previous generated file
     */
    protected void cleanUp(String outputPath) {
        var names = generatedFileNames();
        if (names != null) {
            for (String name : names) {
                try {
                    Files.deleteIfExists(Paths.get(outputPath, name));
                } catch (IOException e) {
                    logger.debug("Failed to delete file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Generated file names used for cleaning up
     * @return file names
     */
    protected abstract String[] generatedFileNames();
}

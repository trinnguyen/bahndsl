package de.uniba.swt.dsl.generator.externals;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public abstract class AbstractExternalGenerator {

    private static Logger logger = Logger.getLogger(AbstractExternalGenerator.class);

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

            logger.info(String.format("Execute: %s; folder: %s", String.join(" ", cmd), workingDir));

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

    public abstract boolean generate(String outputPath);
}

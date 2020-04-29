package de.uniba.swt.dsl.generator.externals;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RuntimeCliExecutor {

    private static final Logger logger = Logger.getLogger(RuntimeCliExecutor.class);

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
}

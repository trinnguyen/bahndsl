package de.uniba.swt.dsl.generator.externals;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.AbstractFileSystemAccess;
import org.eclipse.xtext.generator.AbstractFileSystemAccess2;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public abstract class ExternalGenerator {

    @Inject
    RuntimeCliExecutor executor;

    private static final Logger logger = Logger.getLogger(ExternalGenerator.class);

    protected abstract String[] supportedTools();

    protected boolean executeArgs(String[] args, IFileSystemAccess2 fsa) {
        for (String cli : supportedTools()) {
            try {
                int res = executor.internalExecuteCli(cli, args, getDefaultOutputPath(fsa));
                return res == 0;
            } catch (IOException e) {
                logger.debug(String.format("Failed to execute %s, error: %s", cli, e.getMessage()));
            }
        }

        System.err.println(String.format("None of the command lines exist: %s", Arrays.toString(supportedTools())));
        return false;
    }

    private String getDefaultOutputPath(IFileSystemAccess2 fsa) {
        if (fsa instanceof AbstractFileSystemAccess) {
            var concreteFsa = (AbstractFileSystemAccess2) fsa;
            var map = concreteFsa.getOutputConfigurations();
            var key = AbstractFileSystemAccess.DEFAULT_OUTPUT;
            return (map != null && map.containsKey(key)) ? map.get(key).getOutputDirectory() : "";
        }

        return "";
    }


    public boolean generate(IFileSystemAccess2 fsa) {
        cleanUp(fsa);
        return execute(fsa);
    }

    protected abstract boolean execute(IFileSystemAccess2 fsa);

    /**
     * Remove previous generated file
     */
    protected void cleanUp(IFileSystemAccess2 fsa) {
        var names = generatedFileNames();
        if (names != null) {
            for (String name : names) {
                fsa.deleteFile(name);
            }
        }
    }

    /**
     * Generated file names used for cleaning up
     * @return file names
     */
    protected abstract String[] generatedFileNames();
}

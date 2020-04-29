package de.uniba.swt.dsl.generator.externals;

import org.eclipse.xtext.generator.AbstractFileSystemAccess;
import org.eclipse.xtext.generator.AbstractFileSystemAccess2;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.Arrays;

public abstract class ExternalGenerator {

    protected abstract String[] supportedTools();

    protected boolean executeArgs(String[] args, IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec) {
        for (String cli : supportedTools()) {
            if (runtimeExec.internalExecuteCli(cli, args, getDefaultOutputPath(fsa)))
                return true;
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


    public boolean generate(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec) {
        cleanUp(fsa);
        return execute(fsa, runtimeExec);
    }

    protected abstract boolean execute(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec);

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

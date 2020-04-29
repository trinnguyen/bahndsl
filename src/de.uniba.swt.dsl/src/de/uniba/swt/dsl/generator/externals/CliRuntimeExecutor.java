package de.uniba.swt.dsl.generator.externals;

public abstract class CliRuntimeExecutor {
    protected abstract boolean internalExecuteCli(String command, String[] args, String workingDir);
}


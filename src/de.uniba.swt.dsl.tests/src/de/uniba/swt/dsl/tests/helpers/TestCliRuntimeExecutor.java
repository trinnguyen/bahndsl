package de.uniba.swt.dsl.tests.helpers;

import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.generator.externals.CliRuntimeExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake runtime executor for testing external cli execution
 * Always success
 */
public class TestCliRuntimeExecutor extends CliRuntimeExecutor {

    private final List<Tuple<String, String[]>> commands = new ArrayList<>();

    public List<Tuple<String, String[]>> getCommands() {
        return commands;
    }

    @Override
    protected boolean internalExecuteCli(String command, String[] args, String workingDir) {
        commands.add(Tuple.of(command, args));
        return true;
    }

    public void clear() {
        commands.clear();
    }
}

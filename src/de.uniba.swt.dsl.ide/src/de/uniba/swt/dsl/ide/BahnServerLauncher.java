package de.uniba.swt.dsl.ide;

import org.eclipse.xtext.ide.server.ServerLauncher;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class BahnServerLauncher {

    /**
     * Use {@code RemoteServer.main(args) } for default (to compatible with Eclipse plugin and VSCode extension)
     * Use {@code ServerLauncher.main(args) } for debugging with remote lsp server
     */
    public static void main(String[] args) throws IOException, ExecutionException {
        ServerLauncher.main(args);
    }
}

package de.uniba.swt.dsl.ide;

import org.eclipse.xtext.ide.server.ServerLauncher;
import org.eclipse.xtext.ide.server.SocketServerLauncher;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BahnServerLauncher {

    /**
     * arguments for default launcher: -log -trace
     * arguments for localhost socket: -port=NUMBER
     *  example: -port=8081
     *
     * @param args arguments
     * @throws IOException IOException
     * @throws ExecutionException ExecutionException
     */
    public static void main(String[] args) throws IOException, ExecutionException {
        var port = CustomSocketLauncher.tryParsingPort(args);
        if (port.isPresent()) {
            SocketServerLauncher.main(args);
        } else {
            ServerLauncher.main(args);
        }
    }
}

class CustomSocketLauncher extends SocketServerLauncher {

    @Override
    protected int getPort(String... args) {
        return tryParsingPort(args).orElse(DEFAULT_PORT);
    }

    public static Optional<Integer> tryParsingPort(String[] args) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith(SocketServerLauncher.PORT)) {
                    var strPort = arg.substring(SocketServerLauncher.PORT.length());
                    try {
                        return Optional.of(Integer.parseInt(strPort));
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid arg for port: " + strPort);
                    }
                }
            }
        }

        return Optional.empty();
    }
}

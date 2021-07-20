package de.uniba.swt.dsl.ide;

import com.google.common.base.Objects;
import org.eclipse.xtext.ide.server.ServerLauncher;
import org.eclipse.xtext.ide.server.SocketServerLauncher;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BahnServerLauncher {

    /**
     * arguments for default launcher: -log -trace
     * arguments for localhost socket: -port NUMBER
     *  example: -port 8686
     *
     * @param args arguments
     * @throws IOException IOException
     * @throws ExecutionException ExecutionException
     */
    public static void main(String[] args) throws IOException, ExecutionException {
        if (CustomSocketLauncher.portExists(args)) {
            new CustomSocketLauncher().launch(args);
        } else {
            ServerLauncher.main(args);
        }
    }
}

class CustomSocketLauncher extends SocketServerLauncher {

    @Override
    protected int getPort(String... args) {
        String strPort = getValue(args, PORT);
        try {
            return Integer.parseInt(strPort);
        } catch (NumberFormatException ex) {
            return DEFAULT_PORT;
        }
    }

    protected static boolean portExists(String... args) {
        for (String arg : args) {
            if (arg.startsWith(SocketServerLauncher.PORT)) {
                return true;
            }
        }

        return false;
    }
}

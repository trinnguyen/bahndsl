
package de.uniba.swt.dsl.ide;

/*
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.xtext.ide.server.LanguageServerImpl;
import org.eclipse.xtext.ide.server.LaunchArgs;
import org.eclipse.xtext.ide.server.ServerLauncher;
import org.eclipse.xtext.ide.server.ServerModule;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
public class RemoteServer {

    public static void main(String[] args) throws IOException, ExecutionException {
        // arguments for default launcher: -log -trace
        // arguments for localhost socket: -port=NUMBER
        // example: -port=8081
        RemoteServer thiz = Guice.createInjector(new ServerModule()).getInstance(RemoteServer.class);
        thiz.start(args);
    }

    @Inject
    private LanguageServerImpl languageServer;

    public void start(String[] args) throws IOException, ExecutionException {
        try {
            var port = tryParsingPort(args);
            Launcher<LanguageClient> launcher;
            if (port.isPresent()) {
                System.out.println("Bahn Language Server is starting with custom launcher with port: " + port.get());
                launcher = createLocalSocketLauncher(port.get());
            } else {
                launcher = createDefaultBahnLauncher(BahnServerLauncher.class.getName(), args);
                InputOutput.println("Bahn Language Server is starting with default language launcher");
            }

            languageServer.connect(launcher.getRemoteProxy());
            Future<Void> future = launcher.startListening();
            InputOutput.println("Bahn Language Server has been started.");
            while (!future.isDone()) {
                Thread.sleep(10_000L);
            }
        } catch (InterruptedException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

    private Launcher<LanguageClient> createDefaultBahnLauncher(String prefix, String[] args) {
        // create default LSP server sample code for vscode extension
        LaunchArgs launchArgs = ServerLauncher.createLaunchArgs(prefix, args);
        return Launcher.createLauncher(languageServer, LanguageClient.class, launchArgs.getIn(), launchArgs.getOut(), launchArgs.isValidate(), launchArgs.getTrace());
    }

    private Launcher<LanguageClient> createLocalSocketLauncher(int localPort) throws IOException, ExecutionException, InterruptedException {
        // Create custom LSP server with localhost:port
        Function<MessageConsumer, MessageConsumer> wrapper = consumer -> {
            MessageConsumer result = consumer;
            return result;
        };

        return createSocketLauncher(languageServer, LanguageClient.class, new InetSocketAddress("localhost", localPort), Executors.newCachedThreadPool(), wrapper);
    }

    private static <T> Launcher<T> createSocketLauncher(Object localService, Class<T> remoteInterface, SocketAddress socketAddress, ExecutorService executorService, Function<MessageConsumer, MessageConsumer> wrapper) throws IOException, ExecutionException, InterruptedException {
        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().bind(socketAddress);
        AsynchronousSocketChannel socketChannel = serverSocket.accept().get();
        return Launcher.createIoLauncher(localService, remoteInterface, Channels.newInputStream(socketChannel), Channels.newOutputStream(socketChannel), executorService, wrapper);
    }

    private static final String PortPrefix = "-port=";
    private static Optional<Integer> tryParsingPort(String[] args) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith(PortPrefix)) {
                    var strPort = arg.substring(PortPrefix.length());
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
*/
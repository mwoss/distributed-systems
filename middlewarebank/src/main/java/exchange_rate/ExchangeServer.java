package exchange_rate;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import org.apache.log4j.Logger;

public class ExchangeServer {
    private static final Logger logger = Logger.getLogger(ExchangeServer.class.getName());

    private int port = 50051;
    private Server server;

    private void start() throws IOException
    {
        server = ServerBuilder.forPort(port)
                .addService(new ExchangeServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                ExchangeServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final ExchangeServer server = new ExchangeServer();
        server.start();
        server.blockUntilShutdown();
    }
}

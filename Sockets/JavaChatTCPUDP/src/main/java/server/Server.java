package server;

import server.workers.TCPClientsResponder;
import server.workers.UDPResponder;

import java.io.PrintStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * A chat server that delivers public and private messages.
 */
public class Server {

    private ServerSocket serverSocketTCP;
    private Socket clientSocket;
    private DatagramSocket serverDatagram;
    private final int PORT = 9008;
    private final int MAX_CLIENTS = 2;

    private final Queue<TCPClientsResponder> threads = new ConcurrentLinkedQueue<>();
    private UDPResponder udpResponse;


    public Server() throws IOException {
        this.serverSocketTCP = new ServerSocket(PORT);
        this.serverDatagram = new DatagramSocket(PORT);
        this.udpResponse = new UDPResponder(this.serverDatagram, this.threads);
    }

    private void run() {
        udpResponse.start();
        try {
            while (true) {
                clientSocket = serverSocketTCP.accept();
                if (threads.size() >= MAX_CLIENTS)
                    closeConnectionBusyServer(clientSocket);

                createConnectionWithClient();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void createConnectionWithClient(){
        TCPClientsResponder client = new TCPClientsResponder(clientSocket, threads);
        client.start();
        threads.add(client);
    }

    private void closeConnectionBusyServer(Socket clientSocket) throws IOException {
        PrintStream os = new PrintStream(clientSocket.getOutputStream(), true);
        os.println("Server is too busy. Try later.");
        os.close();
        clientSocket.close();
    }

    public static void main(String args[]) throws IOException {
        Server server = null;
        try{
            server = new Server();
            server.run();
        }
        finally{
            if((server != null ? server.serverSocketTCP : null) != null)
                server.serverSocketTCP.close();
            if((server != null ? server.serverDatagram : null) != null)
                server.serverDatagram.close();
        }
    }
}
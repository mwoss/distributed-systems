package server.workers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

public class TCPClientsResponder extends Thread {

    private Socket socket;
    private final Queue<TCPClientsResponder> threads;
    private String nick;

    public TCPClientsResponder(Socket socket, Queue<TCPClientsResponder> threads) {
        this.socket = socket;
        this.threads = threads;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            nick = in.readLine();
            System.out.println(nick + " joined chat");

            while (true) {
                String msg = in.readLine();
                broadcastTCPMessage(msg);
            }
        } catch (IOException e) {
            System.out.println("User " + nick + " disconnected");
        } finally {
            if (socket != null) {
                closeClientSocket(socket);
            }
        }
    }

    private synchronized void broadcastTCPMessage(String msg) throws IOException {
        System.out.println("Received message from " + nick + " Broadcast *TCP* message");
        for (TCPClientsResponder client : threads) {
            if (!this.socket.equals(client.socket)) {
                PrintWriter out = new PrintWriter(client.socket.getOutputStream(), true);
                out.println(nick + " said: " + msg);
            }
        }
    }

    private void closeClientSocket(Socket socket) {
        try {
            threads.remove(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}

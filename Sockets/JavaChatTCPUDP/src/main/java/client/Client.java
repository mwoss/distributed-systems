package client;

import client.workers.MulticastReceiver;
import client.workers.SendThread;
import client.workers.TCPReceiver;
import client.workers.UDPReceiver;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

class Client {

    private Socket clientSocket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private ArrayList<Thread> workers;

    private final String HOST_NAME = "localhost";
    private final InetAddress GROUP_ADDRESS = InetAddress.getByName("225.225.225.225");
    private final int PORT = 9008;
    private final int MULTICAST_PORT = 9876;

    public Client() throws IOException {
        this.clientSocket = new Socket(HOST_NAME, PORT);
        this.datagramSocket = new DatagramSocket(clientSocket.getLocalPort());
        this.multicastSocket = new MulticastSocket(MULTICAST_PORT);
        multicastSocket.setBroadcast(true);
        multicastSocket.joinGroup(GROUP_ADDRESS);
        this.workers = new ArrayList<>();
    }

    private void run() throws InterruptedException, IOException {
        workers.addAll(Arrays.asList(
                new TCPReceiver(clientSocket, datagramSocket, multicastSocket),
                new UDPReceiver(datagramSocket),
                new MulticastReceiver(multicastSocket),
                new SendThread(clientSocket, datagramSocket, multicastSocket, GROUP_ADDRESS)));

        workers.forEach(Thread::start);
        workers.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = null;
        try {
            client = new Client();
            client.run();
        } finally {
            if ((client != null ? client.clientSocket : null) != null) {
                System.out.println("Client socked closed.");
                client.clientSocket.close();
            }
            if ((client != null ? client.multicastSocket : null) != null && !client.multicastSocket.isClosed()) {
                System.out.println("Multicast socked closed.");
                client.multicastSocket.leaveGroup(client.GROUP_ADDRESS);
                client.multicastSocket.close();
            }

            if ((client != null ? client.datagramSocket : null) != null && !client.datagramSocket.isClosed()) {
                System.out.println("Datagram socked closed.");
                client.datagramSocket.close();
            }
        }

    }
}
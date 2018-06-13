package client.workers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class SendThread extends Thread {

    private String nickName;
    private PrintWriter out;
    private Scanner in;

    private Socket socket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;
    private final InetAddress groupAddress;


    public SendThread(Socket socket, DatagramSocket datagramSocket,
                      MulticastSocket multicastSocket, InetAddress inetAddress) throws IOException {

        this.socket = socket;
        this.datagramSocket = datagramSocket;
        this.in = new Scanner(System.in);
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.multicastSocket = multicastSocket;
        this.groupAddress = inetAddress;
    }

    @Override
    public void run() {
        delayStart();
        if (!socket.isClosed()) {
            System.out.println("CHOSE NICKNAME");
            nickName = in.nextLine();
            out.println(nickName);
            System.out.println("You've chosen nick: " + nickName + ". Start chatting!");
            while (true) {
                String message = in.nextLine();
                switch (message) {
                    case "UDP ART":
                        sendUDPMessage();
                        break;
                    case "MULTICAST ART":
                        sendMulticastMessage();
                        break;
                    default:
                        sendTCPMessage(message);
                        break;
                }
            }
        }
    }

    private void sendTCPMessage(String message) {
        out.println(message);
    }

    private void sendUDPMessage() {
        try {
            String asciiArt = "(づ｡◕‿‿◕｡)づ";
            byte[] message = (nickName + "\n" + asciiArt).getBytes();
            DatagramPacket packet = new DatagramPacket(message, message.length, socket.getInetAddress(), socket.getPort());
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMulticastMessage() {
        try {
            String asciiArt = "(•_•) ( •_•)>⌐■-■ (⌐■_■)";
            byte[] message = (nickName + " said: " + asciiArt).getBytes();
            DatagramPacket packet = new DatagramPacket(message, message.length, groupAddress, multicastSocket.getLocalPort());
            multicastSocket.leaveGroup(groupAddress);
            multicastSocket.send(packet);
            multicastSocket.joinGroup(groupAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void delayStart() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

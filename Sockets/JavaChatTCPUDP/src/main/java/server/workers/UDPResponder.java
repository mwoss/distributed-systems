package server.workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Queue;

public class UDPResponder extends Thread {

    private DatagramSocket datagramSocket;
    private final Queue<TCPClientsResponder> threads;


    public UDPResponder(DatagramSocket datagramSocket, Queue<TCPClientsResponder> threads){
        this.datagramSocket = datagramSocket;
        this.threads = threads;
    }

    @Override
    public void run() {
        try {
            while (true) {
                broadcastUDPMessage();
            }
        }
        catch (IOException e){
            System.out.println("Problem with receiving UDP message");
        }
        finally {
            datagramSocket.close();
        }
    }

    private synchronized void broadcastUDPMessage() throws IOException {
        byte[] message = initBuffer();
        DatagramPacket packet = new DatagramPacket(message, message.length);
        datagramSocket.receive(packet);

        byte[] formattedMessage = formatMessage(packet);

        for(TCPClientsResponder client: threads){
            InetAddress receiverAddress = client.getSocket().getInetAddress();
            int receiverPort = client.getSocket().getPort();
            if(packet.getPort() != receiverPort || !packet.getAddress().equals(receiverAddress)){
                DatagramPacket messageToSend = new DatagramPacket(formattedMessage,
                        formattedMessage.length,
                        client.getSocket().getInetAddress(),
                        client.getSocket().getPort());
                datagramSocket.send(messageToSend);
            }
        }
    }

    private byte[] initBuffer(){
        byte[] buffer = new byte[2048];
        Arrays.fill(buffer, (byte) 0);
        return buffer;
    }

    private byte[] formatMessage(DatagramPacket data){
        String[] information = new String(data.getData()).split("\n");
        String message = information[0] + " said: " + information[1];
        printServerMessage(information[0]);

        return message.getBytes();
    }

    private void printServerMessage(String nick){
        System.out.println("Received message from " + nick + " Broadcast *UDP* message");
    }
}

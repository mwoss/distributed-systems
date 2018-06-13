package client.workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPReceiver extends  Thread{

    private DatagramSocket datagramSocket;

    public UDPReceiver(DatagramSocket datagramSocket){
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        try{
            byte[] buffer = new byte[2048];
            while (true){
                receiveUDP(buffer);
            }
        }catch (IOException e){
            System.out.println("Datagram socked closed");
        }
    }

    private void receiveUDP(byte[] buffer) throws IOException {
        Arrays.fill(buffer, (byte)0);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(packet);
        System.out.println(new String(packet.getData()));

    }
}

package client.workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MulticastReceiver extends Thread {

    private MulticastSocket multicastSocket;

    public MulticastReceiver(MulticastSocket multicastSocket){
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        try{
            byte[] buffer = new byte[2048];
            while (true){
                receiveMulticast(buffer);
            }
        }catch (IOException e){
            System.out.println("Multicast socked closed");
        }finally {
            multicastSocket.close();
        }
    }


    private void receiveMulticast(byte[] buffer) throws IOException {
        Arrays.fill(buffer, (byte)0);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        multicastSocket.receive(packet);
        System.out.println(new String(packet.getData()));
    }
}

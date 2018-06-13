package client.workers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.Socket;

public class TCPReceiver extends Thread {

    private Socket socket;
    private DatagramSocket datagramSocket;
    private MulticastSocket multicastSocket;

    public TCPReceiver(Socket socket, DatagramSocket datagramSocket, MulticastSocket multicastSocket) {
        this.socket = socket;
        this.datagramSocket = datagramSocket;
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data;
            while (true) {
                data = in.readLine();
                if (data == null) {
                    socket.close();
                    break;
                } else
                    receiveTCP(data);
            }
            closeUp();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void receiveTCP(String data) {
        System.out.println(data);
    }

    private void closeUp(){
        datagramSocket.close();
        multicastSocket.close();
    }

}

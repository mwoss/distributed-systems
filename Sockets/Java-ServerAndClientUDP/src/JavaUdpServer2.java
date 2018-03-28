import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class JavaUdpServer2 {

    @SuppressWarnings("Duplicates")
    public static void main(String args[])
    {
        System.out.println("JAVA UDP SERVER");
        DatagramSocket socket = null;
        int portNumber = 9008;

        try{
            socket = new DatagramSocket(portNumber);
            byte[] receiveBuffer = new byte[1024];

            while(true) {
                Arrays.fill(receiveBuffer, (byte)0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                Integer number = Integer.reverseBytes(ByteBuffer.wrap(receiveBuffer).getInt());
                System.out.println("received msg: " + number);

                InetAddress address = InetAddress.getByName("localhost");
                Integer newNumber = number + 1;
                byte[] sendBuffer = ByteBuffer.allocate(4).putInt(newNumber).array();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, receivePacket.getPort());
                socket.send(sendPacket);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}

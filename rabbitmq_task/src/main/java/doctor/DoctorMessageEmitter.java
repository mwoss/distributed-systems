package doctor;

import com.rabbitmq.client.Channel;
import utils.MessageEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DoctorMessageEmitter implements Runnable, MessageEmitter {

    private Channel emitterChannel;
    private String id;
    private BufferedReader bufferedReader;

    private Thread emitter;

    DoctorMessageEmitter(Channel emitterChannel, String id) {
        this.emitterChannel = emitterChannel;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.id = id;
    }

    public void run() {
        String message;
        String routingKey;
        while(true){
            System.out.println("Enter message: ");
            try {
                message = bufferedReader.readLine();

                if ("exit".equals(message)) {
                    break;
                }
                routingKey = "lol" + id;

                emitterChannel.basicPublish(;, routingKey, null, message.getBytes("UTF-8"));
                System.out.println("Sent: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void start() {
        if (emitter == null) {
            emitter = new Thread(this);
            emitter.start();
        }
    }
}

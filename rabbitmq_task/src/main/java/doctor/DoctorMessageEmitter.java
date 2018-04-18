package doctor;

import com.rabbitmq.client.Channel;
import utils.ConstValues;
import utils.MessageEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

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
        while (true) {
            System.out.println("Enter message: ");
            try {
                message = bufferedReader.readLine();
                if (message.equals("exit")) {
                    break;
                }
                routingKey = ConstValues.ROUTING_KEY_TECHNICIAN + parseInput(message);
                message = ConstValues.DOCTOR + " " +  message + " " + id;
                emitterChannel.basicPublish(ConstValues.EXCHANGE_NAME_COMMON, routingKey, null, message.getBytes("UTF-8"));
                System.out.println("Sent: " + message);
            } catch (IOException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void startTask() {
        if (emitter == null) {
            emitter = new Thread(this);
            emitter.start();
        }
    }

    private String parseInput(String message) {
        String[] data = message.split(" ");
        if (data.length != 2)
            throw new IllegalArgumentException("Not enough arguments");
        if (!Arrays.asList(ConstValues.INJURIES).contains(data[0]))
            throw new IllegalArgumentException("Unrecognized injury");
        return data[0];
    }
}

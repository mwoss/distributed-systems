package admin;

import com.rabbitmq.client.Channel;
import utils.ConstValues;
import utils.MessageEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdminMessageEmitter implements Runnable, MessageEmitter {

    private Channel emitterChannel;
    private BufferedReader bufferedReader;

    private Thread emitter;

    public AdminMessageEmitter(Channel emitterChannel) {
        this.emitterChannel = emitterChannel;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        String message;
        while (true) {
            System.out.println("Enter message: ");
            try {
                message = bufferedReader.readLine();
                if (message.equals("exit")) {
                    break;
                }
                message = ConstValues.ADMIN + " " +  message;
                emitterChannel.basicPublish(ConstValues.EXCHANGE_NAME_COMMON, ConstValues.ROUTING_KEY_ADMIN,
                        null, message.getBytes("UTF-8"));
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
}

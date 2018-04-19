package admin;

import com.rabbitmq.client.*;
import utils.ConstValues;
import utils.MessageListener;

import java.io.IOException;

public class AdminMessageListener implements MessageListener {

    private Channel commonChannel;

    public AdminMessageListener(Channel commonChannel) throws IOException {
        this.commonChannel = commonChannel;

        this.commonChannel.queueDeclare(ConstValues.ADMIN_QUEUE_NAME, false, false, false, null);
        this.commonChannel.queueBind(ConstValues.ADMIN_QUEUE_NAME, ConstValues.EXCHANGE_NAME_COMMON, ConstValues.ROUTING_KEY_ALL);
    }

    @Override
    public void receiveMessage() throws IOException {
        Consumer consumer = new DefaultConsumer(commonChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                handleMessage(message);
            }
        };
        commonChannel.basicConsume(ConstValues.ADMIN_QUEUE_NAME, true, consumer);
    }

    private void handleMessage(String message) throws IOException {
        String[] data = message.split(" ");
        String producer_name = data[0];
        if (producer_name.equals(ConstValues.TECHNICIAN)) {
            System.out.println("\u001B[36m" + "Received from technician: " + message + "\u001B[0m");
        } else if (producer_name.equals(ConstValues.DOCTOR)) {
            System.out.println("\u001B[33m" + "Received from admin: " + message + "\u001B[0m");
        }
    }
}

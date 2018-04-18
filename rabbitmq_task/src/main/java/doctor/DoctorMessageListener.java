package doctor;

import com.rabbitmq.client.*;
import utils.ConstValues;

import java.io.IOException;

public class DoctorMessageListener implements utils.MessageListener {

    private Channel listenerChannel;
    private String id;

    DoctorMessageListener(Channel listenerChannel, String id) throws IOException {
        this.listenerChannel = listenerChannel;
        this.id = id;

        this.listenerChannel.queueDeclare(this.id, false, false, false, null);
        this.listenerChannel.queueBind(this.id, ConstValues.EXCHANGE_NAME_COMMON, ConstValues.ROUTING_KEY_DOCTOR + this.id);
    }

    public void receiveMessage() throws IOException {
        Consumer consumer = new DefaultConsumer(listenerChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                handleMessage(message);
//                listenerChannel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        listenerChannel.basicConsume(id, true, consumer);
    }

    private void handleMessage(String message) throws IOException {
        String[] data = message.split(" ");
        String producer_name = data[0];
        if (producer_name.equals(ConstValues.TECHNICIAN)) {
            System.out.println("\u001B[36m" + "Received from technician: " + message + "\u001B[0m");
        } else if (producer_name.equals(ConstValues.ADMIN)) {
            System.out.println("\u001B[31m" + "Received from admin: " + message + "\u001B[0m");
        }
    }
}

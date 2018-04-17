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

        listenerChannel.queueDeclare(this.id, false, false, false, null);
        listenerChannel.queueBind(this.id, ConstValues.EXCHANGE_NAME_IN, ConstValues.ROUTING_KEY_DOCTOR + this.id);
    }

    public void receiveMessage() throws IOException {
        Consumer consumer = new DefaultConsumer(listenerChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received from technician: " + message);
                listenerChannel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        System.out.println("Waiting for messages.");
        listenerChannel.basicQos(1);
        listenerChannel.basicConsume(id, false, consumer);
    }
}

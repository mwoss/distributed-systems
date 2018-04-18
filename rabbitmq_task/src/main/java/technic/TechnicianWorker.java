package technic;

import com.rabbitmq.client.*;
import utils.ConstValues;
import utils.MessageListener;

import java.io.IOException;

public class TechnicianWorker implements MessageListener {

//    private Channel listenerChannel;
//    private Channel emitterChannel;
    private Channel commonChannel;
    private String injury;

//    public TechnicianWorker(Channel listenerChannel, Channel emitterChannel, String injury) throws IOException {
//        this.emitterChannel = emitterChannel;
//        this.listenerChannel = listenerChannel;
//        this.injury = injury;
//
//        this.listenerChannel.queueDeclare(this.injury, false, false, false, null);
//        this.listenerChannel.queueBind(this.injury, ConstValues.EXCHANGE_NAME_OUT, ConstValues.ROUTING_KEY_TECHNICIAN + this.injury);
//    }

    public TechnicianWorker(Channel commonChannel, String injury) throws IOException {
        this.commonChannel = commonChannel;
        this.injury = injury;

        this.commonChannel.queueDeclare(this.injury, false, false, false, null);
        this.commonChannel.queueBind(this.injury, ConstValues.EXCHANGE_NAME_OUT, ConstValues.ROUTING_KEY_TECHNICIAN + this.injury);
    }

    @Override
    public void receiveMessage() throws IOException {
        Consumer consumer = new DefaultConsumer(commonChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received from doctor: " + message);
                String msgToDoctor = messageToSend(message);
                commonChannel.basicPublish(ConstValues.EXCHANGE_NAME_IN, ConstValues.ROUTING_KEY_DOCTOR + extractName(message),
                        null, msgToDoctor.getBytes("UTF-8"));
                commonChannel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        System.out.println("Waiting for messages.");
        commonChannel.basicQos(1);
        commonChannel.basicConsume(injury, false, consumer);
    }

    private String extractName(String message){
        return message.split(" ")[2];
    }

    private String messageToSend(String message){
        String[] data = message.split(" ");
        data[2] = "done";
        return String.join(" ", data);
    }
}

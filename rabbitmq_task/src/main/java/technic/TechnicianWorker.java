package technic;

import com.rabbitmq.client.*;
import utils.ConstValues;
import utils.MessageListener;

import java.io.IOException;

public class TechnicianWorker implements MessageListener {

    private Channel commonChannel;
    private String injury;
    private String id;

    public TechnicianWorker(Channel commonChannel, String injury, String id) throws IOException {
        this.commonChannel = commonChannel;
        this.injury = injury;
        this.id = id;

        this.commonChannel.queueDeclare(this.injury, false, false, false, null);
        this.commonChannel.queueBind(this.injury, ConstValues.EXCHANGE_NAME_COMMON, ConstValues.ROUTING_KEY_TECHNICIAN + this.injury);

        this.commonChannel.queueDeclare(this.id, false, false, false, null);
        this.commonChannel.queueBind(this.id, ConstValues.EXCHANGE_NAME_COMMON, ConstValues.ROUTING_KEY_ADMIN);
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
        System.out.println("Waiting for messages.");
        commonChannel.basicQos(1);
        commonChannel.basicConsume(injury, true, consumer);
        commonChannel.basicConsume(id, true, consumer);
    }

    private String extractName(String message){
        return message.split(" ")[3];
    }

    private void handleMessage(String message) throws IOException{
        String[] data = message.split(" ");
        String producer_name = data[0];
        if(producer_name.equals(ConstValues.DOCTOR)){
            String msgToDoctor = messageToSend(data);
            System.out.println("\u001B[33m" + "Received from doctor: " + message + "\u001B[0m");
            commonChannel.basicPublish(ConstValues.EXCHANGE_NAME_COMMON, ConstValues.ROUTING_KEY_DOCTOR + extractName(message),
                    null, msgToDoctor.getBytes("UTF-8"));
        }
        else if(producer_name.equals(ConstValues.ADMIN)){
            System.out.println("\u001B[31m" + "Received from admin: " + message + "\u001B[0m");
        }

    }
    private String messageToSend(String[] data){
        data[0] = ConstValues.TECHNICIAN;
        data[3] = "done";
        return String.join(" ", data);
    }
}

package doctor;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Doctor {

    private final String EXCHANGE_NAME_IN = "EXCHANGE_DOCTOR_IN";
    private final String EXCHANGE_NAME_OUT = "EXCHANGE_DOCTOR_OUT";

    private Channel emitterChannel;
    private Channel listenerChannel;

    public Doctor() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();

        this.emitterChannel = connection.createChannel();
        this.listenerChannel = connection.createChannel();

        emitterChannel.exchangeDeclare(EXCHANGE_NAME_OUT, BuiltinExchangeType.TOPIC);
        listenerChannel.exchangeDeclare(EXCHANGE_NAME_IN, BuiltinExchangeType.TOPIC);

    }

    private void outputInformations(){
        System.out.println("**Doctor connected**");
    }

    public static void main(String[] args) throws Exception{
        Doctor doctor = new Doctor();
    }
}

package doctor;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Doctor {

    private static final String EXCHANGE_NAME_IN = "EXCHANGE_DOCTOR_IN";
    private static final String EXCHANGE_NAME_OUT = "EXCHANGE_DOCTOR_OUT";

    private Channel emitterChannel;
    private Channel listenerChannel;
    private String id;

    public Doctor() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();

        this.emitterChannel = connection.createChannel();
        this.listenerChannel = connection.createChannel();

        emitterChannel.exchangeDeclare(EXCHANGE_NAME_OUT, BuiltinExchangeType.TOPIC);
        listenerChannel.exchangeDeclare(EXCHANGE_NAME_IN, BuiltinExchangeType.TOPIC);
        getInformation();
    }

    private void getInformation() throws IOException {
        System.out.println("Doctor connected. Input ID:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        id = bufferedReader.readLine();
    }

    private void initWorkers(){
        DoctorMessageEmitter emitter = new DoctorMessageEmitter(emitterChannel, id);
        DoctorMessageListener listener = new DoctorMessageListener(listenerChannel, id);
        emitter.start();
        listener.receiveMessage();
    }

    public static void main(String[] args) throws Exception{
        Doctor doctor = new Doctor();
        doctor.initWorkers();
    }
}

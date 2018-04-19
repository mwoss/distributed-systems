package technic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import utils.ConstValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

public class Technician {

    private Channel commonChannel;
    private String[] injuries;

    public Technician() throws IOException, TimeoutException, IllegalArgumentException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();

        this.commonChannel = connection.createChannel();
        commonChannel.exchangeDeclare(ConstValues.EXCHANGE_NAME_COMMON, BuiltinExchangeType.TOPIC);

        this.injuries = getInformation();
    }

    private String[] getInformation() throws IOException {
        System.out.println("Technician connected. Input two injury types:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        //injury1 injury2 ID
        return parseInput(bufferedReader.readLine().split(" "));
    }

    private String[] parseInput(String[] data){
        if(data.length != 2)
            throw new IllegalArgumentException("Not enough arguments");
        for(String q_name : data){
            if(!Arrays.asList(ConstValues.INJURIES).contains(q_name))
                throw new IllegalArgumentException("Unrecognized injury name");
        }
        return data;
    }

    private void initWorkers() throws IOException {
        String name = Integer.toString(ThreadLocalRandom.current().nextInt());
        TechnicianWorker technicianWorker1 = new TechnicianWorker(commonChannel, injuries[0], "tech" + name);
        TechnicianWorker technicianWorker2 = new TechnicianWorker(commonChannel, injuries[1], "tech" + name);
        technicianWorker1.receiveMessage();
        technicianWorker2.receiveMessage();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        try{
            Technician technician = new Technician();
            technician.initWorkers();
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

    }
}

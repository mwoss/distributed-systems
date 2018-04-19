package admin;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import utils.ConstValues;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Admin {

    private Channel commonChannel;

    public Admin() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();

        this.commonChannel = connection.createChannel();
        commonChannel.exchangeDeclare(ConstValues.EXCHANGE_NAME_COMMON, BuiltinExchangeType.TOPIC);
        System.out.println("Admin connected.");

    }

    private void initWorkers() throws IOException {
        AdminMessageEmitter adminMessageEmitter = new AdminMessageEmitter(commonChannel);
        AdminMessageListener adminMessageListener = new AdminMessageListener(commonChannel);
        adminMessageEmitter.startTask();
        adminMessageListener.receiveMessage();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Admin admin = new Admin();
        admin.initWorkers();
    }
}

package doctor;


import com.rabbitmq.client.Channel;

public class DoctorMessageListener implements utils.MessageListener {

    public DoctorMessageListener(Channel listenerChannel, String id) {
    }

    // use JDK9 for private interface methods
    public void receiveMessage() {

    }
}

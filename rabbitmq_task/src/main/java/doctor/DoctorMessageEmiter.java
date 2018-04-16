package doctor;

import com.rabbitmq.client.Channel;
import utils.MessageEmitter;



public class DoctorMessageEmiter implements Runnable, MessageEmitter {

    private Thread emitter;

    public DoctorMessageEmiter(Channel emitterChannel, String id) {
    }

    public void run() {

    }

    public void start() {
        if (emitter == null) {
            emitter = new Thread(this);
            emitter.start();
        }
    }
}

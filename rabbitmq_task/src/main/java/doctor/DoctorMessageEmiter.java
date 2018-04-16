package doctor;

import utils.MessageEmitter;

public class DoctorMessageEmiter implements Runnable, MessageEmitter {

    private Thread emitter;

    public void run() {

    }

    public void start() {
        if (emitter == null) {
            emitter = new Thread(this);
            emitter.start();
        }
    }
}

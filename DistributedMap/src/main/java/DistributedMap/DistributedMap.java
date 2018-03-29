package DistributedMap;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;
import java.util.HashMap;

public class DistributedMap implements SimplStringMap {

    private final HashMap<String, String> distMap;
    private JChannel jChannel;
    private MessageReceiver messageReceiver;

    public DistributedMap(String channelName, String address) throws Exception {
        this.distMap = new HashMap<>();
        this.jChannel = new JChannel();
        this.messageReceiver = new MessageReceiver(distMap, jChannel);
        jChannel.setReceiver(this.messageReceiver);
        jChannel.connect(channelName);
        jChannel.getState(null, 5000);
        initProtocolStack(address);
    }

    public boolean containsKey(String key) {
        synchronized (distMap) {
            return distMap.containsKey(key);
        }
    }

    public String get(String key) {
        synchronized (distMap) {
            return distMap.get(key);
        }
    }

    public String put(String key, String value) throws Exception {
        synchronized (distMap) {
            String msg = "p;" + key + ";" + value;
            sendMessage(msg.getBytes());
            return distMap.put(key, value);
        }
    }

    public String remove(String key) throws Exception {
        synchronized (distMap) {
            String msg = "r;" + key;
            sendMessage(msg.getBytes());
            return distMap.remove(key);
        }
    }

    public void printContent() {
        synchronized (distMap) {
            distMap.forEach((k, v) -> System.out.print(v + ", "));
        }
    }

    public void disconnect() {
        jChannel.close();
    }

    private void sendMessage(byte[] content) throws Exception {
        Message msg = new Message(null, content);
        jChannel.send(msg);
    }

    private void initProtocolStack(String multicastAddress) throws Exception {
        ProtocolStack stack = new ProtocolStack();
        jChannel.setProtocolStack(stack);
        stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(multicastAddress)))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE())
                .addProtocol(new SEQUENCER());

        stack.init();
    }
}

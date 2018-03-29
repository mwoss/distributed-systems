package DistributedMap;

import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageReceiver extends ReceiverAdapter {

    private final Map<String, String> distMap;
    private JChannel jChannel;

    public MessageReceiver(Map<String, String> distMap, JChannel jChannel) {
        this.distMap = distMap;
        this.jChannel = jChannel;
    }

    @Override
    public void receive(Message msg) {
        if (!msg.getSrc().equals(jChannel.getAddress())) {
            String toParse = ((String) msg.getObject());
            String[] data = toParse.split(";");
            System.out.println(toParse);
            synchronized (distMap) {
                if (data[0].equals("p"))
                    distMap.put(data[1], data[2]);
                else if (data[0].equals("r"))
                    distMap.remove(data[1]);
            }
        }
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("** view: " + view);
        handleView(jChannel, view);
    }

    private static void handleView(JChannel ch, View new_view) {
        if (new_view instanceof MergeView) {
            ViewHandler handler = new ViewHandler(ch, (MergeView) new_view);
            // requires separate thread as we don't want to block JGroups
            handler.start();
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (distMap) {
            Util.objectToStream(distMap, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        synchronized (distMap) {
            Map<String, String> tmpMap = (HashMap<String, String>) Util.objectFromStream(new DataInputStream(input));
            distMap.clear();
            distMap.putAll(tmpMap);
        }
    }


    private static class ViewHandler extends Thread {
        JChannel ch;
        MergeView view;

        private ViewHandler(JChannel ch, MergeView view) {
            this.ch = ch;
            this.view = view;
        }

        public void run() {
            List<View> subgroups = view.getSubgroups();
            View tmp_view = subgroups.get(0); // picks the first
            Address local_addr = ch.getAddress();
            if (!tmp_view.getMembers().contains(local_addr)) {
                System.out.println("Not member of the new primary partition ("
                        + tmp_view + "), will re-acquire the state");
                try {
                    ch.getState(null, 30000);
                } catch (Exception ex) {
                }
            } else {
                System.out.println("Not member of the new primary partition ("
                        + tmp_view + "), will do nothing");
            }
        }
    }
}

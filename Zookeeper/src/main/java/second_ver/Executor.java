package second_ver;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.*;
import java.util.List;

public class Executor implements Watcher, Runnable, DataMonitorListener {
    private String znode;
    private DataMonitor dm;
    private ZooKeeper zk;
    private String exec[];
    private Process child;

    public Executor(String hostPort, String znode, String exec[]) throws KeeperException, IOException, InterruptedException {
        this.znode = znode;
        this.exec = exec;
        zk = new ZooKeeper(hostPort, 10000, this);
        dm = new DataMonitor(zk, znode, null, this);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Start program with 2 or more args: hostPort program [args ...]");
            System.exit(2);
        }
        String hostPort = args[0];
        String znode = "/zknode-test";
        String exec[] = new String[args.length - 1];
        System.arraycopy(args, 1, exec, 0, exec.length);
        try {
            new Executor(hostPort, znode, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                switch (br.readLine()) {
                    case "tree":
                        printTree(znode);
                        break;
                }
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    private void printTree(String path) {
        try {
            if (zk.exists(path, this) != null) {
                System.out.println(path);
                List<String> children = zk.getChildren(path, this);
                children.forEach(child -> printTree(path + "/" + child));
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

    public void exists(byte[] data) {
        if (data == null) {
            if (child != null) {
                System.out.println("Killing process");
                child.destroyForcibly();
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException ignored) {
                }
            }
            child = null;
        } else {
            if (child != null) {
                System.out.println("Stopping child");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                zk.getChildren(znode, this);
                child = Runtime.getRuntime().exec(exec);
                System.out.println("Starting child ");
                new StreamWriter(child.getInputStream(), System.out);
                new StreamWriter(child.getErrorStream(), System.err);
            } catch (IOException | InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    static class StreamWriter extends Thread {
        OutputStream os;

        InputStream is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        public void run() {
            byte b[] = new byte[80];
            int rc;
            try {
                while ((rc = is.read(b)) > 0) {
                    os.write(b, 0, rc);
                }
            } catch (IOException ignored) {
            }

        }
    }
}
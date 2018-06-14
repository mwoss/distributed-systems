import logic.Executor;
import org.apache.zookeeper.KeeperException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws IOException, KeeperException {
        if (args.length < 2) {
            System.err.println("USAGE: Executor hostPort znode [args ...]");
            System.exit(2);
        }
        String hostPort = args[0];
        String znode = "/zknode-test";
        String exec[] = new String[args.length - 1];
        System.arraycopy(args, 1, exec, 0, exec.length);

        Executor executor = new Executor(hostPort, znode, exec);
        new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    switch (br.readLine()) {
                        case "tree":
                            executor.printDescendantsTree(znode);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        executor.run();
    }
}

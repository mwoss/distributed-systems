package Application;

import DistributedMap.DistributedMap;

import java.util.Scanner;

public class App {

    private static final String CHANNEL_NAME = "channel";
    private static final String MULTICAST_ADDRESS = "230.0.0.3";

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        DistributedMap distributedMap = new DistributedMap(CHANNEL_NAME, MULTICAST_ADDRESS);
        printMessage();
        Scanner scanner = new Scanner(System.in);
        String[] input = {"void"};
        while (!input[0].equals("quit")) {
            input = scanner.nextLine().split("\\s+");
//            System.out.println(input);
            switch (input[0]) {
                case "put":
                    System.out.println(distributedMap.put(input[1], input[2]));
                    break;
                case "remove":
                    System.out.println(distributedMap.remove(input[1]));
                    break;
                case "contains":
                    System.out.println(distributedMap.containsKey(input[1]));
                    break;
                case "get":
                    System.out.println(distributedMap.get(input[1]));
                    break;
                case "content":
                    distributedMap.printContent();
                    break;
                default:
                    System.out.println("Wrong operation try again");
                    break;
            }
        }

        distributedMap.disconnect();
    }

    private static void printMessage(){
        System.out.println("Connected");
    }
}

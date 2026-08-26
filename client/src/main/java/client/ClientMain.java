package client;

public class ClientMain {
    public static void main(String[] args){
        String hostName = args.length >= 2 ? args[1] : "localhost";
        String port = args.length >= 1 ? args[0] : "8080";
        Client client = new Client(hostName, port);
        client.run();
    }
}

import java.io.*;
import java.net.*;

public class concHTTPAsk {

    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                Thread thread = new Thread(new HTTPAskWorker(connectionSocket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

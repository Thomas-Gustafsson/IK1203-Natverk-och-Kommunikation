import java.io.*;
import java.net.*;
import tcpclient.TCPClient;

public class HTTPAsk {
    public static void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            InputStream inputStream = connectionSocket.getInputStream();
            OutputStream outputStream = connectionSocket.getOutputStream();

            // Read the client request as a string
            byte[] clientRequestBytes = new byte[1024];
            int readBytes = inputStream.read(clientRequestBytes);
            String clientRequest = new String(clientRequestBytes, 0, readBytes, "UTF-8");

            // Extract the parameters from the request
            String[] requestLines = clientRequest.split("\r\n");
            String[] requestWords = requestLines[0].split(" ");
            String[] params = requestWords[1].split("[?&]");

            // Set all to default values in case not given in HTTP request
            String hostname = "";
            int port = 0;
            int limit = 0;
            boolean shutdown = false;
            int timeout = 0;
            byte[] toServerBytes = new byte[0];

            for (int i = 1; i < params.length; i++) {
                String[] param = params[i].split("=");
                String key = param[0];
                String value = param[1];

                switch (key) {
                    case "hostname":
                        hostname = value;
                        break;
                    case "port":
                        port = Integer.parseInt(value);
                        break;
                    case "limit":
                        limit = Integer.parseInt(value);
                        break;
                    case "shutdown":
                        shutdown = true;
                        break;
                    case "timeout":
                        timeout = Integer.parseInt(value);
                        break;
                    case "toByteArray":
                        toServerBytes = value.getBytes();

                }
            }

            TCPClient tcpClient = new tcpclient.TCPClient(shutdown, timeout, limit);
            byte[] responseBytes = tcpClient.askServer(hostname, port, toServerBytes);

            String response = "HTTP/1.0 200 OK\r\n\r\n";
            byte[] responseBytesWithHeader = (response + new String(responseBytes, "UTF-8")).getBytes("UTF-8");
            outputStream.write(responseBytesWithHeader);

            connectionSocket.close();
        }
    }
}

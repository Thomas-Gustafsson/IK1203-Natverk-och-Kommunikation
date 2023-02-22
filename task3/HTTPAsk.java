import java.io.*;
import java.net.*;
import tcpclient.TCPClient;

public class HTTPAsk {
    public static void main(String[] args) throws Exception {
        int serverPort = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(serverPort);

        while (true) {
            try (Socket tcpConnectionSocket = serverSocket.accept();
                 InputStream inputStream = tcpConnectionSocket.getInputStream();
                 OutputStream outputStream = tcpConnectionSocket.getOutputStream()) {

                // Read the client request from browser as a string (GET...)
                byte[] clientRequestBytes = new byte[1024];
                int readBytes = inputStream.read(clientRequestBytes);
                String clientRequest = new String(clientRequestBytes, 0, readBytes);

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
                // Set each HTTP parameter as local variable to pass as arg to tcpClient.askServer()
                for (int i = 1; i < params.length; i++) {
                    String[] param = params[i].split("=");  // hostname=example.com
                    String key = param[0];  // hostname
                    String value = param[1]; // example.com

                    switch (key) {
                        case "hostname":
                            hostname = value;
                            break;
                        case "port":
                            try {
                                port = Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                            }
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
                        case "string":
                            toServerBytes = value.getBytes();
                    }
                }

                if (port <= 0 || port > 65535) {
                    String response = "HTTP/1.1 400 Bad Request\r\n\r\nInvalid port number";
                    outputStream.write(response.getBytes());
                    continue;
                }

                if (hostname.isEmpty()) {
                    String response = "HTTP/1.1 404 Not Found\r\n\r\nMissing hostname parameter";
                    outputStream.write(response.getBytes());
                    continue;
                }

                TCPClient tcpClient = new tcpclient.TCPClient(shutdown, timeout, limit);
                byte[] responseBytes = tcpClient.askServer(hostname, port, toServerBytes);
                ByteArrayOutputStream httpResponse = new ByteArrayOutputStream();
                httpResponse.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                httpResponse.write(responseBytes);
                byte[] responseBytesWithHeader = httpResponse.toByteArray();
                outputStream.write(responseBytesWithHeader);
            }
        }
    }
}


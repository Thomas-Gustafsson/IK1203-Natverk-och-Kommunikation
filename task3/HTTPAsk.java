import java.io.*;
import java.net.*;
import tcpclient.TCPClient;

public class HTTPAsk {

    public static void main(String[] args) {
        int serverPort = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                try {
                    InputStream inputStream = connectionSocket.getInputStream();
                    OutputStream outputStream = connectionSocket.getOutputStream();

                    String hostname = "";
                    int port = 0;
                    Integer limit = null;
                    boolean shutdown = false;
                    Integer timeout = null;
                    byte[] toServerBytes = new byte[0];
                    StringBuilder response = new StringBuilder();

                    // Read the client request from browser as a string (GET...)
                    byte[] clientRequestBytes = new byte[1024];
                    inputStream.read(clientRequestBytes);
                    String clientRequest = new String(clientRequestBytes).trim();

                    // Extract the parameters from the request
                    String[] requestArray = clientRequest.split("[ ?=&\r\n]+");

                    // Check if request contains "ask" keyword
                    if (!clientRequest.contains("ask")) {
                        response.append("HTTP/1.1 404 Not Found\r\n");
                        outputStream.write(response.toString().getBytes());
                        // Skip processing the rest of the loop for this request
                        continue;
                    }


                    // Check if all necessary parameters are present in the request
                    if (!clientRequest.contains("hostname") || !clientRequest.contains("port") || !clientRequest.contains("HTTP/1.1") || !clientRequest.contains("GET")) {
                        response.append("HTTP/1.1 400 Bad Request\r\n");
                        outputStream.write(response.toString().getBytes());
                        // Skip processing the rest of the loop for this request
                        continue;
                    }

                    // Set each HTTP parameter as local variable to pass as arg to tcpClient.askServer()
                    for (int i = 2; i < requestArray.length; i++) {
                        String key = requestArray[i];
                        switch (key) {
                            case "hostname":
                                hostname = requestArray[++i];
                                break;
                            case "port":
                                try {
                                    port = Integer.parseInt(requestArray[++i]);
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                                break;
                            case "limit":
                                limit = Integer.valueOf(requestArray[++i]);
                                break;
                            case "shutdown":
                                shutdown = Boolean.parseBoolean(requestArray[++i]);
                                break;
                            case "timeout":
                                timeout = Integer.valueOf(requestArray[++i]);
                                break;
                            case "string":
                                toServerBytes = requestArray[++i].getBytes();
                                break;
                            default:
                                break;
                        }
                    }

                    // Check if port number is valid
                    if (port <= 0 || port > 65535) {
                        response.append("HTTP/1.1 400 Bad Request\r\nInvalid port number");
                        outputStream.write(response.toString().getBytes());
                        continue;
                    }

                    // Check if hostname is empty
                    if (hostname.isEmpty()) {
                        response.append("HTTP/1.1 404 Not Found\r\nMissing hostname parameter");
                        outputStream.write(response.toString().getBytes());
                        continue;
                    }

                    TCPClient tcpClient = new tcpclient.TCPClient(shutdown, timeout, limit);
                    byte[] responseBytes = tcpClient.askServer(hostname, port, toServerBytes);
                    response.append("HTTP/1.1 200 OK\r\n\r\n");
                    response.append(new String(responseBytes, "UTF-8") + "\r\n");

                    outputStream.write(response.toString().getBytes());
                } finally {
                    connectionSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

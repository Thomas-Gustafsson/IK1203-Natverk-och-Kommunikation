package tcpclient;
import java.net.*;
import java.io.*;

import static java.lang.System.currentTimeMillis;

public class TCPClient {
    private final boolean shutdown;
    private final Integer timeout;
    private final Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             OutputStream outputStream = socket.getOutputStream();   // Used to write data to server
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server

            // Writing the data to be sent to the server
            outputStream.write(toServerBytes);
            outputStream.flush();

            ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
            long startTime = currentTimeMillis();
            while(currentTimeMillis() - startTime < timeout) {
                if(shutdown) {
                    System.out.println("Shutdown acknowledged. Data will not be received, closing connection.\n");
                    break;
                }
                if(receivedData != null){
                    // Reading data from server and storing it in a ByteArrayOutputStream
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        receivedData.write(buffer, 0, bytesRead);
                        if(limit != null && receivedData.size() >= limit) {
                            System.out.println("Data limit reached, closing connection.\n");
                            break;
                        }
                        if(bytesRead < buffer.length) {        // stop reading from input stream when there's no more data to be read
                            System.out.println("No more data to be read, closing connection.\n");
                            break;
                        }
                    }
                    return receivedData.toByteArray();
                } else {
                    System.out.println("No data has been received within the given time limit, closing connection.\n");
                    return null;
                }
            }
        } return null;
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server

            ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
            long startTime = currentTimeMillis();
            while(currentTimeMillis() - startTime < timeout) {
                if(shutdown) {
                    System.out.println("Shutdown acknowledged. Data will not be received, closing connection.\n");
                    break;
                }
                if(receivedData != null){
                    // Reading data from server and storing it in a ByteArrayOutputStream
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        receivedData.write(buffer, 0, bytesRead);
                        if(limit != null && receivedData.size() >= limit) {
                            System.out.println("Data limit reached, closing connection.\n");
                            break;
                        }
                        if(bytesRead < buffer.length) {        // stop reading from input stream when there's no more data to be read
                            System.out.println("No more data to be read, closing connection.\n");
                            break;
                        }
                    }
                    return receivedData.toByteArray();
                } else {
                    System.out.println("No data has been received within the given time limit, closing connection.\n");
                    return null;
                }
            }
        } return null;
    }
}


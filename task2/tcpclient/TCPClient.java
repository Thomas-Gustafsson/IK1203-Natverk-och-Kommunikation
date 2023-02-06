package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private final boolean shutdown;
    private Integer timeout = 0;
    private final Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        if(timeout != null){
            this.timeout = timeout;
        }
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             OutputStream outputStream = socket.getOutputStream();   // Used to write data to server
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server
            // Writing the data to be sent to the server
            outputStream.write(toServerBytes);
            outputStream.flush();

            if (shutdown) {
                return "\nShutdown acknowledged. Data will not be received, closing connection.".getBytes();
            }

            // Reading data from server and storing it in a ByteArrayOutputStream
            ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];

            socket.setSoTimeout(timeout);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                receivedData.write(buffer, 0, bytesRead);
                if (limit != null && receivedData.size() >= limit) {
                    System.out.println("Data limit reached, closing connection.\n");
                    break;
                }
                if (bytesRead < buffer.length) {   // stop reading from input stream when there's no more data to be read
                    break;
                }
            }
            return receivedData.toByteArray();
        } catch (SocketTimeoutException e) {
            return "\nTimeout reached, closing connection.".getBytes();
        }
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server

            if (shutdown) {
                return "\nShutdown acknowledged. Data will not be received, closing connection.".getBytes();
            }

            // Reading data from server and storing it in a ByteArrayOutputStream
            ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];

            socket.setSoTimeout(timeout);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                receivedData.write(buffer, 0, bytesRead);
                if (limit != null && receivedData.size() >= limit) {
                    System.out.println("Data limit reached, closing connection.\n");
                    break;
                }
                if (bytesRead < buffer.length) {   // stop reading from input stream when there's no more data to be read
                    break;
                }
            }
            return receivedData.toByteArray();
        } catch (SocketTimeoutException e) {
            return "\nTimeout reached, closing connection.".getBytes();
        }
    }
}
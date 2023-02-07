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
        ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             OutputStream outputStream = socket.getOutputStream();   // Used to write data to server
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server

            // Writing the data to be sent to the server
            outputStream.write(toServerBytes);
            outputStream.flush();

            if (shutdown) {
                socket.shutdownOutput();
                return "\nShutdown acknowledged. Data will not be received, connection closed.".getBytes();
            }

            // Reading data from server and storing it in a ByteArrayOutputStream
            int bytesRead;
            byte[] buffer = new byte[1024];

            socket.setSoTimeout(timeout);

            while ((bytesRead = inputStream.read(buffer)) != -1 && !socket.isOutputShutdown()) {
                if (limit != null && receivedData.size() + bytesRead > limit) {
                    receivedData.write(buffer, 0, limit - receivedData.size());
                    System.out.println("Data limit reached, returning data received so far.\n");
                    return receivedData.toByteArray();
                }
                receivedData.write(buffer, 0, bytesRead);
                if (bytesRead < buffer.length) {   // stop reading from input stream when there's no more data to be read
                    System.out.println("Success, all data received.\n");
                    break;
                }
            }
            return receivedData.toByteArray();
        } catch (SocketTimeoutException e) {
            if (receivedData.size() == 0) {
                return "\nTimeout reached, no data to return.".getBytes();
            } else {
                System.out.println("Timeout reached, returning data received so far.\n");
                return receivedData.toByteArray();
            }
        }
    }


    public byte[] askServer(String hostname, int port) throws IOException {
        ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server

            if (shutdown) {
                socket.shutdownOutput();
                return "\nShutdown acknowledged. Data will not be received, connection closed.".getBytes();
            }

            // Reading data from server and storing it in a ByteArrayOutputStream
            int bytesRead;
            byte[] buffer = new byte[1024];

            socket.setSoTimeout(timeout);

            while ((bytesRead = inputStream.read(buffer)) != -1 && !socket.isOutputShutdown()) {
                if (limit != null && receivedData.size() + bytesRead > limit) {
                    receivedData.write(buffer, 0, limit - receivedData.size());
                    System.out.println("Data limit reached, returning data received so far.\n");
                    return receivedData.toByteArray();
                }
                receivedData.write(buffer, 0, bytesRead);
                if (bytesRead < buffer.length) {   // stop reading from input stream when there's no more data to be read
                    System.out.println("Success, all data received.\n");
                    break;
                }
            }
            return receivedData.toByteArray();
        } catch (SocketTimeoutException e) {
            if (receivedData.size() == 0) {
                return "\nTimeout reached, no data to return.".getBytes();
            } else {
                System.out.println("Timeout reached, returning data received so far.\n");
                return receivedData.toByteArray();
            }
        }
    }
}
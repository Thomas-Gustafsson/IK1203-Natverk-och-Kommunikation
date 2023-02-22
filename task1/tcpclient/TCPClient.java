package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    public TCPClient() { // default constructor
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             OutputStream outputStream = socket.getOutputStream();   // Used to write data to server
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server

            // Writing the data to be sent to the server
            outputStream.write(toServerBytes);
            outputStream.flush();

            // Reading data from server and storing it in a ByteArrayOutputStream
            ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                receivedData.write(buffer, 0, bytesRead);
                if (bytesRead < buffer.length) {        // stop reading from input stream when there's no more data to be read
                    break;
                }
            }

            return receivedData.toByteArray();
        }
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        try (Socket socket = new Socket(hostname, port);             // Creating the socket and using try-with-resources to automatically close the socket after execution
             InputStream inputStream = socket.getInputStream()) {    // Used to read data from server

            // Reading data from server and storing it in a ByteArrayOutputStream
            ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                receivedData.write(buffer, 0, bytesRead);
                if (bytesRead < buffer.length) {        // stop reading from input stream when there's no more data to be read
                    break;
                }
            }

            return receivedData.toByteArray();
        }
    }
}

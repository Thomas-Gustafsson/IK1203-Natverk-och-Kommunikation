package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    public TCPClient() { // default constructor, likely to not be used in code
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        Socket socket = new Socket(hostname, port);             // Creating the socket
        OutputStream outputStream = socket.getOutputStream();   // Used to write data to server
        InputStream inputStream = socket.getInputStream();      //         read

        outputStream.write(toServerBytes);
        outputStream.flush();

        ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            receivedData.write(buffer, 0, bytesRead);
            if (bytesRead < buffer.length) {
                break;
            }
        }

        socket.close();

        return receivedData.toByteArray();
    }
}

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

        int totalBytesRead = 0;
        int bytesRead;
        byte[] fromServerBytes = new byte[1024];
        while ((bytesRead = inputStream.read(fromServerBytes, totalBytesRead, fromServerBytes.length - totalBytesRead)) > 0) {
            totalBytesRead += bytesRead;
            if (totalBytesRead == fromServerBytes.length) {
                byte[] newArray = new byte[fromServerBytes.length * 2];
                System.arraycopy(fromServerBytes, 0, newArray, 0, fromServerBytes.length);
                fromServerBytes = newArray;
            }
        }

        socket.close();

        byte[] response = new byte[totalBytesRead];
        System.arraycopy(fromServerBytes, 0, response, 0, totalBytesRead);
        return response;
    }
}

package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private InputStream inputStream;
    private OutputStream outputStream;

    public TCPClient() { // default constructor, likely to not be used in code
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
    Socket socket = new Socket(hostname, port);             // Creating the socket
    Outputstream outputStream = socket.getOutputStream();   // Used to write data to server
    Inputstream inputStream = socket.getInputStream();      //         read

    outputStream.write(toServerBytes);
    outputstream.flush();

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

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

    private byte[] handleServerRequest(String hostname, int port, byte[] toServerBytes, boolean writeData) throws IOException {
        ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
        try (Socket socket = new Socket(hostname, port);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = writeData ? socket.getOutputStream() : null) {

            if (shutdown) {
                socket.shutdownOutput();
                return "\nShutdown acknowledged. Data will not be received, connection closed.".getBytes();
            }

            int bytesRead;
            byte[] buffer = new byte[1024];

            socket.setSoTimeout(timeout);

            if (writeData) {
                outputStream.write(toServerBytes);
                outputStream.flush();
            }

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
            } else { // might happen if server manages to send data but lags -timeout- ms between sending bytes
                System.out.println("Timeout reached, returning data received so far.\n");
                return receivedData.toByteArray();
            }
        }
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        return handleServerRequest(hostname, port, toServerBytes, true);
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        return handleServerRequest(hostname, port, null, false);
    }

}
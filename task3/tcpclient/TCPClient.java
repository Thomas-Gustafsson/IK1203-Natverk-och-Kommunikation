package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private final boolean shutdown;
    private final Integer timeout;
    private final Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    private byte[] handleServerRequest(String hostname, int port, byte[] toServerBytes, boolean writeData) throws IOException {
        ByteArrayOutputStream receivedData = new ByteArrayOutputStream();
        try (Socket socket = new Socket(hostname, port);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = writeData ? socket.getOutputStream() : null) {

            if (writeData) {
                outputStream.write(toServerBytes);
                outputStream.flush();
            }

            if (shutdown) socket.shutdownOutput();

            int bytesRead;
            byte[] buffer = new byte[1024];
            if (timeout != null) socket.setSoTimeout(timeout);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (limit != null && receivedData.size() + bytesRead > limit) {
                    receivedData.write(buffer, 0, limit - receivedData.size());
                    System.out.println("Data limit reached with limit " + limit + ", returning data received so far.\n");
                    break;
                }
                receivedData.write(buffer, 0, bytesRead);
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
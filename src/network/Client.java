package network;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 9806;

    // sends one request, returns the vcc's decision, null if the server can't be reached
    public static String sendRequestToServer(String data) {
        try (
                Socket socket = new Socket(HOST, PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            // client sends the request to the server
            outputStream.writeUTF(data);

            // server acknowledges right away
            String acknowledgment = inputStream.readUTF();
            System.out.println("Server replied: " + acknowledgment);

            // waits here until the vcc clicks accept or reject
            return inputStream.readUTF();

        } catch (IOException e) {
            System.err.println("Client connection failed: " + e.getMessage());
            return null;
        }
    }
}

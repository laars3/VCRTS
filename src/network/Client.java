package network;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 9806;

    public static void sendRequestToServer(String data) {
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // TODO (Roni - Phase 1): Send the formatted data string to the Server
            out.println(data);

            // TODO (Roni - Phase 1): Read the immediate Acknowledgment reply from the Server
            String acknowledgment = in.readLine();
            System.out.println("Server replied: " + acknowledgment);

            // TODO (Anthony - Phase 3): Wait for the final Accept/Reject notification from the Server to show in the GUI

        } catch (IOException e) {
            System.err.println("Client connection failed: " + e.getMessage());
        }
    }
}
package network;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Read data string from the GUI Client
            String requestData = in.readLine();
            System.out.println("Received from client: " + requestData);

            // TODO (Lars - Phase 1): Send an immediate acknowledgment back to the client
            out.println("Acknowledgment: Request received by VCC Server and is pending review.");

            // TODO (Integration - Phase 2): Hold this requestData so Ryan and Niyo's AdminFrame can Accept/Reject it

        } catch (IOException e) {
            System.err.println("ClientHandler exception: " + e.getMessage());
        }
    }
}
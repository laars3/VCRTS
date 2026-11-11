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
        // no try with resources, closing the streams would close the socket before accept/reject
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // one line per request, e.g. JOB,client1,P-1,...
            String request = in.readLine();
            System.out.println("Received: " + request);

            out.println("ACK");

            // todo: add the request to the server's pending list
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        }
    }
}

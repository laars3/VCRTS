package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("VCC Server is starting...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            // Infinite loop to accept multiple concurrent clients
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");

                // TODO (Lars - Phase 1): Make a new thread using ClientHandler so the server doesn't freeze
                ClientHandler handler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(String.valueOf(handler));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }
}
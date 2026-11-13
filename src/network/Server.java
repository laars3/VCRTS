package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server implements Runnable {
    private static final int PORT = 9806;

    // handler threads add to this at the same time, so it's synchronized
    private final List<ClientHandler> pending = Collections.synchronizedList(new ArrayList<ClientHandler>());

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            // each client gets its own thread so the loop can go back to waiting
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                Thread clientThread = new Thread(handler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server stopped: " + e.getMessage());
        }
    }

    void addPending(ClientHandler request) {
        pending.add(request);
    }

    // copy so the admin table can't change the real list
    public List<ClientHandler> getPending() {
        synchronized (pending) {
            return new ArrayList<ClientHandler>(pending);
        }
    }

    // the admin saves the data first, then calls this to tell the client
    public void accept(ClientHandler request) {
        if (pending.remove(request)) {
            request.reply("Accepted");
        }
    }

    // nothing is saved, the client is just told
    public void reject(ClientHandler request) {
        if (pending.remove(request)) {
            request.reply("Rejected");
        }
    }
}

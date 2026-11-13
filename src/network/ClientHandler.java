package network;

import java.io.*;
import java.net.Socket;

// reads one request and acks it, then waits in the server's pending list for the vcc's decision
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private DataOutputStream outputStream;
    private String line = "";

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        // no try with resources, closing the streams would close the socket before accept/reject
        try {
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());

            // one message per request, e.g. JOB,client1,P-1,...
            line = inputStream.readUTF();
            System.out.println("Received: " + line);

            outputStream.writeUTF("ACK");

            // empty request, turn it down right away
            if (line.trim().isEmpty()) {
                reply("Rejected");
                return;
            }

            // socket stays open here, the vcc's decision goes back through it later
            server.addPending(this);
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        }
    }

    // JOB or VEHICLE
    public String getType() {
        return field(0);
    }

    // username of whoever sent it
    public String getSender() {
        return field(1);
    }

    // job id or vehicle id
    public String getId() {
        return field(2);
    }

    // every field after the type, in the order the client sent them
    public String[] getFields() {
        String[] parts = line.split(",", -1);
        String[] fields = new String[parts.length - 1];
        System.arraycopy(parts, 1, fields, 0, fields.length);
        return fields;
    }

    public String getLine() {
        return line;
    }

    private String field(int index) {
        String[] parts = line.split(",", -1);
        return index < parts.length ? parts[index] : "";
    }

    // sends the decision and closes the connection
    void reply(String decision) {
        try {
            outputStream.writeUTF(decision);
            clientSocket.close();
        } catch (IOException e) {
            // client closed its window before the vcc decided
            System.err.println("Could not reply to client: " + e.getMessage());
        }
    }
}

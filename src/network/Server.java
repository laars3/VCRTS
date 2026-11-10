package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private static final int PORT = 9806;

    @Override
    public void run() {
        // todo: open the server socket, accept clients in a loop, one handler thread each
    }
}

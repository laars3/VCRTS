import gui.*;
import javax.swing.JFrame;
import network.Server;
import storage.UserStore;
import storage.VehicleStore;
import storage.JobStore;

public class Main{
  public static void main(String[] args) {
        // Keep the same stores for the entire application session.
        UserStore userStore = new UserStore();
        VehicleStore vehicleStore = new VehicleStore(userStore);
        JobStore jobStore = new JobStore(userStore);

        // server waits for clients in the background so the gui doesn't freeze
        Server server = new Server();
        new Thread(server).start();

        // the admin frame gets the server through here to accept or reject requests
        JFrame login = new StartFrame(userStore, vehicleStore, jobStore, server);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setVisible(true);
    }
}

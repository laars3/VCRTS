import gui.*;
import javax.swing.JFrame;
import storage.UserStore;
import storage.VehicleStore;
import storage.JobStore;

public class Main{
  public static void main(String[] args) {
        // Keep the same stores for the entire application session.
        UserStore userStore = new UserStore();
        VehicleStore vehicleStore = new VehicleStore(userStore);
        JobStore jobStore = new JobStore(userStore);

        JFrame login = new StartFrame(userStore, vehicleStore, jobStore);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setVisible(true);
    }
}

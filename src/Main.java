import gui.*;
import javax.swing.JFrame;
import storage.UserStore;

public class Main{
    public static void main(String[] args){
        // Keep one user store while the app is open.
        UserStore userStore = new UserStore();
        JFrame login = new StartFrame(userStore);

        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setVisible(true);
    }
}
import gui.*;
import javax.swing.JFrame;
import storage.UserStore;

public class Main{
    public static void main(String[] args){
        // one user store shared by every frame
        UserStore userStore = new UserStore();
        JFrame login = new StartFrame(userStore);

        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setVisible(true);
    }
}
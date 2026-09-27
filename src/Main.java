import gui.*;
import javax.swing.JFrame;

public class Main{
    public static void main(String[] args){
        JFrame owner = new StartFrame();

        owner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        owner.setVisible(true);
    }
}
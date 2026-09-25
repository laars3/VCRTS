import gui.OwnerFrame;
import javax.swing.JFrame;

public class Main{
    public static void main(String[] args){
        JFrame frame = new OwnerFrame(); // owner frame just for testing

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
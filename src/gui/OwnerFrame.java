package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OwnerFrame extends JFrame{
    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 720;

    private JButton button;


    public OwnerFrame(){

        registrationButton();
        createPanel();

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    class AddRegistrationListener implements ActionListener{
        public void actionPerformed(ActionEvent event){

        }
    }

    private void registrationButton(){

        button = new JButton("Register Vehicle");

        ActionListener listener = new AddRegistrationListener();
        button.addActionListener(listener);
        System.out.println("click");
    }

    private void createPanel(){
        JPanel panel = new JPanel();
        panel.add(button);
        add(panel);
    }
}



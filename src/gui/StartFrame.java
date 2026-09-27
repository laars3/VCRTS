package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StartFrame extends JFrame{
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 400;

    private JButton ownerButton;
    private JButton clientButton;

    private OwnerFrame ownerFrame;

    public StartFrame(){

        super("VCRTS");
        ownerButton();
        createPanel();

        setSize(FRAME_WIDTH,FRAME_HEIGHT);

    }

    class OwnerListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (ownerFrame == null || !ownerFrame.isDisplayable()){
                ownerFrame = new OwnerFrame();
            }
            ownerFrame.setVisible(true);
            ownerFrame.toFront();
        }
    }

    private void ownerButton(){
        ownerButton = new JButton("Vehicle Owner");

        ActionListener listener = new OwnerListener();
        ownerButton.addActionListener(listener);

    }

    private void createPanel(){
        JPanel panel = new JPanel();

        panel.add(ownerButton);

        add(panel);
    }

}
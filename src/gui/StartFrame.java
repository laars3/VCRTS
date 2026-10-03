package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import storage.UserStore;

public class StartFrame extends JFrame{
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 400;

    private JButton ownerButton;
    private JButton clientButton;
    private JButton createAccountButton;

    private OwnerFrame ownerFrame;
    private ClientFrame clientFrame;
    private CreateAccountFrame createAccountFrame;

    // shared with every frame that needs accounts
    private UserStore userStore;

    public StartFrame(UserStore userStore){

        super("VCRTS");
        this.userStore = userStore;
        ownerButton();
        clientButton();
        createAccountButton();
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

    private void clientButton(){
        clientButton = new JButton("Job Owner");

        ActionListener listener = new ClientListener();
        clientButton.addActionListener(listener);
    }

    class ClientListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (clientFrame == null || !clientFrame.isDisplayable()){
                clientFrame = new ClientFrame();
            }
            clientFrame.setVisible(true);
            clientFrame.toFront();
        }
    }

    private void createAccountButton(){
        createAccountButton = new JButton("Create Account");

        ActionListener listener = new CreateAccountListener();
        createAccountButton.addActionListener(listener);
    }

    class CreateAccountListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (createAccountFrame == null || !createAccountFrame.isDisplayable()){
                createAccountFrame = new CreateAccountFrame(userStore);
            }
            createAccountFrame.setVisible(true);
            createAccountFrame.toFront();
        }
    }

    private void createPanel(){
        JLabel title = new JLabel("VCRTS");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel intro = new JLabel("<html><body style='width: 260px'>" + "Vehicular Cloud Real Time System. Vehicle owners rent out their car's computing power, and job owners submit jobs to run on it." + "</body></html>");

        JPanel introPanel = new JPanel(new BorderLayout(0, 6));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);

        introPanel.add(new JSeparator(), BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ownerButton);
        buttonPanel.add(clientButton);
        buttonPanel.add(createAccountButton);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

}
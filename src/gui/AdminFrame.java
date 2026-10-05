package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Small portal for VCC admins, lets them jump into both the owner and client views.
public class AdminFrame extends JFrame{

    private JButton ownerViewButton;
    private JButton clientViewButton;

    // start frame keeps the owner and client frames, so admin sees the same ones users do
    private StartFrame startFrame;

    public AdminFrame(StartFrame startFrame){

        super("VCC Admin");
        this.startFrame = startFrame;
        createButtons();
        createPanel();

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void createButtons(){
        ownerViewButton = new JButton("Open Owner View");
        ownerViewButton.addActionListener(new OwnerViewListener());

        clientViewButton = new JButton("Open Client View");
        clientViewButton.addActionListener(new ClientViewListener());
    }

    class OwnerViewListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            startFrame.openOwnerFrame();
        }
    }

    class ClientViewListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            startFrame.openClientFrame();
        }
    }

    private void createPanel(){
        JLabel title = new JLabel("VCC Admin Portal");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel intro = new JLabel("<html><body style='width: 240px'>" + "Open either view to see what vehicle owners and job owners see." + "</body></html>");

        JPanel introPanel = new JPanel(new BorderLayout(0, 6));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);
        introPanel.add(new JSeparator(), BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        buttonPanel.add(ownerViewButton);
        buttonPanel.add(clientViewButton);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
}

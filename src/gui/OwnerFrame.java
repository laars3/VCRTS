/*
 * Author: Anthony
 * Prev Author: Lars
 * README Task #9: Owner Frame Introductions
 * Problem Addressed: The Owner window lacked structural UI elements and direction. Added an introductory header to explain that this page is for registering vehicles to rent out computation power.
 * Java Components Implemented: BorderLayout (for organizing the screen), JLabel (for the text), Font & SwingConstants (for styling and centering).
 */


package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OwnerFrame extends JFrame {
    private JButton button;
    private RegistrationFrame registrationFrame;

    public OwnerFrame() {
        super("Owner View");
        setSize(1280, 720);

        // 1. set the layout rules so we can organize the screen
        setLayout(new BorderLayout());

        // 2. create the introduction text for the car owner
        JLabel introLabel = new JLabel("<html><div style='text-align: center;'>Hey Owner! Welcome to our VCRTS app.<br>Here, you can register your vehicle to rent out its computational power.</div></html>", SwingConstants.CENTER);
        introLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // 3. pin the text to the top of the window
        add(introLabel, BorderLayout.NORTH);

        // 4. create the registration button
        button = new JButton("Register Vehicle");
        button.addActionListener(new AddRegistrationListener());

        // 5. place the button in the center
        JPanel panel = new JPanel();
        panel.add(button);
        add(panel, BorderLayout.CENTER);
    }

    // This class handles the button click event
    class AddRegistrationListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // Open the registration window
            if (registrationFrame == null || !registrationFrame.isDisplayable()) {
                registrationFrame = new RegistrationFrame();
            }
            // Show the submission window and bring it to the user's focus
            registrationFrame.setVisible(true);
            registrationFrame.toFront();
        }
    }
}
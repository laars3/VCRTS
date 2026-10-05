/*
 * Author: Anthony
 * Prev Author: Ryan
 * README Task #9: Client Frame Introductions
 * Problem Addressed: The Client window was previously blank and lacked context. Added a structural layout and introduction text to clarify that this page is for submitting computational jobs.
 * Java Components Implemented: BorderLayout (for organizing the screen), JLabel (for the text), Font & SwingConstants (for styling and centering).
 */

package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientFrame extends JFrame {
    private JButton submitJobButton;
    private JobSubmissionFrame jobSubmissionFrame;

    public ClientFrame() {
        super("Client View");
        setSize(1280, 720);

        // 1. set the layout rules so we can put things at the top, bottom, or center
        setLayout(new BorderLayout());

        // 2. create the introduction text and make it large and centered
        JLabel introLabel = new JLabel("<html><div style='text-align: center;'>Hey Client! Welcome to our VCRTS app.<br>Here, you can submit your computational jobs to the Vehicular Cloud.</div></html>", SwingConstants.CENTER);
        introLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // 3. pin the introduction text to the top (NORTH) of the screen
        add(introLabel, BorderLayout.NORTH);

        // 4. create the button and tell it what to do when clicked
        submitJobButton = new JButton("Submit Job");
        submitJobButton.addActionListener(new AddJobListener());

        // 5. put the button inside a panel, and put that panel at the center of the screen
        JPanel panel = new JPanel();
        panel.add(submitJobButton);
        add(panel, BorderLayout.CENTER);
    }

    // This class handles the submission button click event
    class AddJobListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // Lazy initialization (to save memory): don't build the jobSubmissionFrame window until the exact moment the user clicks the button.
            if (jobSubmissionFrame == null || !jobSubmissionFrame.isDisplayable()) {
                jobSubmissionFrame = new JobSubmissionFrame();
            }
            // Show the submission window and bring it to the user's focus
            jobSubmissionFrame.setVisible(true);
            jobSubmissionFrame.toFront();
        }
    }
}
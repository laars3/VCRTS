package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import storage.TransactionLog;

/*
 * Author: Anthony
 * Prev Author: Ryan
 * README Task #9: Client Frame Introductions
 * Problem Addressed: The Client window was previously blank and lacked context. Added a structural layout and introduction text to clarify that this page is for submitting computational jobs.
 * Java Components Implemented: BorderLayout (for organizing the screen), JLabel (for the text), Font & SwingConstants (for styling and centering).
 */

public class ClientFrame extends JFrame {
    private JButton submitJobButton;
    private JobSubmissionFrame jobSubmissionFrame;

    public ClientFrame() {
        super("Client View");
        setSize(1280, 720);

        setLayout(new BorderLayout());

        JLabel introLabel = new JLabel("<html><div style='text-align: center;'>Hey Client! Welcome to our VCRTS app.<br>Here, you can submit your computational jobs to the Vehicular Cloud.</div></html>", SwingConstants.CENTER);
        introLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(introLabel, BorderLayout.NORTH);

        submitJobButton = new JButton("Submit Job");
        submitJobButton.addActionListener(new AddJobListener());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(submitJobButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    class AddJobListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (jobSubmissionFrame == null || !jobSubmissionFrame.isDisplayable()) {
                jobSubmissionFrame = new JobSubmissionFrame();
            }
            jobSubmissionFrame.setVisible(true);
            jobSubmissionFrame.toFront();
        }
    }
}

// --- RYAN'S ORIGINAL POPUP CODE ---
class JobSubmissionFrame extends JFrame {
    private JTextField clientIdField;
    private JTextField jobIdField;
    private JTextField jobDurationField;
    private JTextField jobDeadlineField;
    private JButton submitButton;

    public JobSubmissionFrame() {
        super("Submit Job");
        this.createTextFields();
        this.createButton();
        this.createPanel();
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createTextFields() {
        this.clientIdField = new JTextField(20);
        this.jobIdField = new JTextField(20);
        this.jobDurationField = new JTextField(20);
        this.jobDeadlineField = new JTextField(20);
    }

    private void createButton() {
        this.submitButton = new JButton("Submit");
        this.submitButton.addActionListener(new SubmitListener());
    }

    private void createPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Client ID:"));
        formPanel.add(this.clientIdField);
        formPanel.add(new JLabel("Job ID:"));
        formPanel.add(this.jobIdField);
        formPanel.add(new JLabel("Job Duration (hours):"));
        formPanel.add(this.jobDurationField);
        formPanel.add(new JLabel("Job Deadline:"));
        formPanel.add(this.jobDeadlineField);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.submitButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        this.add(mainPanel);
    }

    class SubmitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String clientId = clientIdField.getText();
            String jobId = jobIdField.getText();
            String jobDuration = jobDurationField.getText();
            String jobDeadline = jobDeadlineField.getText();
            TransactionLog.append("Client: " + clientId + ", Job ID: " + jobId + ", Job Duration: " + jobDuration + ", Job Deadline: " + jobDeadline);
            dispose();
        }
    }
}

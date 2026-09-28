package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import storage.TransactionLog;

public class ClientFrame extends JFrame {

    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 720;

    private JButton submitJobButton;
    private JobSubmissionFrame jobSubmissionFrame;

    public ClientFrame() {
        super("Job Owner View");

        submitJobButton();
        createPanel();

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
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

    private void submitJobButton() {

        submitJobButton = new JButton("Submit Job");

        ActionListener listener = new AddJobListener();
        submitJobButton.addActionListener(listener);
    }

    private void createPanel() {

        JPanel panel = new JPanel();

        panel.add(submitJobButton);

        add(panel);
    }
}


class JobSubmissionFrame extends JFrame {

    private JTextField clientIdField;
    private JTextField jobIdField;
    private JTextField jobDurationField;
    private JTextField jobDeadlineField;

    private JButton submitButton;

    public JobSubmissionFrame() {

        super("Submit Job");

        createTextFields();
        createButton();
        createPanel();

        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createTextFields() {

        clientIdField = new JTextField(20);
        jobIdField = new JTextField(20);
        jobDurationField = new JTextField(20);
        jobDeadlineField = new JTextField(20);
    }

    private void createButton() {

        submitButton = new JButton("Submit");

        submitButton.addActionListener(new SubmitListener());
    }

    private void createPanel() {

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        formPanel.add(new JLabel("Client ID:"));
        formPanel.add(clientIdField);

        formPanel.add(new JLabel("Job ID:"));
        formPanel.add(jobIdField);

        formPanel.add(new JLabel("Job Duration (hours):"));
        formPanel.add(jobDurationField);

        formPanel.add(new JLabel("Job Deadline:"));
        formPanel.add(jobDeadlineField);


        JPanel buttonPanel = new JPanel();

        buttonPanel.add(submitButton);


        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }


    class SubmitListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            String clientId = clientIdField.getText();
            String jobId = jobIdField.getText();
            String jobDuration = jobDurationField.getText();
            String jobDeadline = jobDeadlineField.getText();

            TransactionLog.append(
                    "Client: " + clientId +
                    ", Job ID: " + jobId +
                    ", Job Duration: " + jobDuration +
                    ", Job Deadline: " + jobDeadline
            );

            JobSubmissionFrame.this.dispose();
        }
    }
}

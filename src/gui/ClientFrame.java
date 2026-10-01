package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import javax.swing.*;

import entity.Job;
import entity.JobOwner;
import storage.JobStore;
import storage.TransactionLog;
import storage.UserStore;

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


    // On submit: builds a Job from the form, links it to its JobOwner, saves both to the stores, and logs it.
    class SubmitListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            String clientId = clientIdField.getText();
            String jobId = jobIdField.getText();
            String jobDurationText = jobDurationField.getText();
            String jobDeadlineText = jobDeadlineField.getText();

            try {
                int durationHours = Integer.parseInt(jobDurationText.trim());
                int durationMinutes = durationHours * 60;

                LocalDateTime deadline = null;
                if (jobDeadlineText != null && !jobDeadlineText.isBlank()) {
                    // Expected format for now: yyyy-MM-ddTHH:mm
                    // (form will need a real date picker later per spec #11)
                    deadline = LocalDateTime.parse(jobDeadlineText.trim());
                }

                // Reuse the job owner's account if we've already seen this clientId, else create one.
                JobOwner owner = (JobOwner) UserStore.get(clientId);
                if (owner == null) {
                    owner = new JobOwner(clientId, "", "");
                    UserStore.add(owner);
                }

                // jobType and inputFilePath aren't collected by this form yet (spec #11),
                // so they're left null until that part of the form is built.
                Job job = new Job(clientId, jobId, null, durationMinutes, deadline, null);

                JobStore.add(job);
                owner.addJobId(jobId);

                TransactionLog.append(
                        "Client: " + clientId +
                        ", Job ID: " + jobId +
                        ", Job Duration: " + durationHours + "h" +
                        ", Job Deadline: " + jobDeadlineText
                );

            } catch (DateTimeParseException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(JobSubmissionFrame.this,
                        "Invalid input: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JobSubmissionFrame.this.dispose();
        }
    }
}

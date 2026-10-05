package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel; // Required for the "dynamic film shooting schedule"
import storage.TransactionLog;

/*
 * Author: Anthony
 * Prev Authors: Ryan (Popup Code) & Roni (UI Styling)
 * README Tasks #2, #3, #4, & #7: Client Dashboard, Live Data Updates, & Job Selection
 * Problem Addressed: The Client window lacked a dashboard to display submitted jobs, and the submission form relied on manual text entry for job types (causing potential data errors).
 *                    Added a dynamic dashboard that instantly updates when a user submits a job, and a dropdown menu to modify job type selection.
 * Film Analogy Explained: To make the architecture easier to explain, I compare DefaultTableModel is compared to a "dynamic film shooting schedule."
 *                         While a standard 2D array is like a locked, printed script (rigid and hard to add scenes to once printed), the DefaultTableModel acts like a digital schedule that lets us safely inject new rows (scenes) on the fly while the application is running.
 * Java Components Implemented: DefaultTableModel ("dynamic film shooting schedule"), JTable & JScrollPane (dashboard display), JComboBox ("film script supervisor" preventing bad data entry).
 */
public class ClientFrame extends JFrame {
    private JButton submitJobButton;
    private JobSubmissionFrame jobSubmissionFrame;

    // Think of this like a dynamic shooting schedule on a film set.
    // Unlike a locked printed script (a standard array), this lets us add new scenes (rows) on the fly while we're shooting content.
    private DefaultTableModel tableModel;

    public ClientFrame() {
        super("Client View");
        setSize(1280, 720);
        setLocationRelativeTo(null);

        // use a BorderLayout so we can easily pin things to the top, center, and bottom of the screen.
        setLayout(new BorderLayout(10, 10));

        // 1. The Introduction Text (Pinned to the top)
        JLabel introLabel = new JLabel("<html><div style='text-align: center;'>Hey Client! Welcome to our VCRTS app.<br>Here, you can submit your computational jobs to the Vehicular Cloud.</div></html>", SwingConstants.CENTER);
        introLabel.setFont(new Font("Arial", Font.BOLD, 24));
        introLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(introLabel, BorderLayout.NORTH);

        // 2. The Dashboard (Pinned to the center)
        String[] columnNames = {"Client ID", "Job ID", "Job Type", "Duration", "Deadline", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // adding some intial (fake) placeholder data so we can see what a populated dashboard looks like.
        tableModel.addRow(new Object[]{"C-901", "J-001", "Language Translation", "2 hours", "10/05/2026", "Completed ✅"});
        tableModel.addRow(new Object[]{"C-902", "J-002", "Image Generation", "1 hour", "10/06/2026", "In Progress 🔄"});
        tableModel.addRow(new Object[]{"C-903", "J-003", "Data Analysis", "5 hours", "10/07/2026", "Pending ⏳"});

        // wrap the table in a scroll pane, so if the list gets too long, the user can still scroll through it.
        JTable clientTable = new JTable(tableModel);
        clientTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        add(scrollPane, BorderLayout.CENTER);

        // 3. The Submit Button (Pinned to the bottom)
        submitJobButton = new JButton("Submit Job");
        submitJobButton.addActionListener(new AddJobListener());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(submitJobButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // This is our "Director's Cue". It tells the program what to do when the button is clicked.
    class AddJobListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // we only build the popup window when the user actually clicks the button to save memory.
            if (jobSubmissionFrame == null || !jobSubmissionFrame.isDisplayable()) {
                // fixes the data entry user input-dummy-data-dashboard problem
                // CRITICAL STEP: we hand the popup a direct communication line back to our tableModel.
                // This allows the popup to instantly send data back to this main screen.
                jobSubmissionFrame = new JobSubmissionFrame(tableModel);
            }
            jobSubmissionFrame.setVisible(true);
            jobSubmissionFrame.toFront();
        }
    }
}


class JobSubmissionFrame extends JFrame {
    private JTextField clientIdField;
    private JTextField jobIdField;
    private JComboBox<String> jobTypeBox;
    private JTextField jobDurationField;
    private JTextField jobDeadlineField;
    private JButton submitButton;

    // this is where we store the communication line to the main dashboard.
    private DefaultTableModel mainTableModel;

    public JobSubmissionFrame(DefaultTableModel tableModel) {
        super("Submit Job");
        this.mainTableModel = tableModel; // save the link to the main screen

        this.createFields();
        this.createButton();
        this.createPanel();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createFields() {
        this.clientIdField = new JTextField(20);
        this.jobIdField = new JTextField(20);

        // The Dropdown Menu (JComboBox).
        // This is our "film script supervisor" — it forces the user to pick from a specific list of dropdowns instead of typing the jobs out, preventing typos & backend errors.
        String[] jobTypes = {"Language Translation", "Image Generation", "Data Analysis", "Video Rendering"};
        this.jobTypeBox = new JComboBox<>(jobTypes);

        this.jobDurationField = new JTextField(20);
        this.jobDeadlineField = new JTextField(20);
    }

    private void createButton() {
        this.submitButton = new JButton("Submit");
        this.submitButton.addActionListener(new SubmitListener());
    }

    private void createPanel() {
        // we use a grid layout to cleanly line up our text labels with the input boxes.
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Client ID:"));
        formPanel.add(this.clientIdField);
        formPanel.add(new JLabel("Job ID:"));
        formPanel.add(this.jobIdField);
        formPanel.add(new JLabel("Job Type:"));
        formPanel.add(this.jobTypeBox);
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

    // this is what happens when the user clicks "Submit" inside the popup window
    class SubmitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // 1. Grab all the information the user just entered
            String clientId = clientIdField.getText();
            String jobId = jobIdField.getText();
            String jobType = (String) jobTypeBox.getSelectedItem();
            String jobDuration = jobDurationField.getText();
            String jobDeadline = jobDeadlineField.getText();

            // 2. Save it to the transaction log file
            TransactionLog.append("Client: " + clientId + ", Job ID: " + jobId + ", Job Type: " + jobType + ", Job Duration: " + jobDuration + ", Job Deadline: " + jobDeadline);

            // 3. Package the new data into a row and instantly send it back to the main dashboard
            mainTableModel.addRow(new Object[]{clientId, jobId, jobType, jobDuration, jobDeadline, "Pending ⏳"});

            // 4. Close the popup window
            dispose();
        }
    }
}
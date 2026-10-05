package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import storage.TransactionLog;

public class ClientFrame extends JFrame {

    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 720;

    private JButton submitJobButton;
    private JobSubmissionFrame jobSubmissionFrame;
    private DefaultTableModel tableModel;

    public ClientFrame() {
        super("Job Owner View");

        submitJobButton();
        createTable();
        createPanel();

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    class AddJobListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            if (jobSubmissionFrame == null || !jobSubmissionFrame.isDisplayable()) {
                // popup gets the table so submitted jobs show up here
                jobSubmissionFrame = new JobSubmissionFrame(tableModel);
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

    private void createTable() {

        String[] columnNames = {"Client ID", "Job ID", "Job Type", "Duration", "Deadline", "Status"};

        // a table model instead of a plain array so new rows can be added while the app runs
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // dummy rows for now
        tableModel.addRow(new String[]{"C-901", "J-001", "Language Translation", "2 hours", "10/05/2026", "Completed"});
        tableModel.addRow(new String[]{"C-902", "J-002", "Image Generation", "1 hour", "10/06/2026", "In Progress"});
        tableModel.addRow(new String[]{"C-903", "J-003", "Data Analysis", "5 hours", "10/07/2026", "Pending"});
    }

    private void createPanel() {

        JLabel title = new JLabel("Submitted Jobs");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel intro = new JLabel("All jobs sent to the cloud. Submit a new one below to run it on a vehicle.");
        intro.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel introPanel = new JPanel(new BorderLayout(0, 4));
        introPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);

        JTable jobTable = new JTable(tableModel);
        jobTable.setFillsViewportHeight(true);
        jobTable.getTableHeader().setReorderingAllowed(false);
        jobTable.getTableHeader().setResizingAllowed(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(submitJobButton);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}


class JobSubmissionFrame extends JFrame {

    private JTextField clientIdField;
    private JTextField jobIdField;
    private JComboBox<String> jobTypeBox;
    private JTextField jobDurationField;
    private JTextField jobDeadlineField;

    private JButton submitButton;

    // client frame's table, new jobs go here
    private DefaultTableModel tableModel;

    public JobSubmissionFrame(DefaultTableModel tableModel) {

        super("Submit Job");
        this.tableModel = tableModel;

        createFields();
        createButton();
        createPanel();

        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createFields() {

        clientIdField = new JTextField(20);
        jobIdField = new JTextField(20);
        jobDurationField = new JTextField(20);
        jobDeadlineField = new JTextField(20);

        // dropdown so job types are always spelled the same
        String[] jobTypes = {"Language Translation", "Image Generation", "Data Analysis", "Video Rendering"};
        jobTypeBox = new JComboBox<String>(jobTypes);
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

        formPanel.add(new JLabel("Job Type:"));
        formPanel.add(jobTypeBox);

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
            String jobType = (String) jobTypeBox.getSelectedItem();
            String jobDuration = jobDurationField.getText();
            String jobDeadline = jobDeadlineField.getText();

            // admin frame reads these labels back, keep them in sync
            TransactionLog.append(
                    "Client: " + clientId +
                    ", Job ID: " + jobId +
                    ", Job Type: " + jobType +
                    ", Job Duration: " + jobDuration +
                    ", Job Deadline: " + jobDeadline
            );

            tableModel.addRow(new String[]{clientId, jobId, jobType, jobDuration + " hours", jobDeadline, "Pending"});

            JobSubmissionFrame.this.dispose();
        }
    }
}

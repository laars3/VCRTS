
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.Job;
import entity.User;
import storage.JobStore;
import storage.TransactionLog;

public class ClientFrame extends JFrame {

    private final User currentUser;
    private final JobStore jobStore;

    private DefaultTableModel tableModel;
    private JButton submitJobButton;
    private JButton backButton;
    private JobSubmissionFrame jobSubmissionFrame;

    // Constructor only initializes and calls helper methods.
    public ClientFrame(User user, JobStore store) {
        super("Job Owner View");

        currentUser = user;
        jobStore = store;

        createTable();
        createButtons();
        createPanel();
        refreshTable();
        setupFrame();
    }

    // Helper method to create the jobs table.
    private void createTable() {
        String[] columns = {
            "Client ID", "Job ID", "Job Type",
            "Duration", "Deadline", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Helper method to create buttons.
    private void createButtons() {
        submitJobButton = new JButton("Submit Job");
        submitJobButton.addActionListener(new AddJobListener());

        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());
    }

    // Helper method for GUI layout.
    private void createPanel() {
        JLabel title = new JLabel("Submitted Jobs");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel intro = new JLabel(
            "Jobs submitted to the vehicular cloud."
        );

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.add(title);
        header.add(intro);

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        JPanel buttons = new JPanel();

        if ("JOB_OWNER".equals(currentUser.getRole())) {
            buttons.add(submitJobButton);
        }

        buttons.add(backButton);

        JPanel main = new JPanel(new BorderLayout(10, 10));

        main.setBorder(
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        );

        main.add(header, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);

        add(main);
    }

    // Helper method to configure the window.
    private void setupFrame() {
        setSize(1280, 720);
        setLocationRelativeTo(null);
    }

    // Helper method to open job submission.
    private void openJobSubmissionFrame() {
        if (jobSubmissionFrame == null
                || !jobSubmissionFrame.isDisplayable()) {

            jobSubmissionFrame = new JobSubmissionFrame(
                currentUser, jobStore, this
            );
        }

        jobSubmissionFrame.setVisible(true);
        jobSubmissionFrame.toFront();
    }

    // Regular submit-job button listener.
    class AddJobListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            openJobSubmissionFrame();
        }
    }

    // Regular back button listener.
    class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    // Helper method to display saved Job instances.
    public void refreshTable() {
        tableModel.setRowCount(0);

        for (Job job : jobStore.getVisibleJobs(currentUser)) {

            tableModel.addRow(new Object[] {
                job.getOwnerId(),
                job.getId(),
                job.getType(),
                job.getDuration() + " hours",
                job.getDeadline(),
                job.getStatus()
            });
        }
    }
}


// Job submission window.
class JobSubmissionFrame extends JFrame {

    private final User currentUser;
    private final JobStore jobStore;
    private final ClientFrame clientFrame;

    private JTextField clientIdField;
    private JTextField jobIdField;
    private JComboBox<String> jobTypeBox;
    private JTextField durationField;
    private JTextField deadlineField;

    private JButton submitButton;
    private JButton backButton;

    public JobSubmissionFrame(User user,
            JobStore store, ClientFrame frame) {

        super("Submit Job");

        currentUser = user;
        jobStore = store;
        clientFrame = frame;

        createTextFields();
        createButtons();
        createPanel();
        setupFrame();
    }

    // Helper method to initialize form inputs.
    private void createTextFields() {
        clientIdField = new JTextField(20);
        clientIdField.setText(currentUser.getUsername());
        clientIdField.setEditable(false);

        jobIdField = new JTextField(20);
        durationField = new JTextField(20);
        deadlineField = new JTextField(20);

        String[] jobTypes = {
            "Language Translation",
            "Image Generation",
            "Data Analysis",
            "Video Rendering"
        };

        jobTypeBox = new JComboBox<String>(jobTypes);
    }

    // Helper method to create buttons.
    private void createButtons() {
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitListener());

        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());
    }

    // Helper method for GUI layout.
    private void createPanel() {
        JPanel fields = new JPanel(new GridLayout(0, 2, 5, 5));

        fields.add(new JLabel("Client ID:"));
        fields.add(clientIdField);

        fields.add(new JLabel("Job ID:"));
        fields.add(jobIdField);

        fields.add(new JLabel("Job Type:"));
        fields.add(jobTypeBox);

        fields.add(new JLabel("Job Duration (hours):"));
        fields.add(durationField);

        fields.add(new JLabel("Job Deadline (MM/dd/yyyy):"));
        fields.add(deadlineField);

        JPanel buttons = new JPanel();
        buttons.add(submitButton);
        buttons.add(backButton);

        JPanel main = new JPanel(new BorderLayout(10, 10));

        main.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        main.add(fields, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);

        add(main);
    }

    // Helper method to configure the window.
    private void setupFrame() {
        pack();
        setLocationRelativeTo(clientFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Regular submit listener.
    class SubmitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            submitJob();
        }
    }

    // Regular back listener.
    class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            dispose();
        }
    }

    // Helper method to create and store a Job instance.
    private void submitJob() {

        String id = jobIdField.getText().trim();
        String type = (String) jobTypeBox.getSelectedItem();
        String duration = durationField.getText().trim();
        String deadline = deadlineField.getText().trim();

        boolean submitted = jobStore.submitJob(
            currentUser,
            id,
            type,
            duration,
            deadline
        );

        if (!submitted) {
            showError(
                "Invalid job information or duplicate Job ID."
            );
            return;
        }

        Job job = jobStore.getJob(currentUser, id);

        saveTransaction(job);
        clientFrame.refreshTable();

        dispose();
    }

    // Helper method to record successful job submission.
    private void saveTransaction(Job job) {
        TransactionLog.append(
            "Client: " + job.getOwnerId()
            + ", Job ID: " + job.getId()
            + ", Job Type: " + job.getType()
            + ", Job Duration: " + job.getDuration()
            + ", Job Deadline: " + job.getDeadline()
        );
    }

    // Helper method to display errors.
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this, message, "Invalid Job",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

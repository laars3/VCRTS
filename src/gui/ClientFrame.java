
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.Job;
import entity.User;
import storage.JobStore;
import storage.TransactionLog;
import validation.InputValidator;

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
            "Duration", "Deadline", "Priority", "Status"
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
                "Jobs submitted to the vehicular cloud.");

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
                BorderFactory.createEmptyBorder(16, 20, 16, 20));

        main.add(header, BorderLayout.NORTH);

        main.add(
                new JScrollPane(table), BorderLayout.CENTER);

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
                    currentUser, jobStore, this);
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
                job.getPriority(),
                job.getStatus()
            });
        }
    }
}


// Job submission window.
class JobSubmissionFrame extends JFrame {

    // Input rules for the job form.
    private static final int JOB_ID_MIN = 3;
    private static final int JOB_ID_MAX = 8;

    private static final String JOB_ID_PATTERN =
            "[A-Za-z0-9_-]+";

    private static final String NUMBER_PATTERN =
            "(0|[1-9][0-9]*)(\\.[0-9]+)?";

    private static final double DURATION_MAX = 168;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final User currentUser;
    private final JobStore jobStore;
    private final ClientFrame clientFrame;

    private JTextField clientIdField;
    private JTextField jobIdField;
    private JComboBox<String> jobTypeBox;
    private JTextField durationField;
    private JTextField deadlineField;
    private JComboBox<Integer> priorityBox;

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

        // 1 runs first, 10 runs last
        Integer[] priorities = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10
        };

        priorityBox = new JComboBox<Integer>(priorities);
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

        fields.add(
                new JLabel("Job Deadline (MM/dd/yyyy):"));

        fields.add(deadlineField);

        fields.add(new JLabel(
                "Job Priority (1 = highest):"));

        fields.add(priorityBox);

        JPanel buttons = new JPanel();

        buttons.add(submitButton);
        buttons.add(backButton);

        JPanel main = new JPanel(new BorderLayout(10, 10));

        main.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

    // Returns the first problem found in the form, or null if all is fine.
    static String validationError(
            String id, String duration, String deadline) {

        if (id.length() < JOB_ID_MIN
                || id.length() > JOB_ID_MAX) {

            return "Job ID must be " + JOB_ID_MIN
                    + "-" + JOB_ID_MAX + " characters.";
        }

        if (!id.matches(JOB_ID_PATTERN)) {

            return "Job ID can only use letters, numbers, - and _.";
        }

        if (!duration.matches(NUMBER_PATTERN)) {

            return "Duration must be a plain number like 5 or 2.5.";
        }

        // UPDATED: Handle invalid duration values without crashing.
        double hours;

        try {

            hours = Double.parseDouble(duration);

        } catch (NumberFormatException error) {

            return "Duration must be a valid number.";
        }

        if (hours <= 0
                || hours > DURATION_MAX
                || Double.isInfinite(hours)) {

            return "Duration must be more than 0 and at most "
                    + (int) DURATION_MAX + " hours.";
        }

        if (!InputValidator.validDeadline(deadline)) {

            return "Deadline must be a real date in MM/dd/yyyy format.";
        }

        if (LocalDate.parse(deadline, DATE_FORMAT)
                .isBefore(LocalDate.now())) {

            return "Deadline can't be in the past.";
        }

        return null;
    }

    // Helper method to create and store a Job instance.
    // UPDATED: Send the job request on a separate thread so the GUI remains responsive.
    private void submitJob() {

        final String id = jobIdField.getText().trim();

        final String type =
                (String) jobTypeBox.getSelectedItem();

        final String duration =
                durationField.getText().trim();

        final String deadline =
                deadlineField.getText().trim();

        final int priority =
                (Integer) priorityBox.getSelectedItem();

        String error = validationError(id, duration, deadline);

        if (error != null) {

            showError(error);

            return;
        }

        // TODO (Anthony - Phase 3): Delete the local jobStore.submitJob() block below once Roni's Client is ready.
        // UPDATED: The local save has already been moved to admin approval.

        // 1. Format the job variables above into a single CSV string (e.g., "JOB,clientId,jobId,type,duration,deadline,priority") for the network
        final String requestData = String.join(",",
                "JOB",
                currentUser.getUsername(),
                id,
                type,
                duration,
                deadline,
                String.valueOf(priority)
        );

        // ADDED: Prevent duplicate submissions while waiting for approval.
        setWaiting(true);

        // ADDED: Wait for the server on a background worker.
        SwingWorker<String, Void> worker =
                new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() {

                // 2. Calls network.Client.sendRequestToServer(formattedString) to send to the Server via Roni's Socket Client & wait for the response
                // Network communication runs off the Swing event thread.
                return network.Client.sendRequestToServer(
                        requestData);
            }

            @Override
            protected void done() {

                // Update the GUI safely after receiving the final decision.
                setWaiting(false);

                try {

                    showDecision(get());

                } catch (Exception error) {

                    showError(
                            "Could not receive the server response: "
                            + error.getMessage());
                }
            }
        };

        // Starts the background worker.
        worker.execute();
    }

    // ADDED: Controls the form while awaiting a decision.
    private void setWaiting(boolean waiting) {

        submitButton.setEnabled(!waiting);

        backButton.setEnabled(!waiting);

        submitButton.setText(
                waiting ? "Waiting for Admin..." : "Submit");

        setDefaultCloseOperation(
                waiting ? JFrame.DO_NOTHING_ON_CLOSE
                        : JFrame.DISPOSE_ON_CLOSE);
    }

    // 3. Read the final "Accepted" or "Rejected" response from the server and show a JOptionPane popup.
    // ADDED: Shows the final decision and refreshes accepted records.
    private void showDecision(String response) {

        if ("Accepted".equals(response)) {

            // note: now that the server handles saving to the CSV, we don't have to call TransactionLog here anymore
            clientFrame.refreshTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Job accepted!",
                    "Approved",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } else if ("Rejected".equals(response)) {

            showError("The admin rejected this job submission.");

        } else {

            showError(
                    "No final admin decision received. Server response: "
                    + response);
        }
    }

    // Helper method to record successful job submission.
    // Retained for reference; approved jobs are now logged in AdminFrame.
    private void saveTransaction(Job job) {

        TransactionLog.append(
            "Client: " + job.getOwnerId()
            + ", Job ID: " + job.getId()
            + ", Job Type: " + job.getType()
            + ", Job Duration: " + job.getDuration()
            + ", Job Deadline: " + job.getDeadline()
            + ", Job Priority: " + job.getPriority()
        );
    }

    // Helper method to display errors.
    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid Job",
                JOptionPane.ERROR_MESSAGE);
    }
}

package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.Job;
import entity.User;
import storage.JobStore;
import storage.TransactionLog;

public class ClientFrame extends JFrame {
    private final User currentUser;
    private final JobStore jobStore;
    private final DefaultTableModel tableModel;

    public ClientFrame(User currentUser, JobStore jobStore) {
        super("Job Owner View");
        this.currentUser = currentUser;
        this.jobStore = jobStore;

        String[] columns = {
            "Client ID", "Job ID", "Job Type",
            "Duration", "Deadline", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        refreshTable();

        JLabel title = new JLabel("Submitted Jobs");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel intro = new JLabel(
                "Jobs submitted to the vehicular cloud.");

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setBorder(
                BorderFactory.createEmptyBorder(16, 20, 8, 20));
        header.add(title);
        header.add(intro);

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        JPanel actions = new JPanel();

        if ("JOB_OWNER".equals(currentUser.getRole())) {
            JButton submit = new JButton("Submit Job");

            submit.addActionListener(e ->
                    new JobSubmissionFrame(
                            currentUser, jobStore, this)
                            .setVisible(true));

            actions.add(submit);
        }

        JButton back = new JButton("Back");
        back.addActionListener(e -> setVisible(false));
        actions.add(back);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.add(header, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(actions, BorderLayout.SOUTH);

        add(main);
        setSize(1280, 720);
        setLocationRelativeTo(null);
    }

    // Refreshes the table using actual Job instances.
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

// Job submission form creates actual Job instances.
class JobSubmissionFrame extends JFrame {
    private final User currentUser;
    private final JobStore jobStore;
    private final ClientFrame clientFrame;

    private final JTextField clientId = new JTextField(20);
    private final JTextField jobId = new JTextField(20);

    private final JComboBox<String> jobType =
            new JComboBox<String>(new String[] {
                "Language Translation",
                "Image Generation",
                "Data Analysis",
                "Video Rendering"
            });

    private final JTextField duration = new JTextField(20);
    private final JTextField deadline = new JTextField(20);

    JobSubmissionFrame(User currentUser,
            JobStore jobStore, ClientFrame clientFrame) {

        super("Submit Job");
        this.currentUser = currentUser;
        this.jobStore = jobStore;
        this.clientFrame = clientFrame;

        // Get the client identity from the logged-in user.
        clientId.setText(currentUser.getUsername());
        clientId.setEditable(false);

        JPanel fields = new JPanel(new GridLayout(0, 2, 5, 5));

        fields.add(new JLabel("Client ID:"));
        fields.add(clientId);

        fields.add(new JLabel("Job ID:"));
        fields.add(jobId);

        fields.add(new JLabel("Job Type:"));
        fields.add(jobType);

        fields.add(new JLabel("Job Duration (hours):"));
        fields.add(duration);

        fields.add(new JLabel("Job Deadline (MM/dd/yyyy):"));
        fields.add(deadline);

        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> submitJob());

        JButton back = new JButton("Back");
        back.addActionListener(e -> dispose());

        JPanel actions = new JPanel();
        actions.add(submit);
        actions.add(back);

        JPanel main = new JPanel(new BorderLayout(8, 10));
        main.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        main.add(fields, BorderLayout.CENTER);
        main.add(actions, BorderLayout.SOUTH);

        add(main);
        pack();
        setLocationRelativeTo(clientFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void submitJob() {
        String id = jobId.getText().trim();
        String type = (String) jobType.getSelectedItem();
        String hours = duration.getText().trim();
        String date = deadline.getText().trim();

        // JobStore validates and creates a Job instance.
        if (!jobStore.submitJob(
                currentUser, id, type, hours, date)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Job not submitted. Check the ID, positive duration, date (MM/dd/yyyy) or permissions.",
                    "Invalid Job",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve the newly saved Job instance.
        Job job = jobStore.getJob(currentUser, id);

        // Record the successfully submitted job.
        TransactionLog.append(
                "Client: " + job.getOwnerId()
                + ", Job ID: " + job.getId()
                + ", Job Type: " + job.getType()
                + ", Job Duration: " + job.getDuration()
                + ", Job Deadline: " + job.getDeadline());

        clientFrame.refreshTable();
        dispose();
    }
}

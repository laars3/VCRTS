package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import entity.Job;
import entity.VCController;
import storage.JobStore;
import storage.TransactionLog;

// Portal for VCC admins, shows every client job and owner vehicle and opens both views.
public class AdminFrame extends JFrame{
    private static final int FRAME_WIDTH = 960;
    private static final int FRAME_HEIGHT = 540;

    // log keys in the order they show up as columns
    private static final String[] JOB_KEYS = {"Time", "Client", "Job ID", "Job Type", "Job Duration", "Job Deadline", "Job Priority"};
    private static final String[] SCHEDULE_COLUMNS = {"Run Order", "Job ID", "Client", "Priority", "Duration", "Waiting Time", "Completion Time"};
    private static final String[] VEHICLE_KEYS = {"Time", "Owner", "Vehicle Manufacturer", "Vehicle Model", "Vehicle Year", "Vehicle Computation Power", "Vehicle Residency"};

    private JButton ownerViewButton;
    private JButton clientViewButton;
    private JButton refreshButton;
    private JButton computeButton;
    private JButton backButton; // 10/25/2026 - anthony - Declares a back button variable

    private DefaultTableModel jobTableModel;
    private DefaultTableModel vehicleTableModel;
    private DefaultTableModel scheduleTableModel;
    private JLabel averageLabel;
    private JTabbedPane tabs;

    // start frame keeps the owner and client frames, so admin sees the same ones users do
    private final StartFrame startFrame;
    private final VCController vcc;
    private final JobStore jobStore;

    public AdminFrame(StartFrame startFrame, VCController vcc, JobStore jobStore){

        super("VCC Admin");
        this.startFrame = startFrame;
        this.vcc = vcc;
        this.jobStore = jobStore;
        createButtons();
        createTables();
        createPanel();
        loadLog();

        // picks up anything submitted in the other views while this was in the back
        addWindowListener(new WindowAdapter(){
            public void windowActivated(WindowEvent event){
                loadLog();
            }
        });

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
    }

    private void createButtons(){
        ownerViewButton = new JButton("Open Owner View");
        ownerViewButton.addActionListener(new OwnerViewListener());

        clientViewButton = new JButton("Open Client View");
        clientViewButton.addActionListener(new ClientViewListener());

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new RefreshListener());

        computeButton = new JButton("Compute Completion Time");
        computeButton.addActionListener(new ComputeListener());

        // 10/25/2026, Anthony - creates and wire back button
        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());
    }

    class OwnerViewListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            startFrame.openOwnerFrame();
        }
    }

    class ClientViewListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            startFrame.openClientFrame();
        }
    }

    class RefreshListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            loadLog();
        }
    }

    // vcc schedules every submitted job, the table shows them in run order
    class ComputeListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            List<Job> jobs = jobStore.getAllJobs(vcc);
            if (jobs.isEmpty()){
                JOptionPane.showMessageDialog(AdminFrame.this, "No jobs have been submitted yet");
                return;
            }

            double average = vcc.computeCompletionTimes(jobs);

            scheduleTableModel.setRowCount(0);
            int run = 1;
            for (Job job : vcc.getActiveJobs()){
                scheduleTableModel.addRow(new Object[]{
                        run,
                        job.getId(),
                        job.getOwnerId(),
                        job.getPriority(),
                        job.getDuration() + " hours",
                        job.getWaitTime() + " hours",
                        job.getScheduledCompletion() + " hours"
                });
                run++;
            }

            averageLabel.setText("Average waiting time: " + String.format("%.2f", average) + " hours");
            tabs.setSelectedIndex(2);
        }
    }

    //10/25/2026 - anthony
    class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false); // hides the current admin window w/out deleting it
            startFrame.setVisible(true); // makes the login screen (StartFrame) visible again
            startFrame.toFront(); // forces the login screen to jump to the front of user's desktop
        }
    }

    private void createTables(){
        // cant edit cells in the ui, rows only come from the log
        jobTableModel = new DefaultTableModel(JOB_KEYS, 0){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        vehicleTableModel = new DefaultTableModel(VEHICLE_KEYS, 0){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        scheduleTableModel = new DefaultTableModel(SCHEDULE_COLUMNS, 0){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
    }

    // Rebuilds both tables from the transaction log.
    private void loadLog(){
        jobTableModel.setRowCount(0);
        vehicleTableModel.setRowCount(0);

        for (String line : TransactionLog.readAll()){
            if (line.contains(", Client: ")){
                jobTableModel.addRow(parseLine(line, JOB_KEYS));
            }
            else if (line.contains(", Owner: ")){
                vehicleTableModel.addRow(parseLine(line, VEHICLE_KEYS));
            }
        }
    }

    // Splits a "Key: value, Key: value" log line into one value per key.
    private String[] parseLine(String line, String[] keys){
        String[] row = new String[keys.length];
        for (int i = 0; i < keys.length; i++){
            row[i] = "";
        }

        for (String part : line.split(", ")){
            int colon = part.indexOf(":");
            if (colon == -1){
                continue;
            }
            String key = part.substring(0, colon).trim();
            String value = part.substring(colon + 1).trim();

            for (int i = 0; i < keys.length; i++){
                if (keys[i].equals(key)){
                    row[i] = value;
                }
            }
        }
        return row;
    }

    private JScrollPane createTableScroll(DefaultTableModel model){
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(table);
    }

    private void createPanel(){
        JLabel title = new JLabel("VCC Admin Portal");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel intro = new JLabel("Every job submitted by clients and every vehicle registered by owners.");
        intro.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel introPanel = new JPanel(new BorderLayout(0, 4));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(computeButton);
        buttonPanel.add(ownerViewButton);
        buttonPanel.add(clientViewButton);
        buttonPanel.add(backButton); // 10/25/2026 - anthony - add back button to the panel

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(introPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        averageLabel = new JLabel("Average waiting time: press Compute Completion Time");
        averageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel schedulePanel = new JPanel(new BorderLayout(0, 8));
        schedulePanel.add(createTableScroll(scheduleTableModel), BorderLayout.CENTER);
        schedulePanel.add(averageLabel, BorderLayout.SOUTH);

        tabs = new JTabbedPane();
        tabs.addTab("Client Jobs", createTableScroll(jobTableModel));
        tabs.addTab("Owner Vehicles", createTableScroll(vehicleTableModel));
        tabs.addTab("Schedule", schedulePanel);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        add(mainPanel);
    }
}

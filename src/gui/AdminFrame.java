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
import network.Server;
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
    private JButton acceptButton;
    private JButton rejectButton;
    private DefaultTableModel requestTableModel;
    private JTable requestTable;

    private DefaultTableModel jobTableModel;
    private DefaultTableModel vehicleTableModel;
    private DefaultTableModel scheduleTableModel;
    private JLabel averageLabel;
    private JTabbedPane tabs;

    // start frame keeps the owner and client frames, so admin sees the same ones users do
    private final StartFrame startFrame;
    private final VCController vcc;
    private final JobStore jobStore;
    // holds the requests waiting for accept or reject
    private final Server server;

    public AdminFrame(StartFrame startFrame, VCController vcc, JobStore jobStore, Server server){

        super("VCC Admin");
        this.startFrame = startFrame;
        this.vcc = vcc;
        this.jobStore = jobStore;
        this.server = server;
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

        computeButton = new JButton("Get CompletionTime");
        computeButton.addActionListener(new ComputeListener());

        // milestone 2 feedback - Anthony - creates and wire back button
        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());

        acceptButton = new JButton("Accept");
        acceptButton.addActionListener(new AcceptRequestListener());
        rejectButton = new JButton("Reject");
        rejectButton.addActionListener(new RejectRequestListener());
    }

    // TODO (Ryan - Phase 2): Implement the Accept listener for Niyo's button
    class AcceptRequestListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // 1. Get the selected row from the incoming requests table.
            // 2. Extract the data and route it to jobStore.submitJob() or vehicleStore.registerVehicle().
            // 3. Trigger the Server to send the "Accepted" notification back to the Client.
        }
    }

    // TODO (Ryan - Phase 2): Implement the Reject listener for Niyo's button
    class RejectRequestListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // 1. Discard the selected request so it doesn't save to the CSV.
            // 2. Trigger the Server to send the "Rejected" notification back to the Client.
        }
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

        requestTableModel = new DefaultTableModel(new String[]{"Type", "Sender ID", "Request ID", "Details"}, 0){
            public boolean isCellEditable(int row, int column){ return false; }
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

    // table in the middle, the tab's own buttons underneath
    private JPanel createTab(JComponent table, JButton... buttons){
        JPanel tabButtons = new JPanel();
        for (JButton button : buttons){
            tabButtons.add(button);
        }

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(table, BorderLayout.CENTER);
        panel.add(tabButtons, BorderLayout.SOUTH);
        return panel;
    }

    private void createPanel(){
        JLabel title = new JLabel("VCC Admin Portal");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel intro = new JLabel("Every job submitted by clients and every vehicle registered by owners.");
        intro.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel introPanel = new JPanel(new BorderLayout(0, 4));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);

        // refresh and back work on every tab, so they stay under the tabs
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton); // 10/25/2026 - anthony - add back button to the panel

        // each tab only shows the buttons that belong to it
        JPanel jobsPanel = createTab(createTableScroll(jobTableModel), clientViewButton);
        JPanel vehiclesPanel = createTab(createTableScroll(vehicleTableModel), ownerViewButton);

        averageLabel = new JLabel("Average waiting time: press Get CompletionTime");
        averageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel schedulePanel = createTab(createTableScroll(scheduleTableModel), computeButton);
        schedulePanel.add(averageLabel, BorderLayout.NORTH);

        requestTable = new JTable(requestTableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel requestsPanel = createTab(new JScrollPane(requestTable), acceptButton, rejectButton);

        tabs = new JTabbedPane();
        tabs.addTab("Client Jobs", jobsPanel);
        tabs.addTab("Owner Vehicles", vehiclesPanel);
        tabs.addTab("Schedule", schedulePanel);
        tabs.addTab("Pending Requests", requestsPanel);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}

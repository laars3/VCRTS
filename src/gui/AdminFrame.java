
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.Job;

import entity.JobOwner;
import entity.User;
import entity.VCController;

import network.ClientHandler;
import network.Server;
import storage.JobStore;
import storage.TransactionLog;
import storage.UserStore;
import storage.VehicleStore;

// Portal for VCC admins, shows every client job and owner vehicle and opens both views.
public class AdminFrame extends JFrame {

    private static final int FRAME_WIDTH = 960;
    private static final int FRAME_HEIGHT = 540;

    // log keys in the order they show up as columns
    private static final String[] JOB_KEYS = {
        "Time", "Client", "Job ID", "Job Type", "Job Duration",
        "Job Deadline", "Job Priority"
    };

    private static final String[] SCHEDULE_COLUMNS = {
        "Run Order", "Job ID", "Client", "Priority", "Duration",
        "Waiting Time", "Completion Time"
    };

    private static final String[] VEHICLE_KEYS = {
        "Time", "Owner", "Vehicle Manufacturer", "Vehicle Model",
        "Vehicle Year", "Vehicle Computation Power", "Vehicle Residency"
    };

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

    // checks the server for new requests
    private Timer pendingRefreshTimer;

    // back goes to the login window
    private final StartFrame startFrame;
    private final VCController vcc;

    // the admin's own read-only views
    private OwnerFrame ownerFrame;
    private ClientFrame clientFrame;

    // accept looks up who sent the request and saves through these
    private final UserStore userStore;
    private final VehicleStore vehicleStore;
    private final JobStore jobStore;

    // holds the requests waiting for accept or reject
    private final Server server;

    // same order as the rows in the pending table
    private List<ClientHandler> displayedRequests =
            new ArrayList<ClientHandler>();

    public AdminFrame(StartFrame startFrame, VCController vcc,
            UserStore userStore, VehicleStore vehicleStore,
            JobStore jobStore, Server server) {

        super("VCC Admin");

        this.startFrame = startFrame;
        this.vcc = vcc;
        this.userStore = userStore;
        this.vehicleStore = vehicleStore;
        this.jobStore = jobStore;
        this.server = server;

        createButtons();
        createTables();
        createPanel();
        loadLog();

        setupFrame();
    }

    private void createButtons() {
        ownerViewButton = new JButton("Open Owner View");
        ownerViewButton.addActionListener(new OwnerViewListener());

        clientViewButton = new JButton("Open Client View");
        clientViewButton.addActionListener(new ClientViewListener());

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new RefreshListener());

        computeButton = new JButton("Schedule Jobs");
        computeButton.addActionListener(new ComputeListener());

        // milestone 2 feedback - Anthony - creates and wire back button
        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());

        acceptButton = new JButton("Accept");
        acceptButton.addActionListener(new AcceptRequestListener());

        rejectButton = new JButton("Reject");
        rejectButton.addActionListener(new RejectRequestListener());
    }

    class AcceptRequestListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            handleRequestDecision(true);
        }
    }

    class RejectRequestListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            handleRequestDecision(false);
        }
    }

    // saves the selected request if accepted, then tells the client
    private void handleRequestDecision(boolean accepting) {

        ClientHandler request = getSelectedRequest();

        if (request == null) {
            return;
        }

        if (!server.getPending().contains(request)) {

            JOptionPane.showMessageDialog(
                    this, "This request has already been handled.");

            loadLog();
            return;
        }

        String status;

        if (accepting) {

            // save first so the client is never told accepted for something that wasn't saved
            boolean saved = saveAcceptedRequest(request);

            if (saved) {
                server.accept(request);
                status = "Accepted";

            } else {

                server.reject(request);
                status = "Rejected - Invalid request or unable to save";
            }

        } else {

            server.reject(request);
            status = "Rejected";
        }

        loadLog();

        JOptionPane.showMessageDialog(
                this, "Request " + request.getId() + ": " + status);
    }

    // the request behind the selected row, null if nothing is selected
    private ClientHandler getSelectedRequest() {

        int selectedRow = requestTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a pending request first.");

            return null;
        }

        int modelRow =
                requestTable.convertRowIndexToModel(selectedRow);

        if (modelRow < 0 || modelRow >= displayedRequests.size()) {

            JOptionPane.showMessageDialog(
                    this, "Unable to find the selected request.");

            return null;
        }

        return displayedRequests.get(modelRow);
    }

    // sends the request to the job or vehicle store
    private boolean saveAcceptedRequest(ClientHandler request) {

        if (request == null || request.getLine() == null) {
            return false;
        }

        String[] data = request.getLine().split(",", -1);

        if (data.length < 3) {
            return false;
        }

        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].trim();
        }

        String type = data[0];
        String username = data[1];

        User user = userStore.findUser(username);

        // the stores only save for an owner who is logged in
        if (user == null || !userStore.isAuthenticated(user)) {
            return false;
        }

        try {

            if ("VEHICLE".equals(type)) {
                return saveVehicleRequest(user, data);
            }

            if ("JOB".equals(type)) {
                return saveJobRequest(user, data);
            }

        } catch (RuntimeException error) {

            System.err.println(
                    "Could not process request: "
                    + error.getMessage());
        }

        return false;
    }

    private boolean saveVehicleRequest(User user, String[] data) {

        // VEHICLE,username,id,make,model,year,power,residency
        if (data.length != 8
                || !"VEHICLE_OWNER".equals(user.getRole())) {
            return false;
        }

        String id = data[2];
        String make = data[3];
        String model = data[4];
        String year = data[5];
        String power = data[6];
        String residency = data[7];

        boolean saved = vehicleStore.registerVehicle(
                user, id, make, model, year, power, residency);

        if (saved) {

            TransactionLog.append(
                    "Owner: " + user.getUsername()
                    + ", Vehicle ID: " + id
                    + ", Vehicle Manufacturer: " + make
                    + ", Vehicle Model: " + model
                    + ", Vehicle Year: " + year
                    + ", Vehicle Computation Power: " + power
                    + ", Vehicle Residency: " + residency);
        }

        return saved;
    }

    private boolean saveJobRequest(User user, String[] data) {

        // JOB,username,id,type,duration,deadline,priority
        if (data.length != 7 || !(user instanceof JobOwner)) {
            return false;
        }

        int priority;

        try {
            priority = Integer.parseInt(data[6]);

        } catch (NumberFormatException error) {
            return false;
        }

        if (priority < 1 || priority > 10) {
            return false;
        }

        String id = data[2];
        String type = data[3];
        String duration = data[4];
        String deadline = data[5];

        boolean saved = jobStore.submitJob(
                user, id, type, duration, deadline, priority);

        if (saved) {

            JobOwner owner = (JobOwner) user;

            TransactionLog.append(
                    "Client: " + owner.getClientId()
                    + ", Job ID: " + id
                    + ", Job Type: " + type
                    + ", Job Duration: " + duration
                    + ", Job Deadline: " + deadline
                    + ", Job Priority: " + priority);
        }

        return saved;
    }

    private void refreshPendingRequests() {

        List<ClientHandler> latest = server.getPending();

        // only rebuild when something changed, so the selected row isn't lost every tick
        if (latest.equals(displayedRequests)) {
            return;
        }

        displayedRequests = latest;

        requestTableModel.setRowCount(0);

        for (ClientHandler request : displayedRequests) {

            requestTableModel.addRow(new Object[] {
                    request.getType(),
                    request.getSender(),
                    request.getId(),
                    getRequestDetails(request)
            });
        }
    }

    // everything after the id, for the details column
    private String getRequestDetails(ClientHandler request) {

        String[] fields = request.getLine().split(",", -1);

        StringBuilder details = new StringBuilder();

        for (int i = 3; i < fields.length; i++) {

            if (i > 3) {
                details.append(", ");
            }

            details.append(fields[i]);
        }

        return details.toString();
    }

    class OwnerViewListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (ownerFrame == null || !ownerFrame.isDisplayable()) {
                ownerFrame = new OwnerFrame(vcc, vehicleStore);
            }

            ownerFrame.refreshTable();
            ownerFrame.setVisible(true);
            ownerFrame.toFront();
        }
    }

    class ClientViewListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (clientFrame == null || !clientFrame.isDisplayable()) {
                clientFrame = new ClientFrame(vcc, jobStore);
            }

            clientFrame.refreshTable();
            clientFrame.setVisible(true);
            clientFrame.toFront();
        }
    }

    class RefreshListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            loadLog();
        }
    }

    // vcc schedules every submitted job, the table shows them in run order
    class ComputeListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            List<Job> jobs = jobStore.getAllJobs(vcc);

            if (jobs.isEmpty()) {

                JOptionPane.showMessageDialog(
                        AdminFrame.this,
                        "No jobs have been submitted yet");

                return;
            }

            double average = vcc.computeCompletionTimes(jobs);

            scheduleTableModel.setRowCount(0);

            int run = 1;

            for (Job job : vcc.getActiveJobs()) {

                scheduleTableModel.addRow(new Object[] {
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

            averageLabel.setText(
                    "Average waiting time: "
                    + String.format("%.2f", average)
                    + " hours");

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

    private void createTables() {

        // cant edit cells in the ui, rows only come from the log
        jobTableModel = new DefaultTableModel(JOB_KEYS, 0) {

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleTableModel =
                new DefaultTableModel(VEHICLE_KEYS, 0) {

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTableModel =
                new DefaultTableModel(SCHEDULE_COLUMNS, 0) {

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTableModel = new DefaultTableModel(
                new String[] {
                    "Type", "Sender ID", "Request ID", "Details"
                }, 0) {

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Rebuilds both tables from the transaction log.
    private void loadLog() {

        jobTableModel.setRowCount(0);
        vehicleTableModel.setRowCount(0);

        for (String line : TransactionLog.readAll()) {

            if (line.contains(", Client: ")) {

                jobTableModel.addRow(
                        parseLine(line, JOB_KEYS));

            } else if (line.contains(", Owner: ")) {

                vehicleTableModel.addRow(
                        parseLine(line, VEHICLE_KEYS));
            }
        }

        refreshPendingRequests();
    }

    // Splits a "Key: value, Key: value" log line into one value per key.
    private String[] parseLine(String line, String[] keys) {

        String[] row = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            row[i] = "";
        }

        for (String part : line.split(", ")) {

            int colon = part.indexOf(":");

            if (colon == -1) {
                continue;
            }

            String key = part.substring(0, colon).trim();

            String value = part.substring(colon + 1).trim();

            for (int i = 0; i < keys.length; i++) {

                if (keys[i].equals(key)) {
                    row[i] = value;
                }
            }
        }

        return row;
    }

    private JScrollPane createTableScroll(DefaultTableModel model) {

        JTable table = new JTable(model);

        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        return new JScrollPane(table);
    }

    // table in the middle, the tab's own buttons underneath
    private JPanel createTab(JComponent table, JButton... buttons) {

        JPanel tabButtons = new JPanel();

        for (JButton button : buttons) {
            tabButtons.add(button);
        }

        JPanel panel = new JPanel(new BorderLayout(0, 8));

        panel.add(table, BorderLayout.CENTER);
        panel.add(tabButtons, BorderLayout.SOUTH);

        return panel;
    }

    private void createPanel() {

        JLabel title = new JLabel("VCC Admin Portal");

        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel intro = new JLabel(
                "Every job submitted by clients and every vehicle registered by owners.");

        intro.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel introPanel = new JPanel(new BorderLayout(0, 4));

        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);

        // refresh and back work on every tab, so they stay under the tabs
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(refreshButton);

        buttonPanel.add(backButton); // 10/25/2026 - anthony - add back button to the panel

        // each tab only shows the buttons that belong to it
        JPanel jobsPanel = createTab(
                createTableScroll(jobTableModel),
                clientViewButton);

        JPanel vehiclesPanel = createTab(
                createTableScroll(vehicleTableModel),
                ownerViewButton);

        averageLabel = new JLabel(
                "Average waiting time: press Schedule Jobs");

        averageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel schedulePanel = createTab(
                createTableScroll(scheduleTableModel),
                computeButton);

        schedulePanel.add(averageLabel, BorderLayout.NORTH);

        requestTable = new JTable(requestTableModel);

        requestTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        JPanel requestsPanel = createTab(
                new JScrollPane(requestTable),
                acceptButton,
                rejectButton);

        tabs = new JTabbedPane();

        tabs.addTab("Client Jobs", jobsPanel);
        tabs.addTab("Owner Vehicles", vehiclesPanel);
        tabs.addTab("Schedule", schedulePanel);
        tabs.addTab("Pending Requests", requestsPanel);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 12));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(16, 20, 16, 20));

        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // only polls while the pending tab is open
    class PendingRefreshListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            if (isVisible() && tabs.getSelectedIndex() == 3) {
                refreshPendingRequests();
            }
        }
    }

    private void setupFrame() {

        pendingRefreshTimer = new Timer(
                750, new PendingRefreshListener());

        pendingRefreshTimer.start();

        // picks up anything submitted in the other views while this was in the back
        addWindowListener(new WindowAdapter() {

            public void windowActivated(WindowEvent event) {
                loadLog();
            }

            public void windowClosed(WindowEvent event) {
                pendingRefreshTimer.stop();
            }
        });

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
    }
}

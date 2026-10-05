package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import storage.TransactionLog;

// Portal for VCC admins, shows every client job and owner vehicle and opens both views.
public class AdminFrame extends JFrame{
    private static final int FRAME_WIDTH = 960;
    private static final int FRAME_HEIGHT = 540;

    // log keys in the order they show up as columns
    private static final String[] JOB_KEYS = {"Time", "Client", "Job ID", "Job Type", "Job Duration", "Job Deadline"};
    private static final String[] VEHICLE_KEYS = {"Time", "Owner", "Vehicle Manufacturer", "Vehicle Model", "Vehicle Year", "Vehicle Computation Power", "Vehicle Residency"};

    private JButton ownerViewButton;
    private JButton clientViewButton;
    private JButton refreshButton;

    private DefaultTableModel jobTableModel;
    private DefaultTableModel vehicleTableModel;

    // start frame keeps the owner and client frames, so admin sees the same ones users do
    private StartFrame startFrame;

    public AdminFrame(StartFrame startFrame){

        super("VCC Admin");
        this.startFrame = startFrame;
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
        buttonPanel.add(ownerViewButton);
        buttonPanel.add(clientViewButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(introPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Client Jobs", createTableScroll(jobTableModel));
        tabs.addTab("Owner Vehicles", createTableScroll(vehicleTableModel));

        JPanel mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        add(mainPanel);
    }
}

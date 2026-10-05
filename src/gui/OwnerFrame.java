package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import storage.TransactionLog;

public class OwnerFrame extends JFrame {
    private JButton button;
    private RegistrationFrame registrationFrame;
    private DefaultTableModel tableModel;

    public OwnerFrame() {
        super("Owner View");
        setSize(1280, 720);

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Your Vehicles");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel intro = new JLabel("Register a vehicle to rent out its computing power.");
        intro.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel introPanel = new JPanel(new BorderLayout(0, 4));
        introPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);
        add(introPanel, BorderLayout.NORTH);

        // EXAMPLE - "Dummy Data" Dashboard
        String[] columnNames = {"Owner ID", "Manufacturer", "Model", "Year", "Computation Power", "Residency"};
        String[][] dummyData = {
                {"O-101", "Tesla", "Model 3", "2023", "High", "8 hours"},
                {"O-102", "Ford", "Mustang Mach-E", "2022", "Medium", "4 hours"},
                {"O-103", "Chevrolet", "Bolt EV", "2021", "Low", "12 hours"},
                {"O-104", "Rivian", "R1T", "2024", "High", "24 hours"}
        };

        // model lets new rows be added after the table is built
        tableModel = new DefaultTableModel(dummyData, columnNames){
            // read only in the ui, rows still get added through addRow
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        JTable vehicleTable = new JTable(tableModel);
        vehicleTable.setFillsViewportHeight(true);
        vehicleTable.getTableHeader().setReorderingAllowed(false);
        vehicleTable.getTableHeader().setResizingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        add(scrollPane, BorderLayout.CENTER);

        button = new JButton("Register Vehicle");
        button.addActionListener(new AddRegistrationListener());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(button);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    class AddRegistrationListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (registrationFrame == null || !registrationFrame.isDisplayable()) {
                registrationFrame = new RegistrationFrame(tableModel);
            }
            registrationFrame.setVisible(true);
            registrationFrame.toFront();
        }
    }
}

class RegistrationFrame extends JFrame {
    private JTextField ownerIdField;
    private JTextField vehicleManufacturerField;
    private JTextField vehicleModelField;
    private JTextField vehicleYearField;
    private JTextField compPowField;
    private JTextField residencyField;
    private JButton submitButton;

    // the owner view's table, new vehicles get added here
    private DefaultTableModel tableModel;

    public RegistrationFrame(DefaultTableModel tableModel) {
        super("Register Vehicle");
        this.tableModel = tableModel;
        this.createTextFields();
        this.createButton();
        this.createPanel();
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createTextFields() {
        this.ownerIdField = new JTextField(20);
        this.vehicleManufacturerField = new JTextField(20);
        this.vehicleModelField = new JTextField(20);
        this.vehicleYearField = new JTextField(20);
        this.compPowField = new JTextField(20);
        this.residencyField = new JTextField(20);
    }

    private void createButton() {
        this.submitButton = new JButton("Submit");
        this.submitButton.addActionListener(new SubmitListener());
    }

    private void createPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Owner ID:"));
        formPanel.add(this.ownerIdField);
        formPanel.add(new JLabel("Vehicle Manufacturer:"));
        formPanel.add(this.vehicleManufacturerField);
        formPanel.add(new JLabel("Vehicle Model:"));
        formPanel.add(this.vehicleModelField);
        formPanel.add(new JLabel("Vehicle Year:"));
        formPanel.add(this.vehicleYearField);
        formPanel.add(new JLabel("Vehicle Computation Power:"));
        formPanel.add(this.compPowField);
        formPanel.add(new JLabel("Residency (hours):"));
        formPanel.add(this.residencyField);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.submitButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        this.add(mainPanel);
    }

    class SubmitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String ownerId = ownerIdField.getText();
            String vManufacturer = vehicleManufacturerField.getText();
            String vModel = vehicleModelField.getText();
            String vYear = vehicleYearField.getText();
            String vComp = compPowField.getText();
            String residency = residencyField.getText();
            TransactionLog.append("Owner: " + ownerId + ", Vehicle Manufacturer: " + vManufacturer + ", Vehicle Model: " + vModel + ", Vehicle Year: " + vYear + ", Vehicle Computation Power: " + vComp + ", Vehicle Residency: " + residency);
            tableModel.addRow(new String[]{ownerId, vManufacturer, vModel, vYear, vComp, residency + " hours"});
            dispose();
        }
    }
}
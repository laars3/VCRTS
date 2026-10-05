package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import storage.TransactionLog;

public class OwnerFrame extends JFrame{
    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 720;

    private JButton button;
    private RegistrationFrame registrationFrame;
    private DefaultTableModel tableModel;

    public OwnerFrame(){

        super("Owner View");
        registrationButton();
        createTable();
        createPanel();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    class AddRegistrationListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (registrationFrame == null || !registrationFrame.isDisplayable()){
                registrationFrame = new RegistrationFrame(tableModel);
            }
            registrationFrame.setVisible(true);
            registrationFrame.toFront();
        }
    }

    private void registrationButton(){
        button = new JButton("Register Vehicle");

        ActionListener listener = new AddRegistrationListener();
        button.addActionListener(listener);
    }

    private void createTable(){
        String[] columnNames = {"Owner ID", "Manufacturer", "Model", "Year", "Computation Power", "Residency"};

        // dummy rows for now
        String[][] dummyData = {
                {"O-101", "Tesla", "Model 3", "2023", "High", "8 hours"},
                {"O-102", "Ford", "Mustang Mach-E", "2022", "Medium", "4 hours"},
                {"O-103", "Chevrolet", "Bolt EV", "2021", "Low", "12 hours"},
                {"O-104", "Rivian", "R1T", "2024", "High", "24 hours"}
        };

        // cant edit cells in the ui, rows only get added with addRow
        tableModel = new DefaultTableModel(dummyData, columnNames){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
    }

    private void createPanel(){
        JLabel title = new JLabel("Your Vehicles");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel intro = new JLabel("Register a vehicle to rent out its computing power.");
        intro.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel introPanel = new JPanel(new BorderLayout(0, 4));
        introPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);

        JTable vehicleTable = new JTable(tableModel);
        vehicleTable.setFillsViewportHeight(true);
        vehicleTable.getTableHeader().setReorderingAllowed(false);
        vehicleTable.getTableHeader().setResizingAllowed(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(button);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}

class RegistrationFrame extends JFrame{

    private JTextField ownerIdField;
    private JTextField vehicleManufacturerField;
    private JTextField vehicleModelField;
    private JTextField vehicleYearField;
    private JTextField compPowField;
    private JTextField residencyField;

    private JButton submitButton;

    // owner frame's table, new vehicles go here
    private DefaultTableModel tableModel;

    public RegistrationFrame(DefaultTableModel tableModel){
        super("Register Vehicle");
        this.tableModel = tableModel;
        createTextFields();
        createButton();
        createPanel();
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    private void createTextFields(){
        ownerIdField = new JTextField(20);
        vehicleManufacturerField = new JTextField(20);
        vehicleModelField = new JTextField(20);
        vehicleYearField = new JTextField(20);
        compPowField = new JTextField(20);
        residencyField = new JTextField(20);
    }

    private void createButton(){
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitListener());
    }

    private void createPanel(){

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Owner ID:"));
        formPanel.add(ownerIdField);
        formPanel.add(new JLabel("Vehicle Manufacturer:"));
        formPanel.add(vehicleManufacturerField);
        formPanel.add(new JLabel("Vehicle Model:"));
        formPanel.add(vehicleModelField);
        formPanel.add(new JLabel("Vehicle Year:"));
        formPanel.add(vehicleYearField);
        formPanel.add(new JLabel("Vehicle Computation Power:"));
        formPanel.add(compPowField);
        formPanel.add(new JLabel("Residency (hours):"));
        formPanel.add(residencyField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    class SubmitListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            String ownerId = ownerIdField.getText();
            String vManufacturer = vehicleManufacturerField.getText();
            String vModel = vehicleModelField.getText();
            String vYear = vehicleYearField.getText();
            String vComp = compPowField.getText();
            String residency = residencyField.getText();
            TransactionLog.append("Owner: " + ownerId + ", " + "Vehicle Manufacturer: " + vManufacturer + ", " + "Vehicle Model: " + vModel + ", " + "Vehicle Year: " + vYear + ", " + "Vehicle Computation Power: " + vComp + ", " + "Vehicle Residency: " + residency);
            tableModel.addRow(new String[]{ownerId, vManufacturer, vModel, vYear, vComp, residency + " hours"});
            RegistrationFrame.this.dispose();
        }
    }

}

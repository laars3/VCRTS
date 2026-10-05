package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import storage.TransactionLog;

/*
 * Author: Anthony
 * Prev Author: Lars
 * README Task #9: Owner Frame Introductions
 * Problem Addressed: The Owner window lacked structural UI elements and direction. Added an introductory header to explain that this page is for registering vehicles to rent out computation power.
 * Java Components Implemented: BorderLayout (for organizing the screen), JLabel (for the text), Font & SwingConstants (for styling and centering).
 */
public class OwnerFrame extends JFrame {
    private JButton button;
    private RegistrationFrame registrationFrame;

    public OwnerFrame() {
        super("Owner View");
        setSize(1280, 720);

        setLayout(new BorderLayout(10, 10));

        JLabel introLabel = new JLabel("<html><div style='text-align: center;'>Hey Owner! Welcome to our VCRTS app.<br>Here, you can register your vehicle to rent out its computational power.</div></html>", SwingConstants.CENTER);
        introLabel.setFont(new Font("Arial", Font.BOLD, 24));
        introLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(introLabel, BorderLayout.NORTH);

        // EXAMPLE - "Dummy Data" Dashboard
        String[] columnNames = {"Owner ID", "Manufacturer", "Model", "Year", "Computation Power", "Residency"};
        String[][] dummyData = {
                {"O-101", "Tesla", "Model 3", "2023", "High", "8 hours"},
                {"O-102", "Ford", "Mustang Mach-E", "2022", "Medium", "4 hours"},
                {"O-103", "Chevrolet", "Bolt EV", "2021", "Low", "12 hours"},
                {"O-104", "Rivian", "R1T", "2024", "High", "24 hours"}
        };

        JTable vehicleTable = new JTable(dummyData, columnNames);
        vehicleTable.setFillsViewportHeight(true);
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
                registrationFrame = new RegistrationFrame();
            }
            registrationFrame.setVisible(true);
            registrationFrame.toFront();
        }
    }
}

// --- IAN'S ORIGINAL POPUP CODE RESTORED ---
class RegistrationFrame extends JFrame {
    private JTextField ownerIdField;
    private JTextField vehicleManufacturerField;
    private JTextField vehicleModelField;
    private JTextField vehicleYearField;
    private JTextField compPowField;
    private JTextField residencyField;
    private JButton submitButton;

    public RegistrationFrame() {
        super("Register Vehicle");
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
            dispose();
        }
    }
}
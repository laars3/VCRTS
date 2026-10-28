
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.User;
import entity.Vehicle;
import storage.VehicleStore;
import storage.TransactionLog;

public class OwnerFrame extends JFrame {

    private final User currentUser;
    private final VehicleStore vehicleStore;

    private DefaultTableModel tableModel;
    private JButton registrationButton;
    private JButton backButton;
    private RegistrationFrame registrationFrame;

    // Constructor only initializes and calls helper methods.
    public OwnerFrame(User user, VehicleStore store) {
        super("Owner View");

        currentUser = user;
        vehicleStore = store;

        createTable();
        createButtons();
        createPanel();
        refreshTable();
        setupFrame();
    }

    // Helper method to create the vehicle table.
    private void createTable() {
        String[] columns = {
            "Vehicle ID", "Manufacturer", "Model",
            "Year", "Computation Power", "Residency"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Helper method to create buttons.
    private void createButtons() {
        registrationButton = new JButton("Register Vehicle");
        registrationButton.addActionListener(
            new AddRegistrationListener()
        );

        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());
    }

    // Helper method to build the GUI.
    private void createPanel() {
        JLabel title = new JLabel("Available Vehicles");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel intro = new JLabel(
            "Vehicles registered with the vehicular cloud."
        );

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.add(title);
        header.add(intro);

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        JPanel buttonPanel = new JPanel();

        if ("VEHICLE_OWNER".equals(currentUser.getRole())) {
            buttonPanel.add(registrationButton);
        }

        buttonPanel.add(backButton);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        );

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Helper method to configure the window.
    private void setupFrame() {
        setSize(1280, 720);
        setLocationRelativeTo(null);
    }

    // Helper method to open vehicle registration.
    private void openRegistrationFrame() {
        if (registrationFrame == null
                || !registrationFrame.isDisplayable()) {

            registrationFrame = new RegistrationFrame(
                currentUser, vehicleStore, this
            );
        }

        registrationFrame.setVisible(true);
        registrationFrame.toFront();
    }

    // Regular listener for registering a vehicle.
    class AddRegistrationListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            openRegistrationFrame();
        }
    }

    // Regular listener for the back button.
    class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
        }
    }

    // Helper method to display stored Vehicle instances.
    public void refreshTable() {
        tableModel.setRowCount(0);

        for (Vehicle vehicle :
                vehicleStore.getVisibleVehicles(currentUser)) {

            tableModel.addRow(new Object[] {
                vehicle.getVehicleId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getComputationPower(),
                vehicle.getResidency() + " hours"
            });
        }
    }
}


// Registration window.
class RegistrationFrame extends JFrame {

    private final User currentUser;
    private final VehicleStore vehicleStore;
    private final OwnerFrame ownerFrame;

    private JTextField vehicleIdField;
    private JTextField manufacturerField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField powerField;
    private JTextField residencyField;

    private JButton submitButton;
    private JButton backButton;

    public RegistrationFrame(User user,
            VehicleStore store, OwnerFrame frame) {

        super("Register Vehicle");

        currentUser = user;
        vehicleStore = store;
        ownerFrame = frame;

        createTextFields();
        createButtons();
        createPanel();
        setupFrame();
    }

    // Helper method for text fields.
    private void createTextFields() {
        vehicleIdField = new JTextField(20);
        manufacturerField = new JTextField(20);
        modelField = new JTextField(20);
        yearField = new JTextField(20);
        powerField = new JTextField(20);
        residencyField = new JTextField(20);
    }

    // Helper method for buttons.
    private void createButtons() {
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitListener());

        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());
    }

    // Helper method for GUI layout.
    private void createPanel() {
        JPanel fields = new JPanel(new GridLayout(0, 2, 5, 5));

        fields.add(new JLabel("Vehicle ID:"));
        fields.add(vehicleIdField);

        fields.add(new JLabel("Manufacturer:"));
        fields.add(manufacturerField);

        fields.add(new JLabel("Model:"));
        fields.add(modelField);

        fields.add(new JLabel("Year:"));
        fields.add(yearField);

        fields.add(new JLabel("Computation Power:"));
        fields.add(powerField);

        fields.add(new JLabel("Residency (hours):"));
        fields.add(residencyField);

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
        setLocationRelativeTo(ownerFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Regular submit listener.
    class SubmitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            submitRegistration();
        }
    }

    // Regular back listener.
    class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            dispose();
        }
    }

    // Helper method to validate registration inputs.
    private boolean validateFields() {
        String id = vehicleIdField.getText().trim();
        String make = manufacturerField.getText().trim();
        String model = modelField.getText().trim();

        if (!id.matches("[A-Za-z0-9_-]{3,8}")) {
            showError("Vehicle ID must be 3-8 characters.");
            return false;
        }

        if (!make.matches("[A-Za-z0-9 .-]{2,30}")
                || !model.matches("[A-Za-z0-9 .-]{2,30}")) {
            showError("Manufacturer and model must be 2-30 characters.");
            return false;
        }

        try {
            int year = Integer.parseInt(yearField.getText().trim());
            int residency = Integer.parseInt(
                residencyField.getText().trim()
            );

            if (year < 1900
                    || year > java.time.Year.now().getValue() + 1
                    || residency < 1 || residency > 168) {

                showError("Invalid year or residency.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Year and residency must be whole numbers.");
            return false;
        }

        return true;
    }

    // Helper method to create and store the Vehicle instance.
    private void submitRegistration() {

        if (!validateFields()) {
            return;
        }

        String id = vehicleIdField.getText().trim();

        boolean registered = vehicleStore.registerVehicle(
            currentUser,
            id,
            manufacturerField.getText().trim(),
            modelField.getText().trim(),
            yearField.getText().trim(),
            powerField.getText().trim(),
            residencyField.getText().trim()
        );

        if (!registered) {
            showError(
                "Invalid vehicle information or duplicate Vehicle ID."
            );
            return;
        }

        Vehicle vehicle = vehicleStore.getVehicle(currentUser, id);

        saveTransaction(vehicle);
        ownerFrame.refreshTable();

        dispose();
    }

    // Helper method to record a successful registration.
    private void saveTransaction(Vehicle vehicle) {
        TransactionLog.append(
            "Owner: " + currentUser.getUsername()
            + ", Vehicle ID: " + vehicle.getVehicleId()
            + ", Vehicle Manufacturer: " + vehicle.getMake()
            + ", Vehicle Model: " + vehicle.getModel()
            + ", Vehicle Year: " + vehicle.getYear()
            + ", Vehicle Computation Power: "
            + vehicle.getComputationPower()
            + ", Vehicle Residency: " + vehicle.getResidency()
        );
    }

    // Helper method for error messages.
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this, message, "Invalid Vehicle",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

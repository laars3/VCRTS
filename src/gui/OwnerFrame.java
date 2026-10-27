package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.User;
import entity.Vehicle;
import storage.TransactionLog;
import storage.VehicleStore;

public class OwnerFrame extends JFrame {
    private final User currentUser;
    private final VehicleStore vehicleStore;
    private final DefaultTableModel tableModel;

    public OwnerFrame(User currentUser, VehicleStore vehicleStore) {
        super("Owner View");
        this.currentUser = currentUser;
        this.vehicleStore = vehicleStore;

        String[] columns = {
            "Vehicle ID", "Manufacturer", "Model",
            "Year", "Computation Power", "Residency"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        refreshTable();

        JLabel title = new JLabel("Available Vehicles");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel intro = new JLabel(
                "Vehicles registered with the vehicular cloud.");

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setBorder(
                BorderFactory.createEmptyBorder(16, 20, 8, 20));
        header.add(title);
        header.add(intro);

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        JPanel actions = new JPanel();

        if ("VEHICLE_OWNER".equals(currentUser.getRole())) {
            JButton register = new JButton("Register Vehicle");

            register.addActionListener(e ->
                    new RegistrationFrame(
                            currentUser, vehicleStore, this)
                            .setVisible(true));

            actions.add(register);
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

    // Refreshes the table using actual Vehicle instances.
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

// Registration form creates real Vehicle instances.
class RegistrationFrame extends JFrame {
    private final User currentUser;
    private final VehicleStore vehicleStore;
    private final OwnerFrame ownerFrame;

    private final JTextField vehicleId = new JTextField(20);
    private final JTextField manufacturer = new JTextField(20);
    private final JTextField model = new JTextField(20);
    private final JTextField year = new JTextField(20);
    private final JTextField power = new JTextField(20);
    private final JTextField residency = new JTextField(20);

    RegistrationFrame(User currentUser,
            VehicleStore vehicleStore, OwnerFrame ownerFrame) {

        super("Register Vehicle");
        this.currentUser = currentUser;
        this.vehicleStore = vehicleStore;
        this.ownerFrame = ownerFrame;

        JPanel fields = new JPanel(new GridLayout(0, 2, 5, 5));

        fields.add(new JLabel("Vehicle ID:"));
        fields.add(vehicleId);

        fields.add(new JLabel("Vehicle Manufacturer:"));
        fields.add(manufacturer);

        fields.add(new JLabel("Vehicle Model:"));
        fields.add(model);

        fields.add(new JLabel("Vehicle Year:"));
        fields.add(year);

        fields.add(new JLabel("Vehicle Computation Power:"));
        fields.add(power);

        fields.add(new JLabel("Residency (hours):"));
        fields.add(residency);

        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> submitRegistration());

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
        setLocationRelativeTo(ownerFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void submitRegistration() {
        String id = vehicleId.getText().trim();
        String make = manufacturer.getText().trim();
        String vehicleModel = model.getText().trim();
        String vehicleYear = year.getText().trim();
        String computation = power.getText().trim();
        String hours = residency.getText().trim();

        // Validate the vehicle information.
        if (!id.matches("[A-Za-z0-9_-]{3,8}")) {
            showError(
                    "Vehicle ID must be 3-8 letters, numbers, hyphens or underscores.");
            return;
        }

        if (!make.matches("[A-Za-z0-9 .-]{2,30}")
                || !vehicleModel.matches("[A-Za-z0-9 .-]{2,30}")) {
            showError(
                    "Manufacturer and model must each be 2-30 characters.");
            return;
        }

        int parsedYear;
        int parsedHours;

        try {
            parsedYear = Integer.parseInt(vehicleYear);
            parsedHours = Integer.parseInt(hours);
        } catch (NumberFormatException e) {
            showError("Year and residency must be whole numbers.");
            return;
        }

        if (parsedYear < 1900
                || parsedYear > java.time.Year.now().getValue() + 1
                || parsedHours < 1 || parsedHours > 168) {

            showError(
                    "Year or residency is out of range (residency must be 1-168 hours).");
            return;
        }

        // Create and store the Vehicle instance.
        if (!vehicleStore.registerVehicle(
                currentUser, id, make, vehicleModel,
                vehicleYear, computation, hours)) {

            showError(
                    "Vehicle not registered. Check power (> 0), duplicate ID or permissions.");
            return;
        }

        // Retrieve the instance that was just saved.
        Vehicle vehicle = vehicleStore.getVehicle(currentUser, id);

        // Record the successful registration.
        TransactionLog.append(
                "Owner: " + currentUser.getUsername()
                + ", Vehicle ID: " + vehicle.getVehicleId()
                + ", Vehicle Manufacturer: " + vehicle.getMake()
                + ", Vehicle Model: " + vehicle.getModel()
                + ", Vehicle Year: " + vehicle.getYear()
                + ", Vehicle Computation Power: "
                + vehicle.getComputationPower()
                + ", Vehicle Residency: " + vehicle.getResidency());

        ownerFrame.refreshTable();
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Invalid Vehicle",
                JOptionPane.ERROR_MESSAGE);
    }
}

package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import javax.swing.*;

import entity.Vehicle;
import entity.VehicleOwner;
import storage.TransactionLog;
import storage.UserStore;
import storage.VehicleStore;

public class OwnerFrame extends JFrame{
    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 720;


    private JButton button;
    private RegistrationFrame registrationFrame;


    public OwnerFrame(){

        super("Owner View");
        registrationButton();
        createPanel();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    class AddRegistrationListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (registrationFrame == null || !registrationFrame.isDisplayable()){ // so many separate windows don't open, it checks if one was already opened and sets it to visible
                registrationFrame = new RegistrationFrame();
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

    private void createPanel(){
        JPanel panel = new JPanel();
        panel.add(button);
        add(panel);
    }
}

class RegistrationFrame extends JFrame{
    private static final int FRAME_WIDTH = 700;
    private static final int FRAME_HEIGHT = 550;

    // vehicle attr
    private JTextField ownerIdField;
    private JTextField vehicleManufacturerField;
    private JTextField vehicleModelField;
    private JTextField vehicleYearField;
    private JTextField compPowField;
    private JTextField residencyField;

    private JButton submitButton;

    public RegistrationFrame(){
        super("Register Vehicle");
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
    // On submit: builds a Vehicle from the form, links it to its VehicleOwner, saves both to the stores, and logs it.
    class SubmitListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            String ownerId = ownerIdField.getText();
            String vManufacturer = vehicleManufacturerField.getText();
            String vModel = vehicleModelField.getText();
            String vYearText = vehicleYearField.getText();
            String vCompText = compPowField.getText();
            String residencyText = residencyField.getText();

            try {
                int vYear = Integer.parseInt(vYearText.trim());
                double vComp = Double.parseDouble(vCompText.trim());
                int residencyHours = Integer.parseInt(residencyText.trim());

                // Reuse the owner's account if we've already seen this ownerId, else create one.
                VehicleOwner owner = (VehicleOwner) UserStore.get(ownerId);
                if (owner == null) {
                    owner = new VehicleOwner(ownerId, "", "");
                    UserStore.add(owner);
                }

                // Form doesn't collect a vehicleId yet, so we derive one.
                String vehicleId = ownerId + "-" + vManufacturer + "-" + vModel + "-" + vYear;

                LocalDateTime arrival = LocalDateTime.now();
                LocalDateTime departure = arrival.plusHours(residencyHours);

                Vehicle vehicle = new Vehicle(ownerId, vehicleId, vManufacturer, vModel, vYear,
                        arrival, departure, vComp);

                VehicleStore.add(vehicle);
                owner.addVehicleId(vehicleId);

                TransactionLog.append("Owner: " + ownerId + ", " + "Vehicle Manufacturer: " + vManufacturer + ", " + "Vehicle Model: " + vModel + ", " + "Vehicle Year: " + vYear + ", " + "Vehicle Computation Power: " + vComp + ", " + "Vehicle Residency: " + residencyHours + "h" + ", Vehicle ID: " + vehicleId);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Invalid input: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RegistrationFrame.this.dispose();
        }
    }

}


package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
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
        JPanel panel = new JPanel();

        panel.add(new JLabel("Owner ID:"));
        panel.add(ownerIdField);
        panel.add(new JLabel("Vehicle Manufacturer:"));
        panel.add(vehicleManufacturerField);
        panel.add(new JLabel("Vehicle Model:"));
        panel.add(vehicleModelField);
        panel.add(new JLabel("Vehicle Year:"));
        panel.add(vehicleYearField);
        panel.add(new JLabel("Vehicle Computation Power:"));
        panel.add(compPowField);
        panel.add(new JLabel("Residency"));
        panel.add(residencyField);

        panel.add(submitButton);

        add(panel);
    }
    class SubmitListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            String ownerId = ownerIdField.getText();
            String vManufacturer = vehicleManufacturerField.getText();
            String vModel = vehicleModelField.getText();
            String vYear = vehicleYearField.getText();
            String vComp = compPowField.getText();
            System.out.println(ownerId + ", " + vManufacturer + ", " + vModel + ", " + vYear + ", " + vComp);

        }
    }

}


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


    public OwnerFrame(){

        super("Owner View");
        registrationButton();
        createPanel();

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    class AddRegistrationListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            new RegistrationFrame().setVisible(true);
        }
    }

    private void registrationButton(){

        button = new JButton("Register Vehicle");

        ActionListener listener = new AddRegistrationListener();
        button.addActionListener(listener);
        System.out.println("click");
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
    private JTextField vehicleManufacturer;
    private JTextField vehicleModel;
    private JTextField vehicleYear;
    private JTextField compPow;

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
        vehicleManufacturer = new JTextField(20);
        vehicleModel = new JTextField(20);
        vehicleYear = new JTextField(20);
        compPow = new JTextField(20);
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
        panel.add(vehicleManufacturer);
        panel.add(new JLabel("Vehicle Model:"));
        panel.add(vehicleModel);
        panel.add(new JLabel("Vehicle Year:"));
        panel.add(vehicleYear);
        panel.add(new JLabel("Vehicle Computation Power:"));
        panel.add(compPow);

        panel.add(submitButton);

        add(panel);
    }
    class SubmitListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            String ownerId = ownerIdField.getText();
            String vehicleManu = vehicleManufacturer.getText();
            // rest of attr

        }
    }

}


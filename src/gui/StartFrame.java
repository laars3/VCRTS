package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import entity.User;
import entity.VCController;
import network.Server;
import storage.UserStore;
import storage.VehicleStore;
import storage.JobStore;

public class StartFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;

    private CreateAccountFrame createAccountFrame;

    private final UserStore userStore;
    private final VehicleStore vehicleStore;
    private final JobStore jobStore;
    private final Server server;

    public StartFrame(UserStore userStore,
            VehicleStore vehicleStore, JobStore jobStore, Server server) {

        super("Log In");

        this.userStore = userStore;
        this.vehicleStore = vehicleStore;
        this.jobStore = jobStore;
        this.server = server;

        createTextFields();
        loginButton();
        createAccountButton();
        createPanel();

        pack();
        setLocationRelativeTo(null);
    }

    private void createTextFields() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
    }

    private void loginButton() {
        loginButton = new JButton("Log In");
        loginButton.addActionListener(new LoginListener());
    }

    class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = userStore.authenticate(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        StartFrame.this,
                        "Wrong username or password");
                return;
            }

            usernameField.setText("");
            passwordField.setText("");

            // every login gets its own window, so a client and the vcc can be open together
            JFrame frame;

            if ("VEHICLE_OWNER".equals(user.getRole())) {
                frame = new OwnerFrame(user, vehicleStore);
            } else if ("JOB_OWNER".equals(user.getRole())) {
                frame = new ClientFrame(user, jobStore);
            } else if ("VCC".equals(user.getRole())) {
                // vcc accounts are made as VCController in UserStore
                frame = new AdminFrame(StartFrame.this, (VCController) user,
                        userStore, vehicleStore, jobStore, server);
            } else {
                JOptionPane.showMessageDialog(
                        StartFrame.this,
                        "No view for this account yet");
                return;
            }

            frame.setVisible(true);
        }
    }

    private void createAccountButton() {
        createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(
                new CreateAccountListener());
    }

    class CreateAccountListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (createAccountFrame == null
                    || !createAccountFrame.isDisplayable()) {
                createAccountFrame = new CreateAccountFrame(userStore);
            }

            createAccountFrame.setVisible(true);
            createAccountFrame.toFront();
        }
    }

    private void createPanel() {
        JLabel title = new JLabel("VCRTS");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel intro = new JLabel(
                "Parked cars lend their spare computing power to run jobs in the cloud.");
        intro.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel ownerInfo = new JLabel(
                "Vehicle owners register their car to share its computing power.");
        ownerInfo.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel clientInfo = new JLabel(
                "Job owners submit jobs to run on those cars.");
        clientInfo.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel getStarted = new JLabel(
                "Log in or create an account to get started.");
        getStarted.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel introText = new JPanel(new GridLayout(0, 1, 0, 2));
        introText.add(intro);
        introText.add(ownerInfo);
        introText.add(clientInfo);
        introText.add(getStarted);

        JPanel introPanel = new JPanel(new BorderLayout(0, 6));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(introText, BorderLayout.CENTER);
        introPanel.add(new JSeparator(), BorderLayout.SOUTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(18, 20, 18, 20));

        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}

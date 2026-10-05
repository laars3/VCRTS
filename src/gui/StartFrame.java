package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import entity.User;
import storage.UserStore;

public class StartFrame extends JFrame{

    private JTextField usernameField;
    private JPasswordField passwordField;

    private JButton loginButton;
    private JButton createAccountButton;

    private OwnerFrame ownerFrame;
    private ClientFrame clientFrame;
    private CreateAccountFrame createAccountFrame;

    // shared with every frame that needs accounts
    private UserStore userStore;

    public StartFrame(UserStore userStore){

        super("VCRTS");
        this.userStore = userStore;
        createTextFields();
        loginButton();
        createAccountButton();
        createPanel();

        setLocationRelativeTo(null);
        pack();
    }

    private void createTextFields(){
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
    }

    private void loginButton(){
        loginButton = new JButton("Log In");

        ActionListener listener = new LoginListener();
        loginButton.addActionListener(listener);
    }

    class LoginListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = userStore.authenticate(username, password);
            if (user == null){
                JOptionPane.showMessageDialog(StartFrame.this, "Wrong username or password");
                return;
            }
            passwordField.setText("");

            // role decides which view opens
            if (user.getRole().equals("VEHICLE_OWNER")){
                if (ownerFrame == null || !ownerFrame.isDisplayable()){
                    ownerFrame = new OwnerFrame();
                }
                ownerFrame.setVisible(true);
                ownerFrame.toFront();
            }
            else if (user.getRole().equals("JOB_OWNER")){
                if (clientFrame == null || !clientFrame.isDisplayable()){
                    clientFrame = new ClientFrame();
                }
                clientFrame.setVisible(true);
                clientFrame.toFront();
            }
            else{
                JOptionPane.showMessageDialog(StartFrame.this, "No view for this account yet");
            }
        }
    }

    private void createAccountButton(){
        createAccountButton = new JButton("Create Account");

        ActionListener listener = new CreateAccountListener();
        createAccountButton.addActionListener(listener);
    }

    class CreateAccountListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (createAccountFrame == null || !createAccountFrame.isDisplayable()){
                createAccountFrame = new CreateAccountFrame(userStore);
            }
            createAccountFrame.setVisible(true);
            createAccountFrame.toFront();
        }
    }

    private void createPanel(){
        JLabel title = new JLabel("VCRTS");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel intro = new JLabel("<html><body style='width: 260px'>" + "Vehicular Cloud Real Time System. Vehicle owners rent out their car's computing power, job owners submit computational jobs to be completed ." + "</body></html>");

        JPanel introPanel = new JPanel(new BorderLayout(0, 6));
        introPanel.add(title, BorderLayout.NORTH);
        introPanel.add(intro, BorderLayout.CENTER);

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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        mainPanel.add(introPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

}

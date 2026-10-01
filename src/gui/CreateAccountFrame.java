package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import storage.UserStore;
import entity.User;

public class CreateAccountFrame extends JFrame {

    private final UserStore userStore;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> accountTypeBox;

    public CreateAccountFrame(UserStore userStore) {
        super("Create an Account");
        this.userStore = userStore;

        createFields();
        createPanel();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // Makes the account fields and leaves the type blank.
    private void createFields() {
        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        confirmPasswordField = new JPasswordField(18);
        accountTypeBox = new JComboBox<String>(new String[] {
                "Vehicle Owner", "Job Owner"
        });
        accountTypeBox.setSelectedIndex(-1);

        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);
        accountTypeBox.setFont(fieldFont);
    }

    // Arranges the form and button.
    private void createPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 14));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 18, 24));

        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 9, 11));
        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Password"));
        form.add(passwordField);
        form.add(new JLabel("Confirm Password"));
        form.add(confirmPasswordField);
        form.add(new JLabel("Account Type"));
        form.add(accountTypeBox);
        mainPanel.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        RoundedButton createButton = new RoundedButton("Create Account");
        createButton.addActionListener(new CreateListener());
        actions.add(createButton);
        mainPanel.add(actions, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Checks the form before adding the account.
    class CreateListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String selectedType = (String) accountTypeBox.getSelectedItem();

            if (username.length() == 0) {
                showError("Enter a username.");
                return;
            }
            if (password.trim().length() == 0) {
                showError("Enter a password.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                showError("The passwords do not match.");
                return;
            }
            if (userStore.usernameExists(username)) {
                showError("That username is already taken.");
                return;
            }
            if (selectedType == null) {
                showError("Choose an account type.");
                return;
            }

            String role = "VEHICLE_OWNER";
            if (selectedType.equals("Job Owner")) {
                role = "JOB_OWNER";
            }

            // Saves the account for this run of the app.
            userStore.addUser(new User(username, password, role));
            JOptionPane.showMessageDialog(CreateAccountFrame.this,
                    "Account created. You can now log in.", "Account Created",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    // Shows an error and leaves the form open.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Check Account Details",
                JOptionPane.ERROR_MESSAGE);
    }
}

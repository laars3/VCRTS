package storage;



import entity.User;
import entity.VehicleOwner;
import entity.JobOwner;
import entity.VCController;

import java.io.*;
import java.util.ArrayList;
import validation.InputValidator;

public class UserStore {

    // Accounts are stored in memory until the program closes.
    private final ArrayList<User> users = new ArrayList<User>();
    private final ArrayList<User> authenticated = new ArrayList<User>();

    private static final String FILE_NAME = "users.csv";

    public UserStore() {
        // Test accounts, now represented by their proper subclasses.
        users.add(new VehicleOwner("vowner", "vowner", "1"));
        users.add(new JobOwner("job", "job", "1"));
        users.add(new VCController("admin", "admin", "admin"));

        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while((line = in.readLine()) != null) {
                String [] parts = line.split(",", -1);
                if(parts.length != 4) continue;
                String id = parts[0];
                String username = parts[1];
                String password = parts[2];
                String role = parts[3];

                if(role.equals("VEHICLE_OWNER")) {
                    users.add(new VehicleOwner(id, username, password));
                } else if (role.equals("JOB_OWNER")) {
                    users.add(new JobOwner(id, username, password));
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveUsers(User user) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME, true))){

            String id;
            if(user instanceof VehicleOwner) {
                id = ((VehicleOwner) user).getOwnerId();

            } else {
                id = ((JobOwner) user).getClientId();
            }

            String line = String.join(",", id, user.getUsername(), user.getPassword(), user.getRole());

            out.write(line);
            out.newLine();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean addUser(User user) {
        if (user == null || !InputValidator.validAccount(user.getUsername(),
                user.getPassword(), user.getRole()) || usernameExists(user.getUsername())) {
            return false;
        }

        String username = user.getUsername().trim();
        User account;

        if ("VEHICLE_OWNER".equals(user.getRole())) {
            account = new VehicleOwner(username, username, user.getPassword());
        } else {
            account = new JobOwner(username, username, user.getPassword());
        }

        account.setUserId(user.getUserId());
        account.setName(user.getName());
        account.setEmail(user.getEmail());

        saveUsers(account);
        users.add(account);
        return true;
    }

    public boolean usernameExists(String username) {
        if (username == null) return false;

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return true;
            }
        }
        return false;
    }

    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())
                    && user.getPassword().equals(password)) {

                if (!authenticated.contains(user)) {
                    authenticated.add(user);
                }
                return user;
            }
        }
        return null;
    }

    public boolean isAuthenticated(User user) {
        for (User account : authenticated) {
            if (account == user) return true;
        }
        return false;
    }
}

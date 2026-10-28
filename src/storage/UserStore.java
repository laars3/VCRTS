package storage;



import entity.User;
import entity.VehicleOwner;
import entity.JobOwner;
import entity.VCController;
import java.util.ArrayList;
import validation.InputValidator;

public class UserStore {

    // Accounts are stored in memory until the program closes.
    private final ArrayList<User> users = new ArrayList<User>();
    private final ArrayList<User> authenticated = new ArrayList<User>();

    public UserStore() {
        // Test accounts, now represented by their proper subclasses.
        users.add(new VehicleOwner("vowner", "vowner", "1"));
        users.add(new JobOwner("job", "job", "1"));
        users.add(new VCController("admin", "admin", "admin"));
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

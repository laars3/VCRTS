package storage;

import entity.User;
import java.util.ArrayList;
import validation.InputValidator;

public class UserStore {

    // Keeps accounts in memory while the app is open.
    private ArrayList<User> users;
    private ArrayList<User> authenticated = new ArrayList<User>();

    // Adds the test accounts.
    public UserStore() {
        users = new ArrayList<User>();

        // These accounts are handy for testing.
        users.add(new User("vowner", "1", "VEHICLE_OWNER"));
        users.add(new User("job", "1", "JOB_OWNER"));
        users.add(new User("admin", "admin", "VCC"));
    }

    // Adds an account to this run of the app.
    public boolean addUser(User user) {
        // False means nothing was added. Old callers can ignore the return value.
        if (user == null || !InputValidator.validAccount(user.getUsername(),
                user.getPassword(), user.getRole()) || usernameExists(user.getUsername())) return false;
        users.add(new User(user.getUsername().trim(), user.getPassword(), user.getRole()));
        return true;
    }

    // Checks whether a username is taken.
    public boolean usernameExists(String username) {
        if (username == null) return false;

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return true;
            }
        }

        return false;
    }

    // Finds a user with matching login details.
    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;

        for (User user : users) {

            if (user.getUsername().equalsIgnoreCase(username.trim())
                    && user.getPassword().equals(password)) {

                if (!authenticated.contains(user)) authenticated.add(user);
                return user;
            }
        }

        return null;
    }
    // A made-up account with the same name is not a successful login.
    public boolean isAuthenticated(User user) {
        for (User account : authenticated) {
            if (account == user) return true;
        }
        return false;
    }
}

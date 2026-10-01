package storage;

import entity.User;
import java.util.ArrayList;

public class UserStore {

    // Keeps accounts in memory while the app is open.
    private ArrayList<User> users;

    // Adds the test accounts.
    public UserStore() {
        users = new ArrayList<User>();

        // These accounts are handy for testing.
        users.add(new User("owner1", "password", "VEHICLE_OWNER"));
        users.add(new User("jobowner1", "password", "JOB_OWNER"));
        users.add(new User("admin", "password", "VCC"));
    }

    // Adds an account to this run of the app.
    public void addUser(User user) {
        users.add(user);
    }

    // Checks whether a username is taken.
    public boolean usernameExists(String username) {

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    // Finds a user with matching login details.
    public User authenticate(String username, String password) {

        for (User user : users) {

            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {

                return user;
            }
        }

        return null;
    }
}
package storage;

import entity.User;

import java.util.Map;
import java.util.LinkedHashMap;

// In-memory registry of all users, keyed by userId.
public class UserStore {
    private static final Map<String, User> users = new LinkedHashMap<>();

    public static void add(User user) {
        users.put(user.getUserId(), user);
    }

    public static User get(String userId) {
        return users.get(userId);
    }

    public static boolean exists(String userId) {
        return users.containsKey(userId);
    }
}

package org.ates.tasktracker.repo;

import org.ates.tasktracker.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {

    private final static Object lock = new Object();

    private final Map<String, User> users = new HashMap<>();

    public void saveUser(User user) {
        synchronized (lock) {
            if (users.entrySet().size() > 5) {
                throw new RuntimeException("Too many users");
            }
            if (user != null) {
                users.put(user.getUsername(), user);
            }
        }
    }

    public User get(String username) {
        return users.get(username);
    }
}

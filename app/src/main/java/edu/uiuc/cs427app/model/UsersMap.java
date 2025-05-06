package edu.uiuc.cs427app.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class UsersMap {
    private Map<String, User> usersMap;

    public UsersMap() {
        this.usersMap = new HashMap<>();
    }

    public boolean isExistingUser(String username) {
        return usersMap.containsKey(username);
    }

    public User getUser(String username) {
        return usersMap.get(username);
    }

    public void setUser(User user) {
        usersMap.put(user.getUsername(), user);
    }

    public Map<String, User> getUsersMap() {
        return usersMap;
    }
}

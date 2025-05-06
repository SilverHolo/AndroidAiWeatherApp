package edu.uiuc.cs427app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

// Lombok annotations for creating getters/setters/constructors
@Data
@AllArgsConstructor
public class User {
    private String username;
    private String password;
    private String theme;

    public User (String username, String password) {
        this.username = username;
        this.password = password;
        this.theme = ""; // Can be empty string here since theme selection defaults to purple
    }
}
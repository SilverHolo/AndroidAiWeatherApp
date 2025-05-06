package edu.uiuc.cs427app.service;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.model.UsersMap;


// This class handles the user login logic as well as storing user data to a local file
public class UserService {
    private final Context context;
    private final String PREFS_NAME = "CITIES_PREFS";
    private final String USERS_MAP_KEY = "USERS_MAP";
    public UserService(Context context) {
        this.context = context;
    }

    // Tries to get the user from the users file
    public User loginUser(String username, String password) {
        UsersMap usersMap = readUsersFile();
        User existingUser = usersMap.getUser(username);

        if (existingUser != null && existingUser.getPassword().equals(password)) {
            return existingUser;
        }
        return null;
    }

    // Adds a new user to the users file with username, password and selected theme
    public boolean registerUser(User user) {
        // Check if user exists already
        UsersMap usersMap = readUsersFile();
        if (usersMap.isExistingUser(user.getUsername())) {
            return false;
        }
        writeUsersFile(user);
        return true;
    }

    // Reads the user file and converts users map from JSON
    private UsersMap readUsersFile() {
        UsersMap usersMap;
        Gson gson = new Gson();
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = mPrefs.getString(USERS_MAP_KEY, "");
        usersMap = gson.fromJson(json, UsersMap.class);
        if (usersMap != null) {
            return usersMap;
        }
        return new UsersMap();
    }

    // Converts users map to JSON and writes to the user file
    private void writeUsersFile(User user) {
        UsersMap usersMap = readUsersFile();
        usersMap.getUsersMap().put(user.getUsername(), user);

        Gson gson = new Gson();
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = mPrefs.edit();
        String json = gson.toJson(usersMap);
        prefEditor.putString(USERS_MAP_KEY, json);
        prefEditor.apply();
    }

    // Retrieves the saved theme preference for a specified user and sets it
    public void setUserThemePreference(String username) {
        UsersMap usersMap = readUsersFile();
        String userTheme = usersMap.getUser(username).getTheme();

        if (userTheme != null) {
            switch (userTheme) {
                case "Blue":
                    context.setTheme(R.style.Theme_Blue);
                    break;
                case "Green":
                    context.setTheme(R.style.Theme_Green);
                    break;
                default:
                    context.setTheme(R.style.Theme_Purple);
                    break;
            }
        }
    }
}

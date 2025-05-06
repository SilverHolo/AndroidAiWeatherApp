package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.service.UserService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private UserService userService;
    private EditText inputNewUsername, inputNewPassword;
    private Spinner themeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Finds and assigns the Spinner view for theme selection from the layout by its ID
        themeSpinner = findViewById(R.id.themeSpinner);
        inputNewUsername = findViewById(R.id.inputNewUsername);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        Button buttonRegisterUser = findViewById(R.id.buttonRegisterUser);
        Button buttonGoBack = findViewById(R.id.buttonGoBack);  // Go Back button

        userService = new UserService(this);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        inputNewUsername.setText(username);
        inputNewPassword.setText(password);

        // Handle register button click
        buttonRegisterUser.setOnClickListener(this);
        // Handle Go Back button click
        buttonGoBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegisterUser:
                String newUsername = inputNewUsername.getText().toString();
                String newPassword = inputNewPassword.getText().toString();
                // Retrieves selected theme from spinner
                String selectedTheme = themeSpinner.getSelectedItem().toString();

                User newUser = new User(newUsername, newPassword);
                // Saves the theme selected by the specified user
                newUser.setTheme(selectedTheme);
                boolean registerResponse = userService.registerUser(newUser);
                // Navigate back to LoginActivity after successful registration
                if (registerResponse) {
                    Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();  // Close RegisterActivity
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.buttonGoBack:
                finish();  // Ends the current activity and goes back to LoginActivity
                break;
        }
    }
}


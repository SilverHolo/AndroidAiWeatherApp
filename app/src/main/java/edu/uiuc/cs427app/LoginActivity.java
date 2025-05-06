package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.service.UserService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private UserService userService;
    private EditText inputUsername, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Set the correct login layout

//        Initialize buttons
        Button buttonLogin = findViewById(R.id.buttonLogin); // Login button
        Button buttonRegister = findViewById(R.id.buttonRegister); // Register button

        userService = new UserService(this);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        switch (view.getId()) {
            case R.id.buttonLogin:
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, R.string.login_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                User loggedInUser = userService.loginUser(username, password);

                if (loggedInUser != null) {
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(this, R.string.login_incorrect, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.buttonRegister:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);

                break;
        }
    }
}


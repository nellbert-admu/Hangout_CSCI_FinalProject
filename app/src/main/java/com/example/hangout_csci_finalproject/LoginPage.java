package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class LoginPage extends AppCompatActivity {
    EditText usernameInput, passwordInput;
    CheckBox rememberBox;
    Button signInButton, registerButton, clearButton;
    SharedPreferences prefs;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();
        prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        initViews();
        checkRememberMe();
    }

    private void initViews() {
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberBox = findViewById(R.id.RememberBox);
        signInButton = findViewById(R.id.SignInButton);
        registerButton = findViewById(R.id.registerButton);
        clearButton = findViewById(R.id.ClearButton);

        signInButton.setOnClickListener(v -> signIn());
        registerButton.setOnClickListener(v -> goToRegister());
        clearButton.setOnClickListener(v -> clearPreferences());

        rememberBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("rememberMe", isChecked);
            editor.apply();
        });
    }

    private void checkRememberMe() {
        boolean rememberMe = prefs.getBoolean("rememberMe", false);
        if (rememberMe) {
            User result = realm.where(User.class).equalTo("uuid", prefs.getString("UUID", "")).findFirst();
            if (result != null) {
                usernameInput.setText(result.getName());
                passwordInput.setText(result.getPassword());
                rememberBox.setChecked(true);
            }
        }
    }

    private void goToRegister() {
        startActivity(new Intent(this, RegisterPage.class));
    }

    private void signIn() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        User result = realm.where(User.class).equalTo("name", username).findFirst();
        if (result != null && result.getPassword().equals(password)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UUID", result.getUuid());
            editor.apply();
            Intent inside = new Intent(this, Home.class);
            startActivity(inside);
        } else {
            Toast.makeText(this, result == null ? "Incorrect Credentials" : "Incorrect password", Toast.LENGTH_LONG).show();
        }
    }

    private void clearPreferences() {
        prefs.edit().clear().apply();
        Toast.makeText(this, "Cleared Preferences", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
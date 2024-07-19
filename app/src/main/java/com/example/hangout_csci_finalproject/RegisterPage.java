package com.example.hangout_csci_finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;

public class RegisterPage extends AppCompatActivity {

    private EditText usernameInput, passwordInput, confirmPasswordInput;
    private Button saveButton, cancelButton;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        setupUI();
        realm = Realm.getDefaultInstance();
        logUsers();
    }

    private void setupUI() {
        usernameInput = findViewById(R.id.UsernameInputRegister);
        passwordInput = findViewById(R.id.PasswordInputRegister);
        confirmPasswordInput = findViewById(R.id.ConfirmPasswordInput);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.CancelButtonRegister);

        saveButton.setOnClickListener(v -> saveFunction());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void logUsers() {
        realm.where(User.class).findAll().forEach(user ->
                Log.d("RealmDemo", user.toString())
        );
    }

    private void saveFunction() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (validateInput(username, password, confirmPassword)) {
            if (isUsernameTaken(username)) {
                showToast("Username already taken");
            } else {
                addUser(username, password);
            }
        }
    }

    private boolean validateInput(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Fields must not be blank");
            return false;
        } else if (!password.equals(confirmPassword)) {
            showToast("Confirm password does not match");
            return false;
        }
        return true;
    }

    private boolean isUsernameTaken(String username) {
        return realm.where(User.class).equalTo("name", username).findFirst() != null;
    }

    private void addUser(String username, String password) {
        User newUser = new User();
        newUser.setName(username);
        newUser.setPassword(password);

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(newUser);
            realm.commitTransaction();

            long count = realm.where(User.class).count();
            showToast("New User Saved. Total: " + count);
            finish();
        } catch (Exception e) {
            showToast("Error saving");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
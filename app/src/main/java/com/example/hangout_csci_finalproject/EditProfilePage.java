package com.example.hangout_csci_finalproject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;

public class EditProfilePage extends AppCompatActivity {

    EditText UsernameInputEdit, PasswordInputEdit, ConfirmPasswordInputEdit;
    Button SaveButtonEdit, CancelButtonEdit;
    SharedPreferences prefs;
    Realm realm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_page);
        setupUI();
        realm = Realm.getDefaultInstance();
        prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        loadUserInfo();
    }

    private void setupUI() {
        UsernameInputEdit = findViewById(R.id.usernameInputEdit);
        PasswordInputEdit = findViewById(R.id.passwordInputEdit);
        ConfirmPasswordInputEdit = findViewById(R.id.confirmPasswordInputEdit);
        SaveButtonEdit = findViewById(R.id.saveButtonEdit);
        CancelButtonEdit = findViewById(R.id.cancelButtonEdit);

        SaveButtonEdit.setOnClickListener(v -> saveFunction());
        CancelButtonEdit.setOnClickListener(v -> finish());
    }

    private void loadUserInfo() {
        String UUID = prefs.getString("UUID", "");
        User result = realm.where(User.class).equalTo("uuid", UUID).findFirst();
        if (result != null) {
            UsernameInputEdit.setText(result.getName());
        }
    }

    public void saveFunction() {
        String username = UsernameInputEdit.getText().toString();
        String password = PasswordInputEdit.getText().toString();
        String confirmPassword = ConfirmPasswordInputEdit.getText().toString();

        if (validateInput(username, password, confirmPassword)) {
            updateUser(username, password);
        }
    }

    private boolean validateInput(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Fields must not be blank", Toast.LENGTH_LONG).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updateUser(String username, String password) {
        String UUID = prefs.getString("UUID", "");
        User result = realm.where(User.class).equalTo("uuid", UUID).findFirst();
        if (result != null) {
            realm.beginTransaction();
            result.setName(username);
            result.setPassword(password);
            realm.commitTransaction();
            Toast.makeText(this, "User Edited.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
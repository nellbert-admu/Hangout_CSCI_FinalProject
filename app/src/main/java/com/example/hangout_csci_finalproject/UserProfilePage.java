package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;

public class UserProfilePage extends AppCompatActivity {

    private TextView usernameUserProfile, passwordUserProfile;
    private Button editUserButton, backButtonUserProfile, logoutButton;
    private SharedPreferences prefs;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);
        initUI();
        loadData();
    }

    private void initUI() {
        usernameUserProfile = findViewById(R.id.usernameUserProfile);
        passwordUserProfile = findViewById(R.id.passwordUserProfile);
        editUserButton = findViewById(R.id.editUserButton);
        backButtonUserProfile = findViewById(R.id.backButtonUserProfile);
        logoutButton = findViewById(R.id.logoutbutton);

        editUserButton.setOnClickListener(v -> goToEditProfile());
        backButtonUserProfile.setOnClickListener(v -> finish());
        logoutButton.setOnClickListener(v -> {goToLoginPage();});
    }

    private void goToLoginPage() {
        startActivity(new Intent(this, LoginPage.class));
    }

    private void loadData() {
        prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String UUID = prefs.getString("UUID", "");
        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("uuid", UUID).findFirst();
        if (user != null) {
            usernameUserProfile.setText("welcome, " + user.getName());
            passwordUserProfile.setText("password: " + user.getPassword());
        }
    }

    private void goToEditProfile() {
        startActivity(new Intent(this, EditProfilePage.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}
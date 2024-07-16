package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class UserProfilePage extends AppCompatActivity {

    TextView usernameUserProfile;
    TextView passwordUserProfile;
    Button editUserButton;
    Button backButtonUserProfile;
    SharedPreferences prefs;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    public void init()
    {
        prefs = getSharedPreferences("my_prefs",MODE_PRIVATE);
        String UUID = prefs.getString("UUID", "");

        realm = Realm.getDefaultInstance();
        User result = realm.where(User.class)
                .equalTo("uuid", UUID)
                .findFirst();

        usernameUserProfile = findViewById(R.id.usernameUserProfile);
        passwordUserProfile = findViewById(R.id.passwordUserProfile);
        usernameUserProfile.setText("Username: "+result.getName());
        passwordUserProfile.setText("Password: "+result.getPassword());

        editUserButton = findViewById(R.id.editUserButton);
        editUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });

        backButtonUserProfile = findViewById(R.id.backButtonUserProfile);
        backButtonUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void goToEditProfile()
    {
        Intent intent = new Intent(this, EditProfilePage.class);
        startActivity(intent);
    }

    public void onRestart() // Updates the shown username & password after coming back from the edit profile screen
    {
        super.onRestart();
        prefs = getSharedPreferences("my_prefs",MODE_PRIVATE);
        String UUID = prefs.getString("UUID", "");

        realm = Realm.getDefaultInstance();
        User result = realm.where(User.class)
                .equalTo("uuid", UUID)
                .findFirst();

        usernameUserProfile = findViewById(R.id.usernameUserProfile);
        passwordUserProfile = findViewById(R.id.passwordUserProfile);
        usernameUserProfile.setText("Username: "+result.getName());
        passwordUserProfile.setText("Password: "+result.getPassword());
    }

    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class Home extends AppCompatActivity {

    Button UserButtonHome;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    public void init(){
        UserButtonHome = findViewById(R.id.userButtonHome);
        UserButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUserPage();
            }
        });

        realm = Realm.getDefaultInstance();
    }

    public void gotoUserPage(){
        Intent intent = new Intent(this, UserProfilePage.class);
        startActivity(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
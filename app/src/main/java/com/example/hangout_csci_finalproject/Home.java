package com.example.hangout_csci_finalproject;

import android.content.Intent;
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

    private Button UserButtonHome;
    private Button detailButton;
    private Button exploreButton;
    private Realm realm;

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
        realm = Realm.getDefaultInstance();
    }

    private void init() {
        UserButtonHome = findViewById(R.id.userButtonHome);
        detailButton = findViewById(R.id.placedetailbutton);
        exploreButton = findViewById(R.id.explorebutton);

        UserButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUserPage();
            }
        });

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailButtonClicked(v);
            }
        });

        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExploreButtonClicked(v);
            }
        });
    }

    private void gotoUserPage() {
        Intent intent = new Intent(this, UserProfilePage.class);
        startActivity(intent);
    }

    private void onDetailButtonClicked(View v) {
        Intent intent = new Intent(this, PlaceDetail.class);
        startActivity(intent);
    }

    private void onExploreButtonClicked(View v) {
        Intent intent = new Intent(this, ExplorePage.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}

package com.example.hangout_csci_finalproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class PlaceDetail extends AppCompatActivity {
    private TextView placeNameView, locationView, descriptionView;
    private ToggleButton toggleDining, toggleOutlets, toggleAircon, toggleQuiet, toggleRestrooms, toggleWifi;
    private RatingBar ratingBar;
    private String placeId;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_placedetail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        realm = Realm.getDefaultInstance();
        initViews();
        placeId = getIntent().getStringExtra("placeId");
        if (placeId != null) {
            loadPlaceData(placeId);
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void initViews() {
        placeNameView = findViewById(R.id.placeName);
        locationView = findViewById(R.id.placeLocation);
        descriptionView = findViewById(R.id.placeDescription);
        toggleDining = findViewById(R.id.toggleDining);
        toggleOutlets = findViewById(R.id.toggleOutlets);
        toggleAircon = findViewById(R.id.toggleAircon);
        toggleQuiet = findViewById(R.id.toggleQuiet);
        toggleRestrooms = findViewById(R.id.toggleRestrooms);
        toggleWifi = findViewById(R.id.togglePlaceWifi);
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void loadPlaceData(String placeId) {
        Place place = realm.where(Place.class).equalTo("id", placeId).findFirst();
        if (place != null) {
            placeNameView.setText(place.getName());
            locationView.setText(place.getLocation());
            descriptionView.setText(place.getDescription());
            toggleDining.setChecked(place.isDining());
            toggleOutlets.setChecked(place.isOutlet());
            toggleAircon.setChecked(place.isAircon());
            toggleQuiet.setChecked(place.isQuiet());
            toggleRestrooms.setChecked(place.isRestroom());
            toggleWifi.setChecked(place.isWifi());
            ratingBar.setRating(place.getRating());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
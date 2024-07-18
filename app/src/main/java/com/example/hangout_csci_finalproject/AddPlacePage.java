package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;

public class AddPlacePage extends AppCompatActivity {
    private Realm realm;
    private EditText placeNameView;
    private EditText locationView;
    private EditText descriptionView;
    private ToggleButton toggleDining;
    private ToggleButton toggleOutlets;
    private ToggleButton toggleAircon;
    private ToggleButton toggleQuiet;
    private ToggleButton toggleRestrooms;
    private ToggleButton toggleWifi;
    private RatingBar ratingBar;
    private Button addButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        placeNameView = findViewById(R.id.editPlaceName);
        locationView = findViewById(R.id.editPlaceLoc);
        descriptionView = findViewById(R.id.editDesc);
        toggleDining = findViewById(R.id.toggleDining);
        toggleOutlets = findViewById(R.id.toggleOutlets);
        toggleAircon = findViewById(R.id.toggleAircon);
        toggleQuiet = findViewById(R.id.toggleQuiet);
        toggleRestrooms = findViewById(R.id.toggleRestrooms);
        toggleWifi = findViewById(R.id.togglePlaceWifi);
        ratingBar = findViewById(R.id.ratingBar);
        addButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.back_button);

        realm = Realm.getDefaultInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPlace();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClicked();
            }
        });
    }

    private void addNewPlace() {
        String placeName = placeNameView.getText().toString();
        String location = locationView.getText().toString();
        String description = descriptionView.getText().toString();

        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String userUuid = prefs.getString("UUID", "");

        if (placeName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Place name and location cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.beginTransaction();
        Place place = realm.createObject(Place.class);
        place.setName(placeName);
        place.setLocation(location);
        place.setDescription(description);
        place.setUserUuid(userUuid);
        place.setDining(toggleDining.isChecked());
        place.setOutlet(toggleOutlets.isChecked());
        place.setAircon(toggleAircon.isChecked());
        place.setQuiet(toggleQuiet.isChecked());
        place.setRestroom(toggleRestrooms.isChecked());
        place.setWifi(toggleWifi.isChecked());
        realm.commitTransaction();

        finish();
    }

    private void onBackButtonClicked() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}

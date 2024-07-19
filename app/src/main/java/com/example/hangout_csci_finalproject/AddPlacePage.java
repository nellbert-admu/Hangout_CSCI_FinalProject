package com.example.hangout_csci_finalproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import io.realm.Realm;

public class AddPlacePage extends AppCompatActivity {
    private EditText placeNameView, locationView, descriptionView;
    private ToggleButton toggleDining, toggleOutlets, toggleAircon, toggleQuiet, toggleRestrooms, toggleWifi;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        initViews();
        Realm realm = Realm.getDefaultInstance();

        findViewById(R.id.addButton).setOnClickListener(v -> addNewPlace(realm));
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void initViews() {
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
    }

    private void addNewPlace(Realm realm) {
        String placeName = placeNameView.getText().toString();
        String location = locationView.getText().toString();
        if (placeName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Place name and location cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique String ID for the new Place object
        String uniqueId = UUID.randomUUID().toString();

        realm.beginTransaction();
        Place place = realm.createObject(Place.class, uniqueId); // Adjusted to use createObject with ID
        place.setName(placeName);
        place.setLocation(location);
        place.setDescription(descriptionView.getText().toString());
        place.setUserUuid(getSharedPreferences("my_prefs", MODE_PRIVATE).getString("UUID", ""));
        place.setDining(toggleDining.isChecked());
        place.setOutlet(toggleOutlets.isChecked());
        place.setAircon(toggleAircon.isChecked());
        place.setQuiet(toggleQuiet.isChecked());
        place.setRestroom(toggleRestrooms.isChecked());
        place.setWifi(toggleWifi.isChecked());
        realm.commitTransaction();

        long placeCount = realm.where(Place.class).count();
        long userCount = realm.where(User.class).count();
        Log.d("RealmCounts", "Places: " + placeCount + ", Users: " + userCount);

        setResult(Activity.RESULT_OK);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();
    }
}
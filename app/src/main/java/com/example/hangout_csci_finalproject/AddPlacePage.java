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

/**
 * Activity for adding a new place in the application.
 * This activity allows users to input details about a new place,
 * including its name, location, description, and various attributes like dining, outlets, etc.
 * It also includes a rating bar for users to rate the place.
 */

public class AddPlacePage extends AppCompatActivity {
    private EditText placeNameView, locationView, descriptionView;
    private ToggleButton toggleDining, toggleOutlets, toggleAircon, toggleQuiet, toggleRestrooms, toggleWifi;
    private RatingBar ratingBar;

    /**
     * Initializes the activity, views, and sets up listeners for the add and back buttons.
     * It also initializes the Realm database instance.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        initViews();
        Realm realm = Realm.getDefaultInstance();

        findViewById(R.id.addButton).setOnClickListener(v -> addNewPlace(realm));
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            // handle the rating value (rating) if needed immediately

        });
    }

    /**
     * Initializes the views by finding them by their IDs.
     */
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

    /**
     * Adds a new place to the Realm database with the details provided by the user.
     * It generates a unique ID for each place, collects all the information from the input fields,
     * and saves the new place object to the database.
     * @param realm The Realm database instance where the new place will be added.
     */
    private void addNewPlace(Realm realm) {
        String placeName = placeNameView.getText().toString();
        String location = locationView.getText().toString();
        if (placeName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Place name and location cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }


        String uniqueId = UUID.randomUUID().toString();

        realm.beginTransaction();
        Place place = realm.createObject(Place.class, uniqueId);
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
        place.setRating(ratingBar.getRating());
        realm.commitTransaction();

        long placeCount = realm.where(Place.class).count();
        long userCount = realm.where(User.class).count();
        Log.d("RealmCounts", "Places: " + placeCount + ", Users: " + userCount);

        setResult(Activity.RESULT_OK);
        finish();
    }

    /**
     * Closes the Realm instance when the activity is destroyed to prevent memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();
    }
}
package com.example.hangout_csci_finalproject;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;

public class PlaceDetail extends AppCompatActivity {
    private TextView placeNameView, locationView, descriptionView, featuresView;
    private RatingBar ratingBar;
    private String placeId;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedetail);

        realm = Realm.getDefaultInstance();
        initViews();
        placeId = getIntent().getStringExtra("place_id");

        if (placeId != null) {
            loadPlaceData(placeId);
        } else {
            Toast.makeText(this, "Error loading place details", Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void initViews() {
        placeNameView = findViewById(R.id.placeName);
        locationView = findViewById(R.id.placeLocation);
        descriptionView = findViewById(R.id.placeDescription);
        featuresView = findViewById(R.id.placeFeatures);
        ratingBar = findViewById(R.id.ratingBar);

        // Set fields to be read-only
        placeNameView.setEnabled(false);
        locationView.setEnabled(false);
        descriptionView.setEnabled(false);
        ratingBar.setIsIndicator(true);
    }

    private void loadPlaceData(String placeId) {
        Place place = realm.where(Place.class).equalTo("id", placeId).findFirst();
        if (place != null) {
            placeNameView.setText(place.getName());
            locationView.setText(place.getLocation());
            descriptionView.setText(place.getDescription());
            ratingBar.setRating(place.getRating());

            StringBuilder features = new StringBuilder();
            if (place.isDining()) features.append("#dining ");
            if (place.isOutlet()) features.append("#outlets ");
            if (place.isAircon()) features.append("#aircon ");
            if (place.isQuiet()) features.append("#quiet ");
            if (place.isRestroom()) features.append("#restrooms ");
            if (place.isWifi()) features.append("#wifi ");

            featuresView.setText(features.toString().trim());
        } else {
            Toast.makeText(this, "Place not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}

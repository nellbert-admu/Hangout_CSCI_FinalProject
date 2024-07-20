package com.example.hangout_csci_finalproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;

public class PlaceDetail extends AppCompatActivity {
    private TextView placeNameView, locationView, descriptionView, featuresView;
    private RatingBar ratingBar;
    private String placeId;
    private Realm realm;
    private ImageView placeImage;

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
        placeImage = findViewById(R.id.placeImage);

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
            updateImageView(place.getPath());

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

    private void updateImageView(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(placeImage);
        } else {
            placeImage.setImageResource(R.mipmap.ic_launcher); // Fallback image
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

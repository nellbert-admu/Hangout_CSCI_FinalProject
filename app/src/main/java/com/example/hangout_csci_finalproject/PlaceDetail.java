package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

public class PlaceDetail extends AppCompatActivity {

    private Realm realm;
    private TextView placeNameView;
    private TextView locationView;
    private TextView descriptionView;
    private ToggleButton toggleDining;
    private ToggleButton toggleOutlets;
    private ToggleButton toggleAircon;
    private ToggleButton toggleQuiet;
    private ToggleButton toggleRestrooms;
    private ToggleButton toggleWifi;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedetail);

        placeNameView = findViewById(R.id.placeName);
        locationView = findViewById(R.id.placeLocation);
        descriptionView = findViewById(R.id.placeDescription);
        toggleDining = findViewById(R.id.toggleDining);
        toggleOutlets = findViewById(R.id.toggleOutlets);
        toggleAircon = findViewById(R.id.toggleAircon);
        toggleQuiet = findViewById(R.id.toggleQuiet);
        toggleRestrooms = findViewById(R.id.toggleRestrooms);
        toggleWifi = findViewById(R.id.togglePlaceWifi);
        backButton = findViewById(R.id.backButton);

        realm = Realm.getDefaultInstance();

        String placeId = getIntent().getStringExtra("placeId");
        if (placeId != null) {
            Place place = realm.where(Place.class).equalTo("id", placeId).findFirst();
            if (place != null) {
                placeNameView.setText(place.getName());
                toggleDining.setChecked(place.isDining());
                toggleOutlets.setChecked(place.isOutlet());
                toggleAircon.setChecked(place.isAircon());
                toggleQuiet.setChecked(place.isQuiet());
                toggleRestrooms.setChecked(place.isRestroom());
                toggleWifi.setChecked(place.isWifi());
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClicked(view);
            }
        });
    }

    public void onBackButtonClicked(View view) {
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

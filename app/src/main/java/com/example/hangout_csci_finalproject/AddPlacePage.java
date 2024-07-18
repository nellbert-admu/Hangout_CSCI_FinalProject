package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button addButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        placeNameView = findViewById(R.id.editPlaceName);
        locationView = findViewById(R.id.editPlaceLoc);
        descriptionView = findViewById(R.id.editTextTextMultiLine);
        toggleDining = findViewById(R.id.toggleDining);
        toggleOutlets = findViewById(R.id.toggleOutlets);
        toggleAircon = findViewById(R.id.toggleAircon);
        toggleQuiet = findViewById(R.id.toggleQuiet);
        toggleRestrooms = findViewById(R.id.toggleRestrooms);
        toggleWifi = findViewById(R.id.togglePlaceWifi);
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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Place place = realm.createObject(Place.class);
                place.setName(placeNameView.getText().toString());
                place.setDining(toggleDining.isChecked());
                place.setOutlet(toggleOutlets.isChecked());
                place.setAircon(toggleAircon.isChecked());
                place.setQuiet(toggleQuiet.isChecked());
                place.setRestroom(toggleRestrooms.isChecked());
                place.setWifi(toggleWifi.isChecked());
            }
        });

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
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

package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExplorePage extends AppCompatActivity {
    private EditText editTextQuery;
    private CheckBox dining, outlet, aircon, quiet, restroom, wifi;
    private Button searchButton, clearButton, backButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "explore_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        editTextQuery = findViewById(R.id.search);
        searchButton = findViewById(R.id.search_button);
        clearButton = findViewById(R.id.clear_button);
        backButton = findViewById(R.id.back_button);
        dining = findViewById(R.id.dining);
        outlet = findViewById(R.id.outlet);
        aircon = findViewById(R.id.aircon);
        quiet = findViewById(R.id.quiet);
        restroom = findViewById(R.id.restroom);
        wifi = findViewById(R.id.wifi);

        loadCheckboxStates(); // Load checkbox states
        loadSearchQuery(); // Load search query

        searchButton.setOnClickListener(v -> {
            String query = editTextQuery.getText().toString();
            boolean isDining = dining.isChecked();
            boolean isOutlet = outlet.isChecked();
            boolean isAircon = aircon.isChecked();
            boolean isQuiet = quiet.isChecked();
            boolean isRestroom = restroom.isChecked();
            boolean isWifi = wifi.isChecked();

            saveSearchQuery(query); // Save search query
            saveCheckboxStates(); // Save checkbox states

            Intent intent = new Intent(ExplorePage.this, Home.class);
            intent.putExtra("QUERY", query);
            intent.putExtra("DINING", isDining);
            intent.putExtra("OUTLET", isOutlet);
            intent.putExtra("AIRCON", isAircon);
            intent.putExtra("QUIET", isQuiet);
            intent.putExtra("RESTROOM", isRestroom);
            intent.putExtra("WIFI", isWifi);
            intent.putExtra("FILTERS_CLEARED", false); // Flag indicating filters are not cleared
            startActivity(intent);
        });

        clearButton.setOnClickListener(v -> {
            editTextQuery.setText("");
            dining.setChecked(false);
            outlet.setChecked(false);
            aircon.setChecked(false);
            quiet.setChecked(false);
            restroom.setChecked(false);
            wifi.setChecked(false);

            // Clear saved states
            clearSavedStates();

            // Show Toast message
            Toast.makeText(ExplorePage.this, "Fields cleared", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        });
    }

    private void clearSavedStates() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("QUERY");
        editor.remove("DINING");
        editor.remove("OUTLET");
        editor.remove("AIRCON");
        editor.remove("QUIET");
        editor.remove("RESTROOM");
        editor.remove("WIFI");
        editor.apply();
    }

    private void saveCheckboxStates() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("DINING", dining.isChecked());
        editor.putBoolean("OUTLET", outlet.isChecked());
        editor.putBoolean("AIRCON", aircon.isChecked());
        editor.putBoolean("QUIET", quiet.isChecked());
        editor.putBoolean("RESTROOM", restroom.isChecked());
        editor.putBoolean("WIFI", wifi.isChecked());
        editor.apply();
    }

    private void loadCheckboxStates() {
        dining.setChecked(sharedPreferences.getBoolean("DINING", false));
        outlet.setChecked(sharedPreferences.getBoolean("OUTLET", false));
        aircon.setChecked(sharedPreferences.getBoolean("AIRCON", false));
        quiet.setChecked(sharedPreferences.getBoolean("QUIET", false));
        restroom.setChecked(sharedPreferences.getBoolean("RESTROOM", false));
        wifi.setChecked(sharedPreferences.getBoolean("WIFI", false));
    }

    private void saveSearchQuery(String query) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("QUERY", query);
        editor.apply();
    }

    private void loadSearchQuery() {
        String query = sharedPreferences.getString("QUERY", "");
        editTextQuery.setText(query);
    }
}

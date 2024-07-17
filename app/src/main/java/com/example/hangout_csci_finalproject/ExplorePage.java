package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ExplorePage extends AppCompatActivity {
    private EditText editTextQuery;
    private CheckBox dining, outlet, aircon, quiet, restroom, wifi;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        editTextQuery = findViewById(R.id.search);
        searchButton = findViewById(R.id.search_button);
        dining = findViewById(R.id.dining);
        outlet = findViewById(R.id.outlet);
        aircon = findViewById(R.id.aircon);
        quiet = findViewById(R.id.quiet);
        restroom = findViewById(R.id.restroom);
        wifi = findViewById(R.id.wifi);
        searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            String query = editTextQuery.getText().toString();
            boolean isDining = dining.isChecked();
            boolean isOutlet = outlet.isChecked();
            boolean isAircon = aircon.isChecked();
            boolean isQuiet = quiet.isChecked();
            boolean isRestroom = restroom.isChecked();
            boolean isWifi = wifi.isChecked();

            Intent intent = new Intent(ExplorePage.this, ExploreResultsPage.class);
            intent.putExtra("QUERY", query);
            intent.putExtra("DINING", isDining);
            intent.putExtra("OUTLET", isOutlet);
            intent.putExtra("AIRCON", isAircon);
            intent.putExtra("QUIET", isQuiet);
            intent.putExtra("RESTROOM", isRestroom);
            intent.putExtra("WIFI", isWifi);
            startActivity(intent);
        });
    }
}

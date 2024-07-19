package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class Home extends AppCompatActivity {

    private static final int ADD_PLACE_REQUEST_CODE = 1;
    private Realm realm;
    private TextView textView;
    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        setupWindowInsets();
        setupUI();
        realm = Realm.getDefaultInstance();
        setupRecyclerView();
        applyFilters(); // Load preferences and apply filters
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupUI() {
        textView = findViewById(R.id.textView);
        prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String UUID = prefs.getString("UUID", "");
        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("uuid", UUID).findFirst();
        textView.setText("welcome, " + user.getName() + ". where to?");

        findViewById(R.id.userButtonHome).setOnClickListener(v -> gotoUserPage());
        findViewById(R.id.explorebutton).setOnClickListener(v -> startActivity(new Intent(this, ExplorePage.class)));
        findViewById(R.id.addPlaceButton).setOnClickListener(v -> startActivityForResult(new Intent(this, AddPlacePage.class), ADD_PLACE_REQUEST_CODE));
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void applyFilters() {
        // Load saved preferences
        SharedPreferences sharedPreferences = getSharedPreferences("explore_prefs", MODE_PRIVATE);
        String query = sharedPreferences.getString("QUERY", "");
        boolean isDining = sharedPreferences.getBoolean("DINING", false);
        boolean isOutlet = sharedPreferences.getBoolean("OUTLET", false);
        boolean isAircon = sharedPreferences.getBoolean("AIRCON", false);
        boolean isQuiet = sharedPreferences.getBoolean("QUIET", false);
        boolean isRestroom = sharedPreferences.getBoolean("RESTROOM", false);
        boolean isWifi = sharedPreferences.getBoolean("WIFI", false);

        RealmQuery<Place> realmQuery = realm.where(Place.class);

        // Apply search query
        if (query != null && !query.isEmpty()) {
            realmQuery = realmQuery
                    .beginGroup()
                    .contains("name", query, Case.INSENSITIVE)
                    .or()
                    .contains("location", query, Case.INSENSITIVE)
                    .endGroup();
        }

        // Apply filters
        if (isDining) {
            realmQuery = realmQuery.and().equalTo("dining", true);
        }
        if (isOutlet) {
            realmQuery = realmQuery.and().equalTo("outlet", true);
        }
        if (isAircon) {
            realmQuery = realmQuery.and().equalTo("aircon", true);
        }
        if (isQuiet) {
            realmQuery = realmQuery.and().equalTo("quiet", true);
        }
        if (isRestroom) {
            realmQuery = realmQuery.and().equalTo("restroom", true);
        }
        if (isWifi) {
            realmQuery = realmQuery.and().equalTo("wifi", true);
        }

        // Execute the query
        RealmResults<Place> filteredResults = realmQuery.findAll();
        adapter = new PlaceAdapter(this, filteredResults, true);
        recyclerView.setAdapter(adapter);
    }

    private void gotoUserPage() {
        startActivity(new Intent(this, UserProfilePage.class));
    }

    protected void deletePlace(Place place) {
        realm.beginTransaction();
        place.deleteFromRealm(); // Deletes the place from Realm.
        realm.commitTransaction();
        updatePlacesList(); // Updates the list of places.
    }

    protected void editPlace(Place place) {
        Intent intent = new Intent(this, EditPlacePage.class);
        intent.putExtra("placeId", place.getId());
        startActivity(intent);
    }

    public void updatePlacesList() {
        RealmResults<Place> updatedList = realm.where(Place.class).findAll(); // Fetches places from Realm.
        if (adapter == null) {
            adapter = new PlaceAdapter(this, updatedList, true); // Initializes the adapter if it's null.
            recyclerView.setAdapter(adapter); // Sets the adapter to the RecyclerView.
        } else {
            adapter.updateData(updatedList); // Updates the adapter data if it already exists.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            applyFilters(); // Re-apply filters when returning from another activity
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

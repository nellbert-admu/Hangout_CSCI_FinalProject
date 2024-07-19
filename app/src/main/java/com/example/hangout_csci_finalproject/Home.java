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

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * The Home activity serves as the main screen of the application, displaying a list of places
 * and providing navigation to other activities such as adding a new place, exploring places,
 * and viewing user profile.
 */
public class Home extends AppCompatActivity {

    private static final int ADD_PLACE_REQUEST_CODE = 1; // Request code for adding a new place.
    private Realm realm; // Instance of Realm for database operations.
    private TextView textView;
    private RecyclerView recyclerView; // RecyclerView for displaying the list of places.
    private PlaceAdapter adapter; // Adapter for the RecyclerView.
    private SharedPreferences prefs;

    /**
     * Initializes the activity, sets up UI components, and prepares the RecyclerView.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables edge-to-edge display.
        setContentView(R.layout.activity_home); // Sets the content view for this activity.
        setupWindowInsets(); // Adjusts window insets for the layout.
        setupUI(); // Sets up UI components like buttons.
        realm = Realm.getDefaultInstance(); // Initializes Realm instance.
        setupRecyclerView(); // Sets up the RecyclerView.
    }

    /**
     * Adjusts the padding of the main view to accommodate system bars.
     */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Sets up UI components by assigning click listeners to buttons.
     */
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

    /**
     * Initializes and configures the RecyclerView for displaying places.
     */
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updatePlacesList(); // Fetches and displays the list of places.
    }

    /**
     * Fetches the list of places from the database and updates the RecyclerView adapter.
     */
    public void updatePlacesList() {
        RealmResults<Place> updatedList = realm.where(Place.class).findAll(); // Fetches places from Realm.
        if (adapter == null) {
            adapter = new PlaceAdapter(this, updatedList, true); // Initializes the adapter if it's null.
            recyclerView.setAdapter(adapter); // Sets the adapter to the RecyclerView.
        } else {
            adapter.updateData(updatedList); // Updates the adapter data if it already exists.
        }
    }

    /**
     * Navigates to the user profile page.
     */
    private void gotoUserPage() {
        startActivity(new Intent(this, UserProfilePage.class));
    }

    /**
     * Handles the result from activities launched for result.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updatePlacesList(); // Updates the list of places if the result is OK.
        }
    }

    /**
     * Deletes a place from the database and updates the list.
     *
     * @param place The place to be deleted.
     */
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

    /**
     * Cleans up the Realm instance when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close(); // Closes the Realm instance.
        }
    }
}
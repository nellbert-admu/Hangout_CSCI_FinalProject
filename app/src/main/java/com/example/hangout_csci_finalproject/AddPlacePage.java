package com.example.hangout_csci_finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
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
    File finalImageFile;
    private ImageView PlaceImage;
    private Realm realm;
    private Place newPlace = new Place();
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
        realm = Realm.getDefaultInstance();

        findViewById(R.id.editButton).setOnClickListener(v -> addNewPlace());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        PlaceImage = findViewById(R.id.PlaceImage);
        PlaceImage.setOnClickListener(v -> takePic());
    }

    private void addNewPlace() {
        String placeName = placeNameView.getText().toString();
        String location = locationView.getText().toString();
        if (placeName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Place name and location cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.executeTransaction(r -> {
            newPlace.setName(placeName);
            newPlace.setLocation(location);
            newPlace.setDescription(descriptionView.getText().toString());
            newPlace.setUserUuid(getSharedPreferences("my_prefs", MODE_PRIVATE).getString("UUID", ""));
            newPlace.setDining(toggleDining.isChecked());
            newPlace.setOutlet(toggleOutlets.isChecked());
            newPlace.setAircon(toggleAircon.isChecked());
            newPlace.setQuiet(toggleQuiet.isChecked());
            newPlace.setRestroom(toggleRestrooms.isChecked());
            newPlace.setWifi(toggleWifi.isChecked());
            newPlace.setRating(ratingBar.getRating());
            r.copyToRealmOrUpdate(newPlace);
        });

        long placeCount = realm.where(Place.class).count();
        long userCount = realm.where(User.class).count();
        Log.d("RealmCounts", "Places: " + placeCount + ", Users: " + userCount);

        setResult(Activity.RESULT_OK);
        finish();
    }

    public void takePic() {
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, 0);
    }

    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (requestCode == 0 && responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
            try {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");
                if (jpeg != null) {
                    finalImageFile = new File(getExternalCacheDir(), UUID.randomUUID().toString() + ".jpeg");
                    try (FileOutputStream fos = new FileOutputStream(finalImageFile)) {
                        fos.write(jpeg);
                        realm.executeTransaction(r -> {
                            newPlace.setPath(finalImageFile.getName());
                            r.copyToRealmOrUpdate(newPlace);
                        });
                        Picasso.get().load(finalImageFile).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(PlaceImage);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadimageViewImage() {
        File getImageDir = getExternalCacheDir();

        if (newPlace.getPath() != null) {
            File file = new File(getImageDir, newPlace.getPath());

            if (file.exists()) {
                Picasso.get()
                        .load(file)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(PlaceImage);
            } else {
                PlaceImage.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            PlaceImage.setImageResource(R.mipmap.ic_launcher);
        }
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
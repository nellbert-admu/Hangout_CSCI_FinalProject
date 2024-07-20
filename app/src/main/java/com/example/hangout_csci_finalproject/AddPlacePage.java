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
import java.io.IOException;
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
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView placeImage;
    private String imagePathTemp;

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

        // Create a temporary Place object
        createTempPlace(realm);

        findViewById(R.id.editButton).setOnClickListener(v -> addNewPlace(realm));
        findViewById(R.id.backButton).setOnClickListener(v -> {
            deleteTempPlace(realm);
            finish();
        });
    }

    private void createTempPlace(Realm realm) {
        realm.executeTransaction(r -> {
            Place tempPlace = r.createObject(Place.class, UUID.randomUUID().toString());
            tempPlace.setTemp(true); // Assuming there is a 'temp' field to indicate temporary objects
            imagePathTemp = null; // Reset imagePathTemp for the new session
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
        placeImage = findViewById(R.id.placeImage);

        placeImage.setOnClickListener(v -> takePic());
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


        realm.executeTransaction(r -> {
            Place place = r.where(Place.class).equalTo("temp", true).findFirst();
            if (place != null) {
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
                if (imagePathTemp != null) {
                    place.setPath(imagePathTemp);
                }
                place.setTemp(false); // Mark as no longer temporary
            }
        });

        long placeCount = realm.where(Place.class).count();
        long userCount = realm.where(User.class).count();
        Log.d("RealmCounts", "Places: " + placeCount + ", Users: " + userCount);

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void deleteTempPlace(Realm realm) {
        realm.executeTransaction(r -> {
            Place tempPlace = r.where(Place.class).equalTo("temp", true).findFirst();
            if (tempPlace != null) {
                tempPlace.deleteFromRealm();
            }
        });
    }

    private void takePic() {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
                try {
                    byte[] jpeg = data.getByteArrayExtra("rawJpeg");
                    if (jpeg != null) {
                        Log.d("AddPlacePage", "Received JPEG data");
                        File finalImageFile = saveFile(jpeg, UUID.randomUUID().toString() + ".jpeg");
                        Log.d("AddPlacePage", "Saving image to: " + finalImageFile.getAbsolutePath());

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(r -> {
                            Place tempPlace = r.where(Place.class).equalTo("temp", true).findFirst();
                            if (tempPlace != null) {
                                tempPlace.setPath(finalImageFile.getAbsolutePath());
                            }
                        });
                        realm.close();

                        updateImageView(finalImageFile.getAbsolutePath());

                    } else {
                        Log.d("AddPlacePage", "No JPEG data received");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private File saveFile(byte[] jpeg, String name) throws IOException {
        File getImageDir = getExternalCacheDir();
        File savedImage = new File(getImageDir, name);

        try (FileOutputStream fos = new FileOutputStream(savedImage)) {
            fos.write(jpeg);
        }
        return savedImage;
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
    public void onBackPressed() {
        Realm realm = Realm.getDefaultInstance();
        deleteTempPlace(realm);
        realm.close();
        super.onBackPressed();
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
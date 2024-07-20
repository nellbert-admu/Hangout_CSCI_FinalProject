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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.realm.Realm;

public class EditPlacePage extends AppCompatActivity {
    private EditText placeNameView, locationView, descriptionView;
    private ToggleButton toggleDining, toggleOutlets, toggleAircon, toggleQuiet, toggleRestrooms, toggleWifi;
    private RatingBar ratingBar;
    private String placeId;
    private Realm realm;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView placeImage;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_place);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        realm = Realm.getDefaultInstance();
        initViews();
        placeId = getIntent().getStringExtra("placeId");
        if (placeId != null) {
            loadPlaceData(placeId);
        }

        findViewById(R.id.editButton).setOnClickListener(v -> updatePlace());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private String tempImagePath;

    private void initViews() {
        placeImage = findViewById(R.id.placeImage);
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

        placeImage.setOnClickListener(v -> takePic());
    }

    private void loadPlaceData(String placeId) {
        place = realm.where(Place.class).equalTo("id", placeId).findFirst();
        if (place != null) {
            placeNameView.setText(place.getName());
            locationView.setText(place.getLocation());
            descriptionView.setText(place.getDescription());
            toggleDining.setChecked(place.isDining());
            toggleOutlets.setChecked(place.isOutlet());
            toggleAircon.setChecked(place.isAircon());
            toggleQuiet.setChecked(place.isQuiet());
            toggleRestrooms.setChecked(place.isRestroom());
            toggleWifi.setChecked(place.isWifi());
            ratingBar.setRating(place.getRating());
            updateImageView(place.getPath());
        }
    }

    private void updatePlace() {
        realm.executeTransaction(r -> {
            Place place = realm.where(Place.class).equalTo("id", placeId).findFirst();
            if (place != null) {
                place.setName(placeNameView.getText().toString());
                place.setLocation(locationView.getText().toString());
                place.setDescription(descriptionView.getText().toString());
                place.setDining(toggleDining.isChecked());
                place.setOutlet(toggleOutlets.isChecked());
                place.setAircon(toggleAircon.isChecked());
                place.setQuiet(toggleQuiet.isChecked());
                place.setRestroom(toggleRestrooms.isChecked());
                place.setWifi(toggleWifi.isChecked());
                place.setRating(ratingBar.getRating());
                if (tempImagePath != null && !tempImagePath.isEmpty()) {
                    place.setPath(tempImagePath);
                }
            }
        });
        Toast.makeText(this, "Place updated successfully", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
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

    private void takePic() {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
                try {
                    byte[] jpeg = data.getByteArrayExtra("rawJpeg");
                    if (jpeg != null) {
                        Log.d("EditPlacePage", "Received JPEG data");
                        File finalImageFile = saveFile(jpeg, UUID.randomUUID().toString() + ".jpeg");
                        Log.d("EditPlacePage", "Saving image to: " + finalImageFile.getAbsolutePath());

                        tempImagePath = finalImageFile.getAbsolutePath(); // Store the path temporarily
                        updateImageView(tempImagePath);


                    } else {
                        Log.d("EditPlacePage", "No JPEG data received");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
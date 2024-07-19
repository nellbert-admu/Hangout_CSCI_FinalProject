package com.example.hangout_csci_finalproject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for capturing and cropping an image.
 * This activity allows users to capture an image using the device's camera or select one from the gallery,
 * rotate the image, and crop it to a specified width and height.
 */
public class ImageActivity extends AppCompatActivity {
    public static int RESULT_CODE_IMAGE_TAKEN = 100; // Result code for image capture
    public static int MAX_WIDTH = 500; // Maximum width for the cropped image
    public static int MAX_HEIGHT = 500; // Maximum height for the cropped image
    private String fileAuthority; // Authority for file provider
    private CropImageView cropImageView; // View for cropping images
    private ImageButton capture; // Button for capturing images
    private ImageButton rotate; // Button for rotating images
    private ImageButton crop; // Button for cropping images
    private Button cancel; // Button for cancelling the operation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_capture_image); // Set the content view to the layout for this activity
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.card_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init(); // Initialize the activity components
    }

    /**
     * Initializes the activity components and sets up listeners for the buttons.
     */
    public void init() {
        fileAuthority = getResources().getString(R.string.fileAuthority); // Retrieve the file authority from resources

        cropImageView = findViewById(R.id.cropImageView); // Initialize the CropImageView

        // Setup capture button and its click listener
        capture = findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });

        // Setup rotate button and its click listener
        rotate = findViewById(R.id.rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotate();
            }
        });

        // Setup crop button and its click listener
        crop = findViewById(R.id.crop);
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crop();
            }
        });

        // Setup cancel button and its click listener
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    /**
     * Starts an activity for result to capture or select an image.
     */
    public void capture() {
        startActivityForResult(getPickImageChooserIntent(), 200);
    }

    /**
     * Rotates the image in the CropImageView by 90 degrees.
     */
    public void rotate() {
        cropImageView.rotateImage(90);
    }

    /**
     * Cancels the current operation and finishes the activity.
     */
    public void cancel() {
        finish();
    }

    /**
     * Crops the image displayed in the CropImageView and returns the result.
     */
    public void crop() {
        Bitmap cropped = cropImageView.getCroppedImage(MAX_WIDTH, MAX_HEIGHT); // Crop the image

        if (cropped != null) {
            System.out.println(cropped.getWidth() + " x " + cropped.getHeight()); // Log the dimensions of the cropped image
            cropImageView.setImageBitmap(cropped); // Display the cropped image

            // Encode the cropped image to JPEG format
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cropped.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // Return the cropped image data
            Intent i = new Intent();
            i.putExtra("rawJpeg", byteArray);
            setResult(RESULT_CODE_IMAGE_TAKEN, i);
            finish(); // Finish the activity
        }
    }

    /**
     * Handles the result from capturing or selecting an image.
     *
     * @param requestCode The request code passed in startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        The intent data returned by the child activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data); // Get the URI of the selected or captured image
            System.out.println("URI: " + imageUri.getPath()); // Log the URI
            cropImageView.setImageUriAsync(imageUri); // Set the image in the CropImageView
        }
    }

    /**
     * Creates a chooser intent to select the source to get an image from.
     * The source can be the device's camera or the gallery.
     *
     * @return An intent for selecting an image source.
     */
    public Intent getPickImageChooserIntent() {
        Uri outputFileUri = getCaptureImageOutputUri(); // Determine URI of camera image to save

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // Collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // Collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // Determine the main intent
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Creates a URI from a file, optionally using a FileProvider.
     *
     * @param file            The file to create the URI for.
     * @param useFileProvider Whether to use a FileProvider.
     * @return The URI for the file.
     */
    public Uri createUriFromFile(File file, boolean useFileProvider) {
        if (!useFileProvider) {
            return Uri.fromFile(file);
        } else {
            return FileProvider.getUriForFile(this, fileAuthority, file);
        }
    }

    /**
     * Gets the URI to the image received from capture by camera.
     *
     * @return The URI to the captured image.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImageDir = getExternalCacheDir();
        if (getImageDir != null) {
            outputFileUri = createUriFromFile(new File(getImageDir.getPath(), "pickImageResult.jpeg"), true);
        }
        return outputFileUri;
    }

    /**
     * Gets the URI of the selected image from the chooser intent.
     * Will return the correct URI for camera and gallery images.
     *
     * @param data The returned data of the activity result.
     * @return The URI of the selected or captured image.
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }
}
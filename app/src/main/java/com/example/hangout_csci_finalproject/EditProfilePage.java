package com.example.hangout_csci_finalproject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

/**
 * Activity for editing the user's profile.
 * This activity allows users to update their username and password.
 * It uses Realm for data persistence and SharedPreferences for session management.
 */
public class EditProfilePage extends AppCompatActivity {

    // UI components
    EditText UsernameInputEdit;
    EditText PasswordInputEdit;
    EditText ConfirmPasswordInputEdit;
    Button SaveButtonEdit;
    Button CancelButtonEdit;
    SharedPreferences prefs;
    Realm realm;

    /**
     * Sets up the UI and initializes components on activity creation.
     * Applies Edge-to-Edge configuration for immersive experience.
     * Initializes Realm and SharedPreferences to fetch and display the current user's information.
     *
     * @param savedInstanceState Contains data supplied in onSaveInstanceState(Bundle) if the activity is re-initialized.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        UsernameInputEdit = findViewById(R.id.usernameInputEdit);
        PasswordInputEdit = findViewById(R.id.passwordInputEdit);
        ConfirmPasswordInputEdit = findViewById(R.id.confirmPasswordInputEdit);

        SaveButtonEdit = findViewById(R.id.saveButtonEdit);
        SaveButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFunction();
            }
        });

        CancelButtonEdit = findViewById(R.id.cancelButtonEdit);
        CancelButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize Realm and SharedPreferences
        realm = Realm.getDefaultInstance();
        prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String UUID = prefs.getString("UUID", "");

        // Fetch and display current user's information
        User result = realm.where(User.class)
                .equalTo("uuid", UUID)
                .findFirst();

        if (result != null) {
            UsernameInputEdit.setText(result.getName());
        }
    }

    /**
     * Saves the updated user information to Realm and SharedPreferences.
     * Validates input fields before saving. Displays appropriate toast messages for validation errors or success.
     */
    public void saveFunction()
    {
        String username = UsernameInputEdit.getText().toString();
        String password = PasswordInputEdit.getText().toString();
        String confirmPassword = ConfirmPasswordInputEdit.getText().toString();

        // Input validation
        if (username.isEmpty()){
            Toast toast = Toast.makeText(this, "Name must not be blank", Toast.LENGTH_LONG);
            toast.show();
        }
        else if ((password.isEmpty())||(confirmPassword.isEmpty())){
            Toast toast = Toast.makeText(this, "Password/s must not be blank", Toast.LENGTH_LONG);
            toast.show();
        }
        else if (!password.equals(confirmPassword)){
            Toast toast = Toast.makeText(this, "Confirm password do not match", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            // Update user information in Realm and SharedPreferences
            prefs = getSharedPreferences("my_prefs",MODE_PRIVATE);
            String UUID = prefs.getString("UUID", "");

            realm = Realm.getDefaultInstance();
            User result = realm.where(User.class)
                    .equalTo("uuid", UUID)
                    .findFirst();

            // delete current user
            realm.beginTransaction();
            result.deleteFromRealm();
            realm.commitTransaction();

            // add the user
            User newItem =  new User();
            newItem.setName(username);
            newItem.setPassword(password);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UUID", newItem.getUuid());
            editor.apply();

            long count = 0;

            try
            {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(newItem);  // save
                realm.commitTransaction();

                Toast t = Toast.makeText(this, "User Edited.", Toast.LENGTH_LONG);
                t.show();
                finish();
            }
            catch(Exception e)
            {
                Toast t = Toast.makeText(this, "Error saving", Toast.LENGTH_LONG);
                t.show();
            }

        }
    }

    /**
     * Closes the Realm instance when the activity is destroyed to prevent memory leaks.
     */
    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
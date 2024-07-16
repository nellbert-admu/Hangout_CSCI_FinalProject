package com.example.hangout_csci_finalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import io.realm.RealmResults;

public class RegisterPage extends AppCompatActivity {
    public void goBack()
    {
        finish();
    }

    EditText UsernameInputRegister;
    EditText PasswordInputRegister;
    EditText ConfirmPasswordInput;
    Button SaveButton;
    Button CancelButtonRegister;
    Realm realm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views Initialization

        UsernameInputRegister = findViewById(R.id.UsernameInputRegister);
        PasswordInputRegister = findViewById(R.id.PasswordInputRegister);
        ConfirmPasswordInput = findViewById(R.id.ConfirmPasswordInput);

        SaveButton = findViewById(R.id.saveButton);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFunction();
            }
        });

        CancelButtonRegister = findViewById(R.id.CancelButtonRegister);
        CancelButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        // Realm Initialization

        realm = Realm.getDefaultInstance();
        RealmResults<User> result = realm.where(User.class).findAll();

        for (User i : result)
        {
            Log.d("RealmDemo", i.toString());
        }
    }

    public void saveFunction()
    {
        String username = UsernameInputRegister.getText().toString();
        String password = PasswordInputRegister.getText().toString();
        String confirmPassword = ConfirmPasswordInput.getText().toString();

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
        else
        {
            // Checks if username exists
            User result = realm.where(User.class)
                    .equalTo("name", username)
                    .findFirst();

            if (result!=null) // username already taken
            {
                Toast t = Toast.makeText(this, "Username already taken", Toast.LENGTH_LONG);
                t.show();
            }

            else // add the user
            {
                User newUser =  new User();
                newUser.setName(username);
                newUser.setPassword(password);

                long count = 0;

                try
                {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(newUser);  // save
                    realm.commitTransaction();

                    count = realm.where(User.class).count();

                    Toast t = Toast.makeText(this, "New User Saved. Total: "+count, Toast.LENGTH_LONG);
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
    }

    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
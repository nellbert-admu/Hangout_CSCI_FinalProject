package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class LoginPage extends AppCompatActivity {
    EditText usernameInput;
    EditText passwordInput;
    CheckBox RememberBox;
    Button SignInButton;
    Button RegisterButton;
    Button ClearButton;
    SharedPreferences prefs;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();

        // Remember Me Initialization

        prefs = getSharedPreferences("my_prefs",MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("rememberMe", false);

        // Views Initialization

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        RememberBox = findViewById(R.id.RememberBox);

        User result = realm.where(User.class)
                .equalTo("uuid", prefs.getString("UUID", ""))
                .findFirst();

        if ((result!=null)&&(rememberMe))
        {
            String usernameR = result.getName();
            String passwordR = result.getPassword();

            usernameInput.setText(usernameR);
            passwordInput.setText(passwordR);
            RememberBox.setChecked(true);
        }


        RememberBox = findViewById(R.id.RememberBox);

        SignInButton = findViewById(R.id.SignInButton);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        RegisterButton = findViewById(R.id.registerButton);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegister();
            }
        });

        ClearButton = findViewById(R.id.ClearButton);
        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
    }

    public void gotoRegister() // change to register
    {
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }
    public void SignIn()
    {
        // Check if user exists
        User result = realm.where(User.class)
                .equalTo("name", usernameInput.getText().toString())
                .findFirst();

        if (result!=null) // user exists
        {

            if ((result.getName().equals(usernameInput.getText().toString()))&&(result.getPassword().equals(passwordInput.getText().toString())))
            // user and password correct
            {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("UUID", result.getUuid());
                editor.putBoolean("rememberMe", RememberBox.isChecked());
                editor.apply();

                boolean rememberMe = prefs.getBoolean("rememberMe", false);
                Intent inside = new Intent(this, Home.class);
                if (rememberMe){
                    inside.putExtra("add", " You will be remembered!");
                }
                else {
                    inside.putExtra("add", "");
                }
                startActivity(inside);
            }
            else // user and password incorrect
            {
                Toast t = Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_LONG);
                t.show();
            }
        }
        else
        {
            Toast t = Toast.makeText(this, "No user found", Toast.LENGTH_LONG);
            t.show();
        }
    }

    public void clear()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Toast toast = Toast.makeText(this, "Cleared Preferences", Toast.LENGTH_LONG);
        toast.show();
    }

    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
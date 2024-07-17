package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class PlaceDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedetail);


    }

    public void onBackButtonClicked(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}

package com.example.hangout_csci_finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class ExploreResultsPage extends AppCompatActivity {
    private Realm realm;
    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploreresults);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String query = getIntent().getStringExtra("QUERY");
        boolean isDining = getIntent().getBooleanExtra("DINING", false);
        boolean isOutlet = getIntent().getBooleanExtra("OUTLET", false);
        boolean isAircon = getIntent().getBooleanExtra("AIRCON", false);
        boolean isQuiet = getIntent().getBooleanExtra("QUIET", false);
        boolean isRestroom = getIntent().getBooleanExtra("RESTROOM", false);
        boolean isWifi = getIntent().getBooleanExtra("WIFI", false);

        realm = Realm.getDefaultInstance();
        performSearch(query, isDining, isOutlet, isAircon, isQuiet, isRestroom, isWifi);
    }

    private void performSearch(String query, boolean isDining, boolean isOutlet, boolean isAircon, boolean isQuiet, boolean isRestroom, boolean isWifi) {
        RealmResults<Place> results = realm.where(Place.class)
                .contains("name", query, Case.INSENSITIVE)
                .equalTo("dining", isDining)
                .equalTo("outlet", isOutlet)
                .equalTo("aircon", isAircon)
                .equalTo("quiet", isQuiet)
                .equalTo("restroom", isRestroom)
                .equalTo("wifi", isWifi)
                .findAll();

        //placeAdapter = new PlaceAdapter(results);
        //recyclerView.setAdapter(placeAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}

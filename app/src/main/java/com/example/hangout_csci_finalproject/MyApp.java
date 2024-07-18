package com.example.hangout_csci_finalproject;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApp extends Application {

    public void onCreate()
    {
        super.onCreate();
        Realm.init(this); // for realm

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(2) // for schema changes
                .migration(new MyMigration()) //  migration implementation
                .build();

        Realm.setDefaultConfiguration(config);
    }
}
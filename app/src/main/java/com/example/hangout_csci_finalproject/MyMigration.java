package com.example.hangout_csci_finalproject;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.create("Place")
                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class)
                    .addField("hasDining", boolean.class)
                    .addField("hasOutlet", boolean.class)
                    .addField("hasAircon", boolean.class)
                    .addField("hasQuiet", boolean.class)
                    .addField("hasRestroom", boolean.class)
                    .addField("hasWifi", boolean.class);

            oldVersion++;
        }

        if (oldVersion == 1) {
            // Perform other migration steps if needed
        }
    }
}

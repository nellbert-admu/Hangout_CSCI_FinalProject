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
            schema.get("Place")
                    .removeField("hasDining")
                    .removeField("hasOutlet")
                    .removeField("hasAircon")
                    .removeField("hasQuiet")
                    .removeField("hasRestroom")
                    .removeField("hasWifi")
                    .addField("dining", boolean.class)
                    .addField("outlet", boolean.class)
                    .addField("aircon", boolean.class)
                    .addField("quiet", boolean.class)
                    .addField("restroom", boolean.class)
                    .addField("wifi", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 2) {
            schema.get("Place")
                    .addField("location", String.class)
                    .addField("description", String.class);
            oldVersion++;
        }
    }
}

package com.example.hangout_csci_finalproject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Place extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private boolean hasDining;
    private boolean hasOutlet;
    private boolean hasAircon;
    private boolean hasQuiet;
    private boolean hasRestroom;
    private boolean hasWifi;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isHasDining() { return hasDining; }
    public void setHasDining(boolean hasDining) { this.hasDining = hasDining; }

    public boolean isHasOutlet() { return hasOutlet; }
    public void setHasOutlet(boolean hasOutlet) { this.hasOutlet = hasOutlet; }

    public boolean isHasAircon() { return hasAircon; }
    public void setHasAircon(boolean hasAircon) { this.hasAircon = hasAircon; }

    public boolean isHasQuiet() { return hasQuiet; }
    public void setHasQuiet(boolean hasQuiet) { this.hasQuiet = hasQuiet; }

    public boolean isHasRestroom() { return hasRestroom; }
    public void setHasRestroom(boolean hasRestroom) { this.hasRestroom = hasRestroom; }

    public boolean isHasWifi() { return hasWifi; }
    public void setHasWifi(boolean hasWifi) { this.hasWifi = hasWifi; }
}

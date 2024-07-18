package com.example.hangout_csci_finalproject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Place extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;



    private boolean dining;
    private boolean outlet;
    private boolean aircon;
    private boolean quiet;
    private boolean restroom;
    private boolean wifi;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isDining() {
        return dining;
    }

    public void setDining(boolean dining) {
        this.dining = dining;
    }

    public boolean isOutlet() {
        return outlet;
    }

    public void setOutlet(boolean outlet) {
        this.outlet = outlet;
    }

    public boolean isAircon() {
        return aircon;
    }

    public void setAircon(boolean aircon) {
        this.aircon = aircon;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isRestroom() {
        return restroom;
    }

    public void setRestroom(boolean restroom) {
        this.restroom = restroom;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

}

package com.example.adme.Helpers;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class User {

    private String username;
    private String mode;
    private String status;
    private GeoPoint langLat;
    private String userId;

    public User() {
    }


    public User(String username, GeoPoint langLat, String mode, String status) {
        this.username = username;
        this.langLat = langLat;
        this.mode = mode;
        this.status = status;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GeoPoint getLangLat() {
        return langLat;
    }

    public void setLangLat(GeoPoint langLat) {
        this.langLat = langLat;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

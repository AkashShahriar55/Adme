package com.example.adme.Helpers;

public class User {

    private String username;
    private String latitude;
    private String longitude;
    private String mode;
    private String status;

    public User() {
    }


    public User(String username, String latitude, String longitude, String mode, String status) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mode = mode;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

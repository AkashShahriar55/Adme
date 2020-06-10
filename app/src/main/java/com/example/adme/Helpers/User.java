package com.example.adme.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class User implements Parcelable {

    private String username;
    private String mode;
    private String status;
    private String latitude;
    private String longitude;
    private String userId;

    public User() {
    }


    public User(String username, String mode, String status, String latitude, String longitude) {
        this.username = username;
        this.mode = mode;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    protected User(Parcel in) {
        username = in.readString();
        mode = in.readString();
        status = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        userId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(mode);
        dest.writeString(status);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(userId);
    }
}

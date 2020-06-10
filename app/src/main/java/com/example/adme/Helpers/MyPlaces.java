package com.example.adme.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class MyPlaces implements Parcelable {
    private String formattedAddress;
    private String name;
    private String latitude;
    private String longitude;

    public MyPlaces(String formattedAddress, String name, String latitude, String longitude) {
        this.formattedAddress = formattedAddress;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected MyPlaces(Parcel in) {
        formattedAddress = in.readString();
        name = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<MyPlaces> CREATOR = new Creator<MyPlaces>() {
        @Override
        public MyPlaces createFromParcel(Parcel in) {
            return new MyPlaces(in);
        }

        @Override
        public MyPlaces[] newArray(int size) {
            return new MyPlaces[size];
        }
    };

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formattedAddress);
        dest.writeString(name);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}

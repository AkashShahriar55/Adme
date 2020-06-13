package com.example.adme.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private String address;
    private String latitude;
    private String longitude;

    public Place(String address, String latitude, String longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Place(Parcel in) {
        address = in.readString();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}

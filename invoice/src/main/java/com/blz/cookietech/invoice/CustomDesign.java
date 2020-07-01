package com.blz.cookietech.invoice;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomDesign implements Parcelable {
    String button_color;

    public CustomDesign() {
    }

    protected CustomDesign(Parcel in) {
        button_color = in.readString();
    }

    public static final Creator<CustomDesign> CREATOR = new Creator<CustomDesign>() {
        @Override
        public CustomDesign createFromParcel(Parcel in) {
            return new CustomDesign(in);
        }

        @Override
        public CustomDesign[] newArray(int size) {
            return new CustomDesign[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(button_color);
    }

    public String getButton_color() {
        return button_color;
    }

    public void setButton_color(String button_color) {
        this.button_color = button_color;
    }
}

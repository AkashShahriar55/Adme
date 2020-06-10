package com.example.adme.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceProviderData implements Parcelable {

    private String income = "0";
    private String due = "0";
    private String completed = "0";
    private String requested = "0";
    private String Active = "0";

    public ServiceProviderData() {
    }

    public ServiceProviderData(String income, String due, String completed, String requested, String active) {
        this.income = income;
        this.due = due;
        this.completed = completed;
        this.requested = requested;
        Active = active;
    }

    protected ServiceProviderData(Parcel in) {
        income = in.readString();
        due = in.readString();
        completed = in.readString();
        requested = in.readString();
        Active = in.readString();
    }

    public static final Creator<ServiceProviderData> CREATOR = new Creator<ServiceProviderData>() {
        @Override
        public ServiceProviderData createFromParcel(Parcel in) {
            return new ServiceProviderData(in);
        }

        @Override
        public ServiceProviderData[] newArray(int size) {
            return new ServiceProviderData[size];
        }
    };

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getRequested() {
        return requested;
    }

    public void setRequested(String requested) {
        this.requested = requested;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(income);
        dest.writeString(due);
        dest.writeString(completed);
        dest.writeString(requested);
        dest.writeString(Active);
    }
}

package com.cookietech.adme.activities.ui.invoice;

import android.os.Parcel;
import android.os.Parcelable;

public class Services implements Parcelable {
    private String service_name;
    private double service_cost;
    private int service_quantity = 1;

    public Services(){}

    public Services(String service_name, double _cost) {
        this.service_name = service_name;
        service_cost = _cost;
    }

    public Services(String service_name, double _cost, int service_quantity) {
        this.service_name = service_name;
        service_cost = _cost;
        this.service_quantity = service_quantity;
    }

    protected Services(Parcel in) {
        service_name = in.readString();
        service_cost = in.readDouble();
        service_quantity = in.readInt();
    }

    public static final Creator<Services> CREATOR = new Creator<Services>() {
        @Override
        public Services createFromParcel(Parcel in) {
            return new Services(in);
        }

        @Override
        public Services[] newArray(int size) {
            return new Services[size];
        }
    };

    public String getService_name() {
        return service_name;
    }


    public double getService_cost() {
        return service_cost;
    }



    public int getService_quantity() {
        return service_quantity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service_name);
        dest.writeDouble(service_cost);
        dest.writeInt(service_quantity);
    }
}

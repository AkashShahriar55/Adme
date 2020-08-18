package com.cookietech.adme.activities.ui.invoice;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomerDetails implements Parcelable {

    private String due_date;
    private String customer_name;
    private String customer_ref;
    private String customer_phone;
    private String customer_email;
    private String customer_address;
    private String service_provider;
    private String service_category;
    private double vat = 0;
    private double discount = 0;
    private String service_id;
    private String appointment_id;

    public CustomerDetails(){}

    public CustomerDetails(String due_date, String customer_name, String customer_ref, String customer_phone, String customer_email, String customer_address, String service_provider, String service_category, double vat, double discount, String service_id, String appointment_id) {
        this.due_date = due_date;
        this.customer_name = customer_name;
        this.customer_ref = customer_ref;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        this.customer_address = customer_address;
        this.service_provider = service_provider;
        this.service_category = service_category;
        this.vat = vat;
        this.discount = discount;
        this.service_id = service_id;
        this.appointment_id = appointment_id;
    }

    public CustomerDetails(String due_date, String customer_name, String customer_phone, String customer_email, String customer_address, String service_provider, String service_category, double vat, double discount, String service_id, String appointment_id) {
        this.due_date = due_date;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        this.customer_address = customer_address;
        this.service_provider = service_provider;
        this.service_category = service_category;
        this.vat = vat;
        this.discount = discount;
        this.service_id = service_id;
        this.appointment_id = appointment_id;
    }

    public CustomerDetails(String due_date, String customer_name, String customer_phone, String customer_email, String customer_address, String service_provider, String service_category, String service_id, double vat, double discount) {
        this.due_date = due_date;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        this.customer_address = customer_address;
        this.service_provider = service_provider;
        this.service_category = service_category;
        this.service_id = service_id;
        this.vat = vat;
        this.discount = discount;
    }

    public CustomerDetails(String due_date, String customer_name, String customer_phone, String customer_email, String customer_address, String service_provider, String service_category,String service_id) {
        this.due_date = due_date;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        this.customer_address = customer_address;
        this.service_provider = service_provider;
        this.service_category = service_category;
        this.service_id = service_id;
    }



    protected CustomerDetails(Parcel in) {
        due_date = in.readString();
        customer_name = in.readString();
        customer_ref = in.readString();
        customer_phone = in.readString();
        customer_email = in.readString();
        customer_address = in.readString();
        service_provider = in.readString();
        service_category = in.readString();
        service_id = in.readString();
        appointment_id = in.readString();
        vat = in.readDouble();
        discount = in.readDouble();
    }

    public static final Creator<CustomerDetails> CREATOR = new Creator<CustomerDetails>() {
        @Override
        public CustomerDetails createFromParcel(Parcel in) {
            return new CustomerDetails(in);
        }

        @Override
        public CustomerDetails[] newArray(int size) {
            return new CustomerDetails[size];
        }
    };


    public String getDue_date() {
        return due_date;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public String getService_provider() {
        return service_provider;
    }

    public String getService_category() {
        return service_category;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public double getVat() {
        return vat;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public void setService_provider(String service_provider) {
        this.service_provider = service_provider;
    }

    public void setService_category(String service_category) {
        this.service_category = service_category;
    }

    public String getCustomer_ref() {
        return customer_ref;
    }

    public void setCustomer_ref(String customer_ref) {
        this.customer_ref = customer_ref;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(due_date);
        dest.writeString(customer_name);
        dest.writeString(customer_ref);
        dest.writeString(customer_phone);
        dest.writeString(customer_email);
        dest.writeString(customer_address);
        dest.writeString(service_provider);
        dest.writeString(service_category);
        dest.writeString(service_id);
        dest.writeString(appointment_id);
        dest.writeDouble(vat);
        dest.writeDouble(discount);
    }
}

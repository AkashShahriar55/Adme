package com.example.adme.Helpers;

import com.google.android.gms.maps.model.LatLng;

public class Appointment {
    private String clint_name;
    private String clint_phone;
    private String clint_ref;
    private String clint_text;
    private String clint_time;
    private String distance;
    private String price_needed;
    private String price_requested;
    private String service_provider_name;
    private String service_provider_ref;
    private String service_provider_text;
    private String service_provider_time;
    private String services;
    private String state;
    private MyPlaces clint_location;

    public Appointment(){}

    public Appointment(String clint_name, String clint_phone, String clint_ref, String clint_text, String clint_time, String distance, String price_needed, String price_requested, String service_provider_name, String service_provider_ref, String service_provider_text, String service_provider_time, String state, String services, MyPlaces clint_location) {
        this.clint_name = clint_name;
        this.clint_phone = clint_phone;
        this.clint_ref = clint_ref;
        this.clint_text = clint_text;
        this.clint_time = clint_time;
        this.distance = distance;
        this.price_needed = price_needed;
        this.price_requested = price_requested;
        this.service_provider_name = service_provider_name;
        this.service_provider_ref = service_provider_ref;
        this.service_provider_text = service_provider_text;
        this.service_provider_time = service_provider_time;
        this.state = state;
        this.services = services;
        this.clint_location = clint_location;
    }

    public LatLng getLatLng() {
        if(clint_location.getLatitude().equals("") || clint_location.getLongitude().equals("")){
            return null;
        } else {
            return new LatLng(Double.parseDouble(clint_location.getLatitude()), Double.parseDouble(clint_location.getLongitude()));
        }
    }

    public String getClint_name() {
        return clint_name;
    }

    public void setClint_name(String clint_name) {
        this.clint_name = clint_name;
    }

    public String getClint_phone() {
        return clint_phone;
    }

    public void setClint_phone(String clint_phone) {
        this.clint_phone = clint_phone;
    }

    public String getClint_ref() {
        return clint_ref;
    }

    public void setClint_ref(String clint_ref) {
        this.clint_ref = clint_ref;
    }

    public String getClint_text() {
        return clint_text;
    }

    public void setClint_text(String clint_text) {
        this.clint_text = clint_text;
    }

    public String getClint_time() {
        return clint_time;
    }

    public void setClint_time(String clint_time) {
        this.clint_time = clint_time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPrice_needed() {
        return price_needed;
    }

    public void setPrice_needed(String price_needed) {
        this.price_needed = price_needed;
    }

    public String getPrice_requested() {
        return price_requested;
    }

    public void setPrice_requested(String price_requested) {
        this.price_requested = price_requested;
    }

    public String getService_provider_name() {
        return service_provider_name;
    }

    public void setService_provider_name(String service_provider_name) {
        this.service_provider_name = service_provider_name;
    }

    public String getService_provider_ref() {
        return service_provider_ref;
    }

    public void setService_provider_ref(String service_provider_ref) {
        this.service_provider_ref = service_provider_ref;
    }

    public String getService_provider_text() {
        return service_provider_text;
    }

    public void setService_provider_text(String service_provider_text) {
        this.service_provider_text = service_provider_text;
    }

    public String getService_provider_time() {
        return service_provider_time;
    }

    public void setService_provider_time(String service_provider_time) {
        this.service_provider_time = service_provider_time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public MyPlaces getClint_location() {
        return clint_location;
    }

    public void setClint_location(MyPlaces clint_location) {
        this.clint_location = clint_location;
    }
}

package com.example.adme.Helpers;

public class SelectServiceItem {
    private String service_title;
    private String service_description;
    private String service_price;

    public SelectServiceItem() {}

    public SelectServiceItem(String service_title, String service_details, String service_price) {
        this.service_title = service_title;
        this.service_description = service_details;
        this.service_price = service_price;
    }

    public String getService_title() {
        return service_title;
    }

    public void setService_title(String service_title) {
        this.service_title = service_title;
    }

    public String getService_details() {
        return service_description;
    }

    public void setService_details(String service_details) {
        this.service_description = service_details;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }
}

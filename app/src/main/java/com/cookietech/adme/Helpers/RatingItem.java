package com.cookietech.adme.Helpers;

public class RatingItem {
    private String clintName;
    private String time;
    private String comment;
    private String totalPrice;
    private String invoiceID;
    private float rating;

    public RatingItem(){}

    public RatingItem(String clintName, String time, String comment, String totalPrice, String invoiceID, float rating) {
        this.clintName = clintName;
        this.time = time;
        this.comment = comment;
        this.totalPrice = totalPrice;
        this.invoiceID = invoiceID;
        this.rating = rating;
    }

    public String getClintName() {
        return clintName;
    }

    public void setClintName(String clintName) {
        this.clintName = clintName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

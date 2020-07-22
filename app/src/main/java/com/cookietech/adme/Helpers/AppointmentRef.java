package com.cookietech.adme.Helpers;

public class AppointmentRef {
    private String reference;
    private String mode;

    public AppointmentRef(){}

    public AppointmentRef(String reference, String mode) {
        this.reference = reference;
        this.mode = mode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}

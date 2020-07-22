package com.cookietech.adme.Helpers;

public class Notification {
    private String text;
    private String time;
    private String type;
    private String reference;
    private String mode;
    private boolean isSeen;

    public Notification(){}

    public Notification(String text, String time, String type, String reference, String mode, boolean isSeen) {
        this.text = text;
        this.time = time;
        this.type = type;
        this.reference = reference;
        this.mode = mode;
        this.isSeen = isSeen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}

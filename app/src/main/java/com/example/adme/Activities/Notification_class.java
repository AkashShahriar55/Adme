package com.example.adme.Activities;

public class Notification_class {
    String profilePicURL, notificaiton_text,notificaion_time;

    Notification_class(String profilePicURL, String notificaion_time, String notificaiton_text){
        this.profilePicURL = profilePicURL;
        this.notificaion_time = notificaion_time;
        this.notificaiton_text=notificaiton_text;
    }

    public void setNotificaion_time(String notificaion_time) {
        this.notificaion_time = notificaion_time;
    }

    public void setNotificaiton_text(String notificaiton_text) {
        this.notificaiton_text = notificaiton_text;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getNotificaion_time() {
        return notificaion_time;
    }

    public String getNotificaiton_text() {
        return notificaiton_text;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

}

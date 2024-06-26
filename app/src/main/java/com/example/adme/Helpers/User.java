package com.example.adme.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.adme.Architecture.FirebaseUtilClass;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_LATITUDE;
import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE;

public class User implements Parcelable {

    private String user_name;
    private String profile_image_url;
    private String joined;
    private String mode;
    private String status;
    private String mUserId;
    private Map<String,String> location = new HashMap<>();
    private Map<String,String> contacts = new HashMap<>();
    private Map<String,String> service_provider_info = new HashMap<>();
    private Map<String,String> client_info = new HashMap<>();
    private List<Map<String,String>> service_reference = new ArrayList<>();
    private String device_token;


    public User() {
    }

    public User(String mUsername, String mEmail, String phone, String profile_image_url, String mJoined, String mUserId, String device_token) {
        this.user_name = mUsername;
        this.joined = mJoined;
        this.mUserId = mUserId;
        this.profile_image_url = profile_image_url;
        this.device_token = device_token;

        mode = FirebaseUtilClass.MODE_CLIENT;
        status = FirebaseUtilClass.STATUS_ONLINE;

        service_provider_info.put(FirebaseUtilClass.ENTRY_INCOME_TODAY,"0");
        service_provider_info.put(FirebaseUtilClass.ENTRY_DUE,"0");
        service_provider_info.put(FirebaseUtilClass.ENTRY_PRESSED_TODAY,"0");
        service_provider_info.put(FirebaseUtilClass.ENTRY_REQUESTED_TODAY,"0");
        service_provider_info.put(FirebaseUtilClass.ENTRY_COMPLETED_TODAY,"0");
        service_provider_info.put(FirebaseUtilClass.ENTRY_INCOME_TOTAL,"0");
        service_provider_info.put(FirebaseUtilClass.ENTRY_MONTHLY_SUBSCRIPTION,FirebaseUtilClass.ENTRY_MONTHLY_SUBSCRIPTION_PAID);


        contacts.put(FirebaseUtilClass.ENTRY_PHONE_NO,phone);
        contacts.put(FirebaseUtilClass.ENTRY_PHONE_NO_PRIVACY,FirebaseUtilClass.ENTRY_PRIVACY_PUBLIC);
        contacts.put(FirebaseUtilClass.ENTRY_EMAIL,mEmail);
        contacts.put(FirebaseUtilClass.ENTRY_EMAIL_PRIVACY,FirebaseUtilClass.ENTRY_PRIVACY_PUBLIC);


    }

    protected User(Parcel in) {
        user_name = in.readString();
        mode = in.readString();
        status = in.readString();
        mUserId = in.readString();
        profile_image_url = in.readString();
        device_token = in.readString();


        int service_count = in.readInt();
        for (int i = 0; i < service_count; i++) {
            Map<String,String> service_reference_info = new HashMap<>();
            service_reference_info.put(FirebaseUtilClass.ENTRY_SERVICE_CATEGORY,in.readString());
            service_reference_info.put(FirebaseUtilClass.ENTRY_MAIN_SERVICE_DESCRIPTION,in.readString());
            service_reference_info.put(FirebaseUtilClass.ENTRY_SERVICE_RATING,in.readString());
            service_reference_info.put(FirebaseUtilClass.ENTRY_SERVICE_REVIEWS,in.readString());
            service_reference_info.put(FirebaseUtilClass.ENTRY_SERVICE_REFERENCE,in.readString());
            service_reference.add(service_reference_info);
        }


        //write service provider info in parcelable
        int service_info_size = in.readInt();
        for (int i = 0; i < service_info_size; i++) {
            String key = in.readString();
            String value = in.readString();
            service_provider_info.put(key,value);
        }

        int contacts_size = in.readInt();
        for (int i = 0; i < contacts_size; i++) {
            String key = in.readString();
            String value = in.readString();
            contacts.put(key,value);
        }

        int location_size = in.readInt();
        for (int i = 0; i < location_size; i++) {
            String key = in.readString();
            String value = in.readString();
            location.put(key,value);
        }

        int client_info_size = in.readInt();
        for (int i = 0; i < client_info_size; i++) {
            String key = in.readString();
            String value = in.readString();
            client_info.put(key,value);
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public List<Map<String,String>> getService_reference() {
        return service_reference;
    }

    public void setService_reference(List<Map<String,String>> service_reference) {
        this.service_reference = service_reference;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    @Exclude
    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    @Exclude
    public LatLng getLatLng() {
        if(location.get(ENTRY_LOCATION_LATITUDE).equals("") || location.get(ENTRY_LOCATION_LONGITUDE).equals("")){
            return null;
        } else {
            return new LatLng(Double.parseDouble(location.get(ENTRY_LOCATION_LATITUDE)), Double.parseDouble(location.get(ENTRY_LOCATION_LONGITUDE)));
        }
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }

    public Map<String, String> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, String> contacts) {
        this.contacts = contacts;
    }

    public Map<String, String> getService_provider_info() {
        return service_provider_info;
    }

    public void setService_provider_info(Map<String, String> service_provider_info) {
        this.service_provider_info = service_provider_info;
    }

    public Map<String, String> getClient_info() {
        return client_info;
    }

    public void setClient_info(Map<String, String> client_info) {
        this.client_info = client_info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(mode);
        dest.writeString(status);
        dest.writeString(mUserId);
        dest.writeString(profile_image_url);
        dest.writeString(device_token);

        int service_count = service_reference.size();
        dest.writeInt(service_count);
        for(Map<String,String> reference:service_reference){
            dest.writeString(reference.get(FirebaseUtilClass.ENTRY_SERVICE_CATEGORY));
            dest.writeString(reference.get(FirebaseUtilClass.ENTRY_MAIN_SERVICE_DESCRIPTION));
            dest.writeString(reference.get(FirebaseUtilClass.ENTRY_SERVICE_RATING));
            dest.writeString(reference.get(FirebaseUtilClass.ENTRY_SERVICE_REVIEWS));
            dest.writeString(reference.get(FirebaseUtilClass.ENTRY_SERVICE_REFERENCE));
        }

        int service_info_size = service_provider_info.size();
        int contacts_size = contacts.size();
        int location_size = location.size();
        int client_info_size = client_info.size();

        //write service provider info in parcelable
        dest.writeInt(service_info_size);
        for(Map.Entry<String,String> map: service_provider_info.entrySet() ){
            dest.writeString( map.getKey());
            dest.writeString(map.getValue());
        }

        dest.writeInt(contacts_size);
        for(Map.Entry<String,String> map: contacts.entrySet() ){
            dest.writeString( map.getKey());
            dest.writeString(map.getValue());
        }

        dest.writeInt(location_size);
        for(Map.Entry<String,String> map: location.entrySet() ){
            dest.writeString( map.getKey());
            dest.writeString(map.getValue());
        }

        dest.writeInt(client_info_size);
        for(Map.Entry<String,String> map: client_info.entrySet() ){
            dest.writeString( map.getKey());
            dest.writeString(map.getValue());
        }



    }
}

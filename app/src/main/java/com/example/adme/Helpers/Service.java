package com.example.adme.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Service implements Parcelable {
    private String category;
    private String rating;
    private String reviews;
    private String working_hour;
    private Map<String,String> location = new HashMap<>();
    private List<String> feature_images = new ArrayList<>();
    private List<Map<String,String>> services = new ArrayList<>();
    private List<String> tags;
    private Map<String,String> user_info = new HashMap<>();
    private String mServiceId;

    public Service() {
    }

    public Service(String category, String working_hour, Map<String, String> location, List<String> feature_images, List<Map<String, String>> services, Map<String, String> user_info,String mServiceId) {
        this.category = category;
        this.working_hour = working_hour;
        this.location = location;
        this.feature_images = feature_images;
        this.services = services;
        this.user_info = user_info;

        String[] tokens = category.split("\\s+");
        this.tags.addAll(Arrays.asList(tokens));
        tokens = location.get(FirebaseUtilClass.ENTRY_LOCATION_ADDRESS).split("\\s+");
        this.tags.addAll(Arrays.asList(tokens));
        for(Map<String,String> service:services){
            tokens = service.get(FirebaseUtilClass.ENTRY_SERVICE_TITLE).split("\\s+");
            tags.addAll(Arrays.asList(tokens));
        }


        this.rating = "0";
        this.reviews = "0";
        this.mServiceId = mServiceId;
    }

    protected Service(Parcel in) {
        category = in.readString();
        rating = in.readString();
        reviews = in.readString();
        working_hour = in.readString();
        feature_images = in.createStringArrayList();
        tags = in.createStringArrayList();
        mServiceId = in.readString();

        int location_size = in.readInt();
        for (int i = 0; i < location_size; i++) {
            String key = in.readString();
            String value = in.readString();
            location.put(key,value);
        }

        int user_info_size = in.readInt();
        for (int i = 0; i < user_info_size; i++) {
            String key = in.readString();
            String value = in.readString();
            user_info.put(key,value);
        }

        int services_size = in.readInt();
        for (int i = 0; i < services_size; i++) {
            Map<String,String> service = new HashMap<>();
            service.put(FirebaseUtilClass.ENTRY_SERVICE_TITLE,in.readString());
            service.put(FirebaseUtilClass.ENTRY_SERVICE_DESCRIPTION,in.readString());
            service.put(FirebaseUtilClass.ENTRY_SERVICE_PRICE,in.readString());
            services.add(service);
        }
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getWorking_hour() {
        return working_hour;
    }

    public void setWorking_hour(String working_hour) {
        this.working_hour = working_hour;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }

    public List<String> getFeature_images() {
        return feature_images;
    }

    public void setFeature_images(List<String> feature_images) {
        this.feature_images = feature_images;
    }

    public List<Map<String, String>> getServices() {
        return services;
    }

    public void setServices(List<Map<String, String>> services) {
        this.services = services;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, String> getUser_info() {
        return user_info;
    }

    public void setUser_info(Map<String, String> user_info) {
        this.user_info = user_info;
    }

    @Exclude
    public String getmServiceId() {
        return mServiceId;
    }

    public void setmServiceId(String mServiceId) {
        this.mServiceId = mServiceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(rating);
        dest.writeString(reviews);
        dest.writeString(working_hour);
        dest.writeStringList(feature_images);
        dest.writeStringList(tags);
        dest.writeString(mServiceId);

        int location_size = location.size();
        dest.writeInt(location_size);
        for(Map.Entry<String,String> map: location.entrySet() ){
            dest.writeString( map.getKey());
            dest.writeString(map.getValue());
        }

        int user_info_size = user_info.size();
        dest.writeInt(user_info_size);
        for(Map.Entry<String,String> map: user_info.entrySet() ){
            dest.writeString( map.getKey());
            dest.writeString(map.getValue());
        }

        int services_size = services.size();
        dest.writeInt(services_size);
        for(Map<String,String> service:services){
            dest.writeString(service.get(FirebaseUtilClass.ENTRY_SERVICE_TITLE));
            dest.writeString(service.get(FirebaseUtilClass.ENTRY_SERVICE_DESCRIPTION));
            dest.writeString(service.get(FirebaseUtilClass.ENTRY_SERVICE_PRICE));
        }
    }
}

package com.cookietech.adme.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service implements Parcelable {
    private String category;
    private String rating;
    private String reviews;
    private String working_hour;
    private String user_name;
    private String pic_url;
    private String user_ref;
    private String description;
    private Map<String,String> location = new HashMap<>();
    private List<String> feature_images = new ArrayList<>();
    private List<Map<String,String>> services = new ArrayList<>();
    private List<String> tags;
    private String status;
    private String mServiceId;


    public Service() {
        this.rating = "0";
        this.reviews = "0";
    }


    public Service(String category,String description, String working_hour, String user_name, String pic_url, String user_ref, Map<String, String> location, List<String> feature_images, List<Map<String, String>> services, List<String> tags, String status) {
        this.category = category;
        this.description = description;
        this.rating = "0";
        this.reviews = "0";
        this.working_hour = working_hour;
        this.user_name = user_name;
        this.pic_url = pic_url;
        this.user_ref = user_ref;
        this.location = location;
        this.feature_images = feature_images;
        this.services = services;
        this.tags = tags;
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getUser_ref() {
        return user_ref;
    }

    public void setUser_ref(String user_ref) {
        this.user_ref = user_ref;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public String getmServiceId() {
        return mServiceId;
    }

    public void setmServiceId(String mServiceId) {
        this.mServiceId = mServiceId;
    }



    protected Service(Parcel in) {
        category = in.readString();
        description = in.readString();
        rating = in.readString();
        reviews = in.readString();
        working_hour = in.readString();
        feature_images = in.createStringArrayList();
        tags = in.createStringArrayList();
        user_name = in.readString();
        pic_url = in.readString();
        user_ref = in.readString();
        status = in.readString();
        mServiceId = in.readString();

        int location_size = in.readInt();
        for (int i = 0; i < location_size; i++) {
            String key = in.readString();
            String value = in.readString();
            location.put(key,value);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(rating);
        dest.writeString(reviews);
        dest.writeString(working_hour);
        dest.writeStringList(feature_images);
        dest.writeStringList(tags);
        dest.writeString(user_name);
        dest.writeString(pic_url);
        dest.writeString(user_ref);
        dest.writeString(status);
        dest.writeString(mServiceId);

        int location_size = location.size();
        dest.writeInt(location_size);
        for(Map.Entry<String,String> map: location.entrySet() ){
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

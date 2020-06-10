package com.example.adme.Activities.ui.home;

import java.util.List;
import java.util.Map;

public class ServiceProvider {
    private String user_name;
    private String work_done;
    private String rating;
    private String short_dis;
    private String min_fee;
    private String max_fee;
    private String pic_url;
    private String user_ref;
    private String lat;
    private String lng;
    private String category;
    private String working_hour;
    private List<String> tag;
    private Map<String,String> services;
    private boolean is_online;

    public ServiceProvider() {
    }

    public ServiceProvider(String user_name, String work_done, String rating, String short_dis, String min_fee, String max_fee, String pic_url, String user_ref, String lat, String lng) {
        this.user_name = user_name;
        this.work_done = work_done;
        this.rating = rating;
        this.short_dis = short_dis;
        this.min_fee = min_fee;
        this.max_fee = max_fee;
        this.pic_url = pic_url;
        this.user_ref = user_ref;
        this.lat = lat;
        this.lng = lng;
    }

    public ServiceProvider(String user_name, String work_done, String rating, String short_dis, String min_fee, String max_fee, String pic_url, String user_ref, String lat, String lng, String category, String working_hour, List<String> tag, Map<String, String> services, boolean is_online) {
        this.user_name = user_name;
        this.work_done = work_done;
        this.rating = rating;
        this.short_dis = short_dis;
        this.min_fee = min_fee;
        this.max_fee = max_fee;
        this.pic_url = pic_url;
        this.user_ref = user_ref;
        this.lat = lat;
        this.lng = lng;
        this.category = category;
        this.working_hour = working_hour;
        this.tag = tag;
        this.services = services;
        this.is_online = is_online;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getWork_done() {
        return work_done;
    }

    public void setWork_done(String work_done) {
        this.work_done = work_done;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getShort_dis() {
        return short_dis;
    }

    public void setShort_dis(String short_dis) {
        this.short_dis = short_dis;
    }

    public String getMin_fee() {
        return min_fee;
    }

    public void setMin_fee(String min_fee) {
        this.min_fee = min_fee;
    }

    public String getMax_fee() {
        return max_fee;
    }

    public void setMax_fee(String max_fee) {
        this.max_fee = max_fee;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWorking_hour() {
        return working_hour;
    }

    public void setWorking_hour(String working_hour) {
        this.working_hour = working_hour;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public Map<String, String> getServices() {
        return services;
    }

    public void setServices(Map<String, String> services) {
        this.services = services;
    }

    public boolean isIs_online() {
        return is_online;
    }

    public void setIs_online(boolean is_online) {
        this.is_online = is_online;
    }
}

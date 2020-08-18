package com.cookietech.adme.Architecture;

public class SharedPreferenceManager {
    private static  SharedPreferenceManager instance = new SharedPreferenceManager();

    public static SharedPreferenceManager getInstance() {
        if(instance == null)
            instance = new SharedPreferenceManager();
        return instance;
    }
}

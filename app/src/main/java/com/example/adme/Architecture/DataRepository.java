package com.example.adme.Architecture;

import androidx.lifecycle.MutableLiveData;

import com.example.adme.Helpers.User;

public class DataRepository {
    private MutableLiveData<User> userData;
    private FirebaseUtilClass firebaseUtilClass;

    public DataRepository() {
        firebaseUtilClass = new FirebaseUtilClass();
        userData = firebaseUtilClass.getUserData();
    }


    public void updateUserName(String name)
    {
        firebaseUtilClass.updateUserName(name);
    }
    public MutableLiveData<User> getUserData() {
        return userData;
    }
}

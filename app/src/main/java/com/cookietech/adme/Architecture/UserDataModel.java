package com.cookietech.adme.Architecture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cookietech.adme.Helpers.User;

public class UserDataModel extends ViewModel {
    private MutableLiveData<User> currentUser;
    private FirebaseUtilClass firebaseUtilClass;

    public UserDataModel() {
        firebaseUtilClass = new FirebaseUtilClass();
        currentUser = firebaseUtilClass.getUserData();
    }

    // get current user data
    public LiveData<User> getCurrentUser(){
        return currentUser;
    }

    // set current user data
    public void setCurrentUser(User user){
        currentUser.setValue(user);
    }
}

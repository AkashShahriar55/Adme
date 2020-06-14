package com.example.adme.Architecture;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adme.Helpers.User;

public class UserDataModel extends ViewModel {
    private MutableLiveData<User>  currentUser;

    public MutableLiveData<User> getCurrentUser(){
        if(currentUser== null){
            currentUser = new MutableLiveData<User>();
        }
        return currentUser;
    }
}

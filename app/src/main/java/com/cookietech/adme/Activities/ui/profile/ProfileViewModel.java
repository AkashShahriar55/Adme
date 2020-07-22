package com.cookietech.adme.Activities.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cookietech.adme.Architecture.DataRepository;
import com.cookietech.adme.Helpers.User;

public class ProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<User> userData;
    private DataRepository repository;
    public ProfileViewModel() {
        repository = new DataRepository();
        userData = repository.getUserData();
    }

    public void updateUserName(String name)
    {
        repository.updateUserName(name);
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }
}

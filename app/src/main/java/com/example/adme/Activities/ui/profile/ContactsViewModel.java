package com.example.adme.Activities.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adme.Architecture.DataRepository;
import com.example.adme.Helpers.User;

public class ContactsViewModel extends ViewModel {
    private MutableLiveData<User> userData;
    private DataRepository repository;
    public ContactsViewModel() {
        repository = new DataRepository();
        userData = repository.getUserData();
    }

    public void updatePhoneNumberMode(String mode) {repository.updatePhoneNumberMode(mode);}
    public void deletePhoneNumber(){repository.deletePhoneNumber();}

    public MutableLiveData<User> getUserData() {
        return userData;
    }
}

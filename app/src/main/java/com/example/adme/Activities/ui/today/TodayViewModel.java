package com.example.adme.Activities.ui.today;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adme.Architecture.DataRepository;
import com.example.adme.Helpers.User;

public class TodayViewModel extends ViewModel {

    private MutableLiveData<User> userData;
    private DataRepository repository;
    public TodayViewModel() {
        repository = new DataRepository();
        userData = repository.getUserData();
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public void updateStatus( String status){
        repository.updateStatus(status);
    }
}
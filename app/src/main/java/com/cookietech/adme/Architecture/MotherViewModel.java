package com.cookietech.adme.Architecture;

import android.util.Log;

import androidx.lifecycle.ViewModel;

public class MotherViewModel extends ViewModel {
    DataRepository repository;
    private boolean isAlreadyLoggedIn;

    public MotherViewModel() {
        repository = DataRepository.getInstance();
        Log.d("akash_debug", "MotherViewModel: constructed");
        initializeSharedData();
    }

    private void initializeSharedData() {
        isAlreadyLoggedIn = repository.isAlreadyLoggedIn();
    }

    public boolean isAlreadyLoggedIn() {
        return isAlreadyLoggedIn;
    }

    public void setAlreadyLoggedIn(boolean alreadyLoggedIn) {
        isAlreadyLoggedIn = alreadyLoggedIn;
    }
}

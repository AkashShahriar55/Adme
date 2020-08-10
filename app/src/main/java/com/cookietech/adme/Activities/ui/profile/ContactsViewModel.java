package com.cookietech.adme.Activities.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cookietech.adme.Architecture.DataRepository;
import com.cookietech.adme.Helpers.User;

public class ContactsViewModel extends ViewModel {
    private MutableLiveData<User> userData;
    private DataRepository repository;
    public ContactsViewModel() {
        repository = DataRepository.getInstance();
        userData = repository.getUserData();
    }

    public void updatePhoneNumberMode(String mode) {repository.updatePhoneNumberMode(mode);}
    public void deletePhoneNumber(){repository.deletePhoneNumber();}
    public void addPhoneNumber(String number){repository.addPhoneNumber(number);}
    public void updateEmailMode(String mode){repository.updateEmailMode(mode);}
    public void deleteEmail(){repository.deleteEmail();}
    public void addEmail(String email){repository.addEmail(email);}

    public MutableLiveData<User> getUserData() {
        return userData;
    }
}

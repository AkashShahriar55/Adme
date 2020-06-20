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

    //for profile update
    public void updateUserName(String name)
    {
        firebaseUtilClass.updateUserName(name);
    }
    public void updatePhoneNumberMode(String mode){firebaseUtilClass.updatePhoneNumberMode(mode);}
    public void deletePhoneNumber(){firebaseUtilClass.deletePhoneNumber();}
    public void addPhoneNumber(String number){firebaseUtilClass.addPhoneNumber(number);}
    public void updateEmailMode(String mode){firebaseUtilClass.updateEmailMode(mode);}
    public void deleteEmail(){firebaseUtilClass.deleteEmail();}
    public void addEmail(String email){firebaseUtilClass.addEmail(email);}

    // for profile update ends

    public MutableLiveData<User> getUserData() {
        return userData;
    }
}

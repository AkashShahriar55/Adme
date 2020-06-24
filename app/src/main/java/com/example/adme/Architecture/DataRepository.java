package com.example.adme.Architecture;

import androidx.lifecycle.MutableLiveData;

import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;

public class DataRepository {
    private MutableLiveData<User> userData;
    private MutableLiveData<Service> serviceData;
    private FirebaseUtilClass firebaseUtilClass;

    public DataRepository() {
        firebaseUtilClass = new FirebaseUtilClass();
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

    //For status update
    public void updateStatus( String status){
        firebaseUtilClass.updateStatus(status);
    }

    // for profile update ends

    public MutableLiveData<User> getUserData() {
        userData = firebaseUtilClass.getUserData();
        return userData;
    }

    public MutableLiveData<Service> getServiceData(String service_id){
        serviceData = firebaseUtilClass.getServiceData(service_id);
        return  serviceData;
    }
}

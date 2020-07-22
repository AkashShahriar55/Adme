package com.cookietech.adme.Architecture;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.cookietech.adme.Helpers.Service;
import com.cookietech.adme.Helpers.User;

import java.util.List;

public class DataRepository {
    private MutableLiveData<User> userData;
    private MutableLiveData<Service> serviceData;
    private MutableLiveData<List<Service>> allServiceList;
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
        Log.d("akash_debug", "getUserData: ");
        userData = firebaseUtilClass.getUserData();
        return userData;
    }

    public MutableLiveData<Service> getServiceData(String service_id){
        serviceData = firebaseUtilClass.getServiceData(service_id);
        return  serviceData;
    }

    public MutableLiveData<List<Service>> getAllServiceList(){
        allServiceList = firebaseUtilClass.getServices();
        return allServiceList;
    }
}

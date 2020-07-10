package com.example.adme.Activities.ui.home;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.adme.Architecture.DataRepository;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private MutableLiveData<List<Service>> queryResult =  new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Service>> localizedServices = new MutableLiveData<>();
    private MutableLiveData<List<Service>> allServiceList = new MutableLiveData<>();
    private DataRepository repository = new DataRepository();
    private MutableLiveData<User> userData = new MutableLiveData<>();

    public HomeViewModel() {
//        queryResult =  new MutableLiveData<>();
//        List<Service> serviceProvidersList = new ArrayList<>();
//        queryResult.setValue(serviceProvidersList);
        userData = repository.getUserData();
        allServiceList = repository.getAllServiceList();
    }

    public void setQueryValue(List<String> queryArray) {
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_Service_list")
                .whereArrayContainsAny("tags", queryArray)
//                .orderBy("tags").startAt(queryArray.get(0)).endAt(queryArray.get(0)+"\uf8ff")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        List<Service> serviceProvidersList = new ArrayList<>();
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                        } else {
                            for (QueryDocumentSnapshot doc : value) {
                                Service sv = doc.toObject(Service.class);
                                sv.setmServiceId(doc.getId());
                                serviceProvidersList.add(sv);
                                Log.d(TAG, "onEvent setQueryValue: "+sv.getUser_name());
                            }
                            queryResult.postValue(serviceProvidersList);
                        }
                    }
                });
    }

    public LiveData<List<Service>> getQueryResult() {
        return queryResult;
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public MutableLiveData<List<Service>> getAllServiceList() {
        return allServiceList;
    }
}
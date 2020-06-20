package com.example.adme.Activities.ui.home;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.adme.Helpers.Service;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private MutableLiveData<List<Service>> queryResult =  new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeViewModel() {
//        queryResult =  new MutableLiveData<>();
//        List<Service> serviceProvidersList = new ArrayList<>();
//        queryResult.setValue(serviceProvidersList);
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

}
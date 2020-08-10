package com.cookietech.adme.Activities.ui.income;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cookietech.adme.Architecture.DataRepository;
import com.cookietech.adme.Helpers.RatingItem;
import com.cookietech.adme.Helpers.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IncomeViewModel extends ViewModel {
    private static final String TAG = "IncomeViewModel";
    private FirebaseFirestore db;
    private MutableLiveData<List<RatingItem>> rating_list;
    private DataRepository repository;
    private MutableLiveData<User> userData;

    public IncomeViewModel() {
        db = FirebaseFirestore.getInstance();
        repository = DataRepository.getInstance();
        userData = repository.getUserData();
        rating_list =  new MutableLiveData<>();
    }

    public void setRating_list(String serviceID) {
        db.collection("/Adme_Service_list/"+serviceID+"/review_list")
                .orderBy("rating", Query.Direction.DESCENDING).limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        List<RatingItem> ratingItems = new ArrayList<>();
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                        } else {
                            for (QueryDocumentSnapshot doc : value) {
                                RatingItem ratingItem = doc.toObject(RatingItem.class);
                                ratingItems.add(ratingItem);
                            }
                            rating_list.setValue(ratingItems);
                        }
                    }
                });
    }

    public void setAllRating_list(List<String> serviceIDList) {
        List<RatingItem> ratingItems = new ArrayList<>();
        for(String serviceID : serviceIDList) {
            db.collection("/Adme_Service_list/" + serviceID + "/review_list")
                    .orderBy("time", Query.Direction.DESCENDING).limit(20)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                            } else {
                                for (QueryDocumentSnapshot doc : value) {
                                    RatingItem ratingItem = doc.toObject(RatingItem.class);
                                    ratingItems.add(ratingItem);
                                }
                                rating_list.setValue(ratingItems);
                            }
                        }
                    });
        }
    }

    public LiveData<List<RatingItem>> getRating_list() {
        return rating_list;
    }

    public LiveData<User> getUserData() {
        return userData;
    }
}
package com.example.adme.Activities.ui.servicehistory;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.adme.Architecture.DataRepository;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ServiceHistoryViewModel extends ViewModel {
    private MutableLiveData<User> userData;
    private MutableLiveData<List<Appointment>> allAppointmentList;
    private DataRepository repository;
    private FirebaseFirestore db;

    public ServiceHistoryViewModel() {
        repository = new DataRepository();
        userData = repository.getUserData();
        db = FirebaseFirestore.getInstance();
        allAppointmentList = new MutableLiveData<List<Appointment>>();
    }

    public void fatchAllAppointmentList(String userID) {
        db.collection("Adme_Appointment_list")
                .whereEqualTo("clint_ref", userID).limit(20)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        List<Appointment> allappointmentlist = new ArrayList<>();
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                        } else {
                            for (QueryDocumentSnapshot doc : value) {
                                Appointment appointment = doc.toObject(Appointment.class);
                                appointment.setAppointmentID(doc.getId());
                                allappointmentlist.add(appointment);
                                Log.d("TAG", "onSuccess appointments: "+allappointmentlist.size());
                            }
                            allAppointmentList.setValue(allappointmentlist);
                        }
                    }
                });
    }

    public LiveData<List<Appointment>> getAllAppointmentList() {
        return allAppointmentList;
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }

}

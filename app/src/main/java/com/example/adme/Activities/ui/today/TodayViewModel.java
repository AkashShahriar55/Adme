package com.example.adme.Activities.ui.today;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adme.Architecture.DataRepository;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.AppointmentRef;
import com.example.adme.Helpers.Notification;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TodayViewModel extends ViewModel {

    private MutableLiveData<User> userData;
//    private MutableLiveData<List<AppointmentRef>> appointmentrefList;
    private MutableLiveData<List<Appointment>> appointmentList;
    private MutableLiveData<List<Appointment>> allAppointmentList;
    private DataRepository repository;
    private FirebaseFirestore db;

    public TodayViewModel() {
        repository = new DataRepository();
        userData = repository.getUserData();
        db = FirebaseFirestore.getInstance();
        appointmentList = new MutableLiveData<List<Appointment>>();
        allAppointmentList = new MutableLiveData<List<Appointment>>();
//        appointmentrefList = new MutableLiveData<List<AppointmentRef>>();
    }

//    public void fatchAppointmentList(String userID, String mode) {
//        db.collection("Adme_User/"+userID+"/appointment_list")
//                .whereEqualTo("mode", mode)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
//                        List<AppointmentRef> appointmentRefList = new ArrayList<>();
//                        appointmentlist.clear();
//                        if (e != null) {
//                            Log.w("TAG", "Listen failed.", e);
//                        } else {
//                            for (QueryDocumentSnapshot doc : value) {
//                                AppointmentRef appointmentRef = doc.toObject(AppointmentRef.class);
//                                appointmentRefList.add(appointmentRef);
//                                db.collection("Adme_Appointment_list").document(appointmentRef.getReference()).get()
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                Appointment appointment = documentSnapshot.toObject(Appointment.class);
//                                                appointmentlist.add(appointment);
//                                                Log.d("TAG", appointmentlist.size()+"onSuccess getPrice_requested: "+appointment.getPrice_requested());
//                                            }
//                                        });
//                            }
//                            appointmentrefList.setValue(appointmentRefList);
//                            appointmentList.setValue(appointmentlist);
//                            Log.d("TAG", "onSuccess getPrice_requested: "+appointmentlist.size());
//                        }
//                    }
//                });
//    }

    public void fatchActiveAppointmentList(String userID) {
        db.collection("Adme_Appointment_list")
                .whereEqualTo("service_provider_ref", userID)
                .whereIn("state", Arrays.asList(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_APPROVED, FirebaseUtilClass.APPOINTMENT_STATE_INVOICE_SEND))
//                .whereEqualTo("state", FirebaseUtilClass.APPOINTMENT_STATE_CLINT_APPROVED)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        List<Appointment> appointmentlist = new ArrayList<>();
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                        } else {
                            for (QueryDocumentSnapshot doc : value) {
                                Appointment appointment = doc.toObject(Appointment.class);
                                appointment.setAppointmentID(doc.getId());
                                appointmentlist.add(appointment);
                                Log.d("TAG", "onSuccess appointments: "+appointmentlist.size());
                            }
                            appointmentList.setValue(appointmentlist);
                        }
                    }
                });
    }

    public void fatchAllAppointmentList(String userID) {
        db.collection("Adme_Appointment_list")
                .whereEqualTo("service_provider_ref", userID)
//                .whereIn("state", Arrays.asList(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_APPROVED, FirebaseUtilClass.APPOINTMENT_STATE_INVOICE_SEND))
//                .whereEqualTo("state", FirebaseUtilClass.APPOINTMENT_STATE_CLINT_APPROVED)
//                .orderBy("time", Query.Direction.DESCENDING).limit(20)
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

//    public LiveData<List<AppointmentRef>> getAppointmentRef() {
//        return appointmentrefList;
//    }

    public LiveData<List<Appointment>> getAppointmentList() {
        return appointmentList;
    }

    public LiveData<List<Appointment>> getAllAppointmentList() {
        return allAppointmentList;
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public void updateStatus( String status){
        repository.updateStatus(status);
    }
}
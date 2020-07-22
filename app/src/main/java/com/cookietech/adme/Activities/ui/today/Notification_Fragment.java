package com.cookietech.adme.Activities.ui.today;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.cookietech.adme.Helpers.Notification;
import com.cookietech.adme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notification_Fragment extends Fragment {
    private static final String TAG = "Notification_Fragment";
    private NotificationViewModel mViewModel;
    private RecyclerView recyclerView_notificaiton;
    private List<Notification> itemList = new ArrayList<>();
    private NotificationItemInventoryAdapter notificationItemInventoryAdapter;
    FirebaseFirestore db;
    Notification notification;

    public static Notification_Fragment newInstance() {
        return new Notification_Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification__fragment, container, false);

        notificationItemInventoryAdapter = new NotificationItemInventoryAdapter(itemList, getContext());
        recyclerView_notificaiton = view.findViewById(R.id.notification);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_notificaiton.setLayoutManager(mLayoutManager);
        recyclerView_notificaiton.setItemAnimator(new DefaultItemAnimator());
        recyclerView_notificaiton.setAdapter(notificationItemInventoryAdapter);

        ImageView button_back = view.findViewById(R.id.back_button);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        // TODO: Use the ViewModel
//        getFirebaseData("notification1");
        getFirebaseQueryList();
    }

    private void getFirebaseData(String doc) {
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_User/user1/notification_list").document(doc)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        notification = documentSnapshot.toObject(Notification.class);
//                        notification.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
//                        setFirebaseData("notification2");
                    }
                });
    }

    private void getFirebaseQueryList() {
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_User/user1/notification_list")
                .whereEqualTo("mode", "Client")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        itemList.clear();
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                        } else {
                            for (QueryDocumentSnapshot doc : value) {
                                Notification notification = doc.toObject(Notification.class);
                                itemList.add(notification);
                            }
                        }
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notificationItemInventoryAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
    }

    private void setFirebaseData(String doc){
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_User/user1/notification_list").document(doc)
                .set(notification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }


}

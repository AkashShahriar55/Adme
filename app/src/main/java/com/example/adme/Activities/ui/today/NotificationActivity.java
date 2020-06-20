package com.example.adme.Activities.ui.today;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Helpers.Notification;
import com.example.adme.R;
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

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "Notification_Fragment";
    private NotificationViewModel mViewModel;
    private RecyclerView recyclerView_notificaiton;
    private List<Notification> itemList = new ArrayList<>();
    private NotificationItemInventoryAdapter notificationItemInventoryAdapter;
    FirebaseFirestore db;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification__fragment);

        notificationItemInventoryAdapter = new NotificationItemInventoryAdapter(itemList, NotificationActivity.this);
        recyclerView_notificaiton = findViewById(R.id.notification);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NotificationActivity.this);
        recyclerView_notificaiton.setLayoutManager(mLayoutManager);
        recyclerView_notificaiton.setItemAnimator(new DefaultItemAnimator());
        recyclerView_notificaiton.setAdapter(notificationItemInventoryAdapter);

        ImageView button_back = findViewById(R.id.back_button);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String mode = getIntent().getStringExtra("mode");
        getFirebaseQueryList(mode);
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

    private void getFirebaseQueryList(String mode) {
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_User/user1/notification_list")
                .whereEqualTo("mode", mode)
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
                        runOnUiThread(new Runnable() {
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

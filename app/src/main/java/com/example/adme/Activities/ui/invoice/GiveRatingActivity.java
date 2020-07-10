package com.example.adme.Activities.ui.invoice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.RatingItem;
import com.example.adme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class GiveRatingActivity extends AppCompatActivity {
    private static final String TAG = "GiveRatingActivity";
    private TextView tv_title;
    private EditText tv_comment;
    private Button bt_send;
    private RatingBar ratingBar;
    String clintUserName,serviceProviderUserName,totalPrice,invoiceID,serviceID;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        clintUserName = getIntent().getStringExtra("clintUserName");
        serviceProviderUserName = getIntent().getStringExtra("serviceProviderUserName");
        totalPrice = getIntent().getStringExtra("totalPrice");
        invoiceID = getIntent().getStringExtra("invoiceID");
        serviceID = getIntent().getStringExtra("serviceID");

        initialize();
    }

    public void initialize(){
        db = FirebaseFirestore.getInstance();
        tv_title = findViewById(R.id.tv_title);
        tv_comment = findViewById(R.id.tv_comment);
        bt_send = findViewById(R.id.bt_send);
        ratingBar = findViewById(R.id.ratingBar);

        tv_title.setText("How would you rate "+serviceProviderUserName+"\'s service?");

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RatingItem ratingItem = new RatingItem();

                ratingItem.setClintName(clintUserName);
                ratingItem.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                ratingItem.setRating(ratingBar.getRating());
                ratingItem.setComment(tv_comment.getText().toString());
                ratingItem.setTotalPrice(totalPrice);
                ratingItem.setInvoiceID(invoiceID);

                if( invoiceID != null && serviceID != null ){
                    db.collection("Adme_Service_list/"+serviceID+"/review_list")
                            .document(invoiceID)
                            .set(ratingItem)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "invoiceItem successfully written!");
                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                    Toast.makeText(getApplicationContext(), "Please, check internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Please, check internet connection(invoiceID/serviceID null)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

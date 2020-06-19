package com.example.adme.Activities.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class Contacts extends AppCompatActivity {


    ConstraintLayout backBtn;
    View phoneView;
    ConstraintLayout firstPhoneLayout, secondPhoneLayout;
    TextInputEditText edtNumber;
    Button addContactBtn, firstContactModeBtn, scndContactModeBtn, firstContactDelBtn, scndContactDelBtn;
    TextView txtFirstPhoneNumber, txtSecondPhoneNumber;


    private ProfileViewModel mViewModel;
    private User mCurrentUser;
    private String isClient;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        backBtn = findViewById(R.id.backBtn);

        edtNumber = findViewById(R.id.edtNumber);
        addContactBtn = findViewById(R.id.addContactBtn);

        //phoneView = (View) findViewById(R.id.phoneNumberLayout);
        firstPhoneLayout = findViewById(R.id.firstPhone);
        secondPhoneLayout = findViewById(R.id.secondPhone);




        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        final Observer<User> userDataObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mCurrentUser = user;
                Log.d("view-model", "onChanged:  bottom details" + user.getStatus());
                updateUiData();
            }
        };

       mViewModel.getUserData().observe(this,userDataObserver);
    }



    private void updateUiData() {
        //txtProfileName.setText(mCurrentUser.getUsername());
        /*if (mAuth.getCurrentUser() != null) {
            long timeStamp = mAuth.getCurrentUser().getMetadata().getCreationTimestamp();
            String date = CookieTechUtilityClass.getDate(timeStamp);
            Map<String, String> contacts = mCurrentUser.getContacts();
            if (contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_ONE) != null) {
                phoneView = (View) firstPhoneLayout;
                txtFirstPhoneNumber = phoneView.findViewById(R.id.phoneNumber);
                txtFirstPhoneNumber.setText("1. "+contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_ONE));
                Log.e("first phone:" , contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_ONE));
            }
            if (contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO) != null) {
                phoneView = (View) secondPhoneLayout;
                txtSecondPhoneNumber = phoneView.findViewById(R.id.phoneNumber);
                txtSecondPhoneNumber.setText("1. "+contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO));
                Log.e("first phone:" , contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO));
            }

        }*/
    }











    }



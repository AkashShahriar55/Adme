package com.example.adme.Activities.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Objects;

public class Contacts extends AppCompatActivity {


    ConstraintLayout backBtn;
    View phoneView, emailView;
    ConstraintLayout firstPhoneLayout, secondPhoneLayout,emailLayout;
    TextInputEditText edtNumber,edtEmail;
    Button addContactBtn, addEmailBtn;
    TextView txtFirstPhoneNumber, txtSecondPhoneNumber, txtEmail, txtContactMode;

    ImageButton firstContactModeBtn, scndContactModeBtn, firstContactDelBtn, scndContactDelBtn, emailModeBtn, emailDelBtn;

    private ContactsViewModel mViewModel;
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
        emailLayout = findViewById(R.id.emailLayout);


        phoneView = (View) firstPhoneLayout;
        txtFirstPhoneNumber = phoneView.findViewById(R.id.contact);
        txtContactMode = phoneView.findViewById(R.id.contactMode);
        firstContactModeBtn = phoneView.findViewById(R.id.changeModeBtn);
        firstContactDelBtn = phoneView.findViewById(R.id.delBtn);

        firstContactModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtContactMode.getText().toString().equals(getString(R.string.contactModePublic)))
                {
                    mViewModel.updatePhoneNumberMode("Private");
                    Log.e("mode chnage","public to private");
                    updateUiData();
                }
                else if(txtContactMode.getText().toString().equals(getString(R.string.contactModePrivate)))
                {
                    mViewModel.updatePhoneNumberMode("Public");
                    Log.e("mode chnage","private to public");
                    updateUiData();
                }
            }
        });




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

        mViewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);

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



    @SuppressLint("SetTextI18n")
    private void updateUiData() {
        //txtProfileName.setText(mCurrentUser.getUsername());
        if (mAuth.getCurrentUser() != null) {
            long timeStamp = mAuth.getCurrentUser().getMetadata().getCreationTimestamp();
            String date = CookieTechUtilityClass.getDate(timeStamp);
            Map<String, String> contacts = mCurrentUser.getContacts();
            if (contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO) != null) {


                txtFirstPhoneNumber.setText("1. "+contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO));
                if(Objects.equals(contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_PRIVACY), "Public"))
                {
                    txtContactMode.setText(R.string.contactModePublic);
                    firstContactModeBtn.setImageResource(R.drawable.ic_unlock);
                }
                else
                {
                    txtContactMode.setText(R.string.contactModePrivate);
                    firstContactModeBtn.setImageResource(R.drawable.ic_lock);
                }
                Log.e("first phone:" , contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO));
            }
            // for the second phone number
            /*if (contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO) != null) {
                phoneView = (View) secondPhoneLayout;
                txtSecondPhoneNumber = phoneView.findViewById(R.id.phoneNumber);
                txtSecondPhoneNumber.setText("1. "+contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO));
                Log.e("first phone:" , contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO));
            }*/

        }
    }











    }



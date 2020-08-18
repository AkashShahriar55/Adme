package com.cookietech.adme.activities.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.CookieTechUtilityClass;
import com.cookietech.adme.Helpers.User;
import com.cookietech.adme.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;
import java.util.Objects;

public class Contacts extends AppCompatActivity {


    ConstraintLayout backBtn;
    View phoneView, emailView;
    ConstraintLayout firstPhoneLayout, secondPhoneLayout,emailLayout;
    TextInputEditText edtNumber,edtEmail;
    Button addContactBtn, addEmailBtn;
    TextView txtFirstPhoneNumber, txtSecondPhoneNumber, txtEmail, txtContactMode,txtEmailMode;

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
        edtEmail = findViewById(R.id.edtEmail);
        addContactBtn = findViewById(R.id.addContactBtn);
        addEmailBtn = findViewById(R.id.addEmailBtn);

        //phoneView = (View) findViewById(R.id.phoneNumberLayout);
        firstPhoneLayout = findViewById(R.id.firstPhone);
        secondPhoneLayout = findViewById(R.id.secondPhone);
        emailLayout = findViewById(R.id.emailLayout);


        phoneView = (View) firstPhoneLayout;
        txtFirstPhoneNumber = phoneView.findViewById(R.id.contact);
        txtContactMode = phoneView.findViewById(R.id.contactMode);
        firstContactModeBtn = phoneView.findViewById(R.id.changeModeBtn);
        firstContactDelBtn = phoneView.findViewById(R.id.delBtn);

        emailView = (View) emailLayout;
        txtEmail = emailView.findViewById(R.id.contact);
        txtEmailMode = emailView.findViewById(R.id.contactMode);
        emailModeBtn = emailView.findViewById(R.id.changeModeBtn);
        emailDelBtn = emailView.findViewById(R.id.delBtn);

        firstContactModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtContactMode.getText().toString().equals(getString(R.string.contactModePublic)))
                {
                    mViewModel.updatePhoneNumberMode("Private");
                    Log.e("mode chnage","public to private");
                    //updateUiData();
                }
                else if(txtContactMode.getText().toString().equals(getString(R.string.contactModePrivate)))
                {
                    mViewModel.updatePhoneNumberMode("Public");
                    Log.e("mode chnage","private to public");
                    //updateUiData();
                }
            }
        });


        firstContactDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deletePhoneNumber();
            }
        });

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneView.getVisibility() == View.GONE)
                {
                    String number = edtNumber.getText().toString();
                    mViewModel.addPhoneNumber(number);
                    edtNumber.setText("");
                    //phoneView.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Your can add only one number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        emailModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtEmailMode.getText().toString().equals(getString(R.string.contactModePublic)))
                {
                    mViewModel.updateEmailMode("Private");
                    Log.e("mode chnage","public to private");
                    //updateUiData();
                }
                else if(txtEmailMode.getText().toString().equals(getString(R.string.contactModePrivate)))
                {
                    mViewModel.updateEmailMode("Public");
                    Log.e("mode chnage","private to public");
                    //updateUiData();
                }
            }
        });


        emailDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deleteEmail();
            }
        });

        addEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailView.getVisibility() == View.GONE)
                {
                    String email = edtEmail.getText().toString();
                    mViewModel.addEmail(email);
                    edtEmail.setText("");
                    //phoneView.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Your can add only one email", Toast.LENGTH_SHORT).show();
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

                phoneView.setVisibility(View.VISIBLE);
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
            else
            {
                phoneView.setVisibility(View.GONE);
            }
            // for the second phone number
            /*if (contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO) != null) {
                phoneView = (View) secondPhoneLayout;
                txtSecondPhoneNumber = phoneView.findViewById(R.id.phoneNumber);
                txtSecondPhoneNumber.setText("1. "+contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO));
                Log.e("first phone:" , contacts.get(FirebaseUtilClass.ENTRY_PHONE_NO_TWO));
            }*/

            if (contacts.get(FirebaseUtilClass.ENTRY_EMAIL) != null) {

                emailView.setVisibility(View.VISIBLE);
                txtEmail.setText("1. "+contacts.get(FirebaseUtilClass.ENTRY_EMAIL));
                if(Objects.equals(contacts.get(FirebaseUtilClass.ENTRY_EMAIL_PRIVACY), "Public"))
                {
                    txtEmailMode.setText(R.string.contactModePublic);
                    emailModeBtn.setImageResource(R.drawable.ic_unlock);
                }
                else
                {
                    txtEmailMode.setText(R.string.contactModePrivate);
                    emailModeBtn.setImageResource(R.drawable.ic_lock);
                }
                Log.e("email:" , contacts.get(FirebaseUtilClass.ENTRY_EMAIL));
            }
            else
            {
                Log.e("no email:" ,"no email in contacts");
                emailView.setVisibility(View.GONE);
            }

        }
    }











    }



package com.example.adme.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adme.Helpers.LoadingDialog;
import com.example.adme.Helpers.User;
import com.example.adme.Helpers.Validation;
import com.example.adme.R;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private Button reg_join_btn,goto_login_btn;
    private FirebaseAuth mAuth;
    private TextInputLayout reg_email, reg_password;
    private TextView password_validation_text;
    private LoadingDialog dialog;


    //create database reference
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Adme_User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeFields();


        reg_join_btn.setOnClickListener(v -> {

            String email = Objects.requireNonNull(reg_email.getEditText()).getText().toString();
            String pass = Objects.requireNonNull(reg_password.getEditText()).getText().toString();

            if(validateForm(email,pass)){
                dialog.startLoadingDialog();
                signUpWithEmailAndPass(email,pass);
            }

        });

        goto_login_btn.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
        });

        Objects.requireNonNull(reg_password.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!new Validation().isPasswordValid(String.valueOf(s))){
                    password_validation_text.setTextColor(getResources().getColor(R.color.red));
                }
                else if (new Validation().isPasswordValid(String.valueOf(s))){
                    password_validation_text.setTextColor(getResources().getColor(R.color.green));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        


    }

    private boolean validateForm(String email, String pass) {



        if (email.isEmpty()){
            reg_email.setErrorEnabled(true);
            reg_email.setError("Field can't be empty");
            return false;
        }
        else if (!new Validation().isEmailValid(email)){
            reg_email.setErrorEnabled(true);
            reg_email.setError("Please Enter a valid email address");
            return false;
        }

        else if (pass.isEmpty()){
            reg_email.setErrorEnabled(false);
            reg_password.setErrorEnabled(true);
            reg_password.setError("Field can't be empty");
            return false;
        }
        else if (!new Validation().isPasswordValid(pass)){
            reg_email.setErrorEnabled(false);
            reg_password.setErrorEnabled(true);
            reg_password.setError("Please Enter a valid email address");
            return false;
        }

        else {
            return true;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(RegistrationActivity.this,LandingActivity.class));
            finish();
        }
    }

    private void signUpWithEmailAndPass(String email_address, String password) {
        mAuth.createUserWithEmailAndPassword(email_address, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        /** User Information processing will added here**/
                        String username = "";
                        String NULL = "";
                        assert user != null;
                        if (user.getDisplayName() != null){
                            username = user.getDisplayName();
                        }
                        else{
                            username = "Adme_User";
                        }

                        User new_user = new User(username,NULL,NULL,"client","online");
                        /*** Insert into fireStore database**/
                        userRef.document(user.getUid()).set(new_user).addOnSuccessListener(aVoid -> {
                            dialog.dismissDialog();
                            startActivity(new Intent(RegistrationActivity.this,LandingActivity.class));
                            finish();
                        });


                    } else {
                        // If sign in fails, display a message to the user.
                        dialog.dismissDialog();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistrationActivity.this, "Authentication failed\n"+ task.getException(),
                                Toast.LENGTH_SHORT).show();

                    }

                    // ...
                });
    }


    private void initializeFields() {
        reg_join_btn = findViewById(R.id.reg_join_btn);
        reg_email = findViewById(R.id.reg_email);
        reg_email = findViewById(R.id.reg_email);
        reg_password = findViewById(R.id.reg_password);
        password_validation_text = findViewById(R.id.password_validation_text);
        mAuth = FirebaseAuth.getInstance();
        dialog = new LoadingDialog(RegistrationActivity.this);
        goto_login_btn = findViewById(R.id.goto_login_btn);

        /** To hide the keyboard after clicking a  button**/
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }











}

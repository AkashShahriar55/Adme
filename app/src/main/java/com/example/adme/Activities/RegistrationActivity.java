package com.example.adme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.LoadingDialog;
import com.example.adme.Helpers.User;
import com.example.adme.Helpers.Validation;
import com.example.adme.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class RegistrationActivity extends AppCompatActivity implements FirebaseUtilClass.CreateUserCommunicator {

    private static final String TAG = "RegistrationActivity";
    private Button reg_join_btn,goto_login_btn;
    private FirebaseAuth mAuth;
    private LoadingDialog dialog;


    //create database reference
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Adme_User");

    private FirebaseUtilClass firebaseUtilClass = new FirebaseUtilClass();

    private SharedPreferences firstUsePreferences;
    private boolean isLocationSettingShowed = false;
    private View contextView;
    private CountryCodePicker ccp;
    private TextInputLayout phoneText,codeText;

    private int btnType = 0;
    private String phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstUsePreferences = getSharedPreferences(String.valueOf(R.string.SHARED_PREFERENCE_FIRST_USE),MODE_PRIVATE);
        isLocationSettingShowed = firstUsePreferences.getBoolean(String.valueOf(R.string.SP_IS_LOCATION_SETTING_SHOWED),false);

        initializeFields();


        reg_join_btn.setOnClickListener(v -> {

            if(btnType == 0){

                phoneText.setEnabled(false);
                reg_join_btn.setEnabled(false);

                phoneNumber = ccp.getFullNumberWithPlus();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        120,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        RegistrationActivity.this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks

            } else {

                reg_join_btn.setEnabled(false);

                String verificationCode = Objects.requireNonNull(codeText.getEditText()).getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);


            }

        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(RegistrationActivity.this,"There Is Some Error In Verification",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                btnType = 1;
                reg_join_btn.setText("Verify Code");
                reg_join_btn.setEnabled(true);





                // ...
            }
        };



        goto_login_btn.setOnClickListener(v -> {
            onBackPressed();
        });



    }





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }


    private void startLandingActivity() {
        Intent intent = new Intent(RegistrationActivity.this, LandingActivity.class);
        startActivity(intent);
    }


    private void initializeFields() {
        reg_join_btn = findViewById(R.id.reg_join_btn);

        mAuth = FirebaseAuth.getInstance();
        dialog = new LoadingDialog(this,"Logging in",null);
        goto_login_btn = findViewById(R.id.goto_login_btn);
        contextView = findViewById(R.id.contextView);

        ccp = findViewById(R.id.ccp);
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        ccp.registerCarrierNumberEditText(phoneText.getEditText());





        /** To hide the keyboard after clicking a  button**/
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void startAccessLocationActivity() {
        Intent intent = new Intent(RegistrationActivity.this, AccessLocationActivity.class);
        startActivity(intent);
    }

    @Override
    public void userAlreadyExists(User user) {
        if(isLocationSettingShowed){
            startLandingActivity();
        }else{
            startAccessLocationActivity();
        }
    }

    @Override
    public void onUserCreatedSuccessfully(User user) {
        dialog.dismiss();

        if(isLocationSettingShowed){
            startLandingActivity();
        }else{
            startAccessLocationActivity();
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                           /* Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                            startActivity(intent);
                            finish();*/

                            //FirebaseUser user = task.getResult().getUser();
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            /*firebaseUtilClass.createUser(user,RegistrationActivity.this);*/
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            //Toast.makeText(Main2Activity.this,"There Is Some Error",Toast.LENGTH_LONG).show();
                            Snackbar.make(contextView,"Error: " + Objects.requireNonNull(task.getException()).getMessage(),Snackbar.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}

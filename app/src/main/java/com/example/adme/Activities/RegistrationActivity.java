package com.example.adme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
    private CountryCodePicker ccp;
    private TextInputLayout phoneText;

    private int btnType = 0;
    private String phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    //For Verification Code
    private EditText editText0,editText1,editText2,editText3,editText4,editText5;
    private ConstraintLayout codeText;

    private TextView policy_text,resend_code_btn,timer_txt;
    private long timeInMillSec = 120000; // Two Minute
    private boolean timerRunning;
    private CountDownTimer countDownTimer;
    private String verificationCode = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstUsePreferences = getSharedPreferences(String.valueOf(R.string.SHARED_PREFERENCE_FIRST_USE),MODE_PRIVATE);
        isLocationSettingShowed = firstUsePreferences.getBoolean(String.valueOf(R.string.SP_IS_LOCATION_SETTING_SHOWED),false);

        initializeFields();

        resend_code_btn.setOnClickListener(v -> {

            resetCodeFields();
            resend_code_btn.setEnabled(false);
            sendVerificationCode();

        });


        reg_join_btn.setOnClickListener(v -> {


            if(phoneText.getEditText().getText().length() < 11){

                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();

            }

            else {


                sendVerificationCode();
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
                Toast.makeText(RegistrationActivity.this,"A Verification Code is sent.",Toast.LENGTH_LONG).show();
                mVerificationId = verificationId;
                mResendToken = token;
                phoneText.setEnabled(false);
                ccp.setEnabled(false);
                StartTimer();
                reg_join_btn.setVisibility(View.GONE);
                codeText.setVisibility(View.VISIBLE);
                policy_text.setVisibility(View.GONE);
                resend_code_btn.setVisibility(View.VISIBLE);
                timer_txt.setVisibility(View.VISIBLE);
            }
        };



        goto_login_btn.setOnClickListener(v -> {
            onBackPressed();
        });



    }

    private void sendVerificationCode() {

        phoneNumber = ccp.getFullNumberWithPlus();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                RegistrationActivity.this,               // Activity (for callback binding)
                mCallbacks);// OnVerificationStateChangedCallbacks
    }

    private void StartTimer() {
        countDownTimer = new CountDownTimer(timeInMillSec,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeInMillSec = millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {
                resend_code_btn.setEnabled(true);
                timer_txt.setText(null);
                timer_txt.setVisibility(View.GONE);

            }
        }.start();
    }

    private void updateTimer() {
        int minute = (int) timeInMillSec / 60000;
        int second = (int) timeInMillSec % 60000 / 1000;
        String timeLeftText = "";
        timeLeftText += minute;
        timeLeftText += " : ";
        if (second<10){
            timeLeftText += "0";
        }
        timeLeftText += second;
        timer_txt.setText(timeLeftText);
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
        ccp = findViewById(R.id.ccp);
        phoneText = findViewById(R.id.phoneText);
        ccp.registerCarrierNumberEditText(phoneText.getEditText());
        codeText = findViewById(R.id.codeText);
        policy_text = findViewById(R.id.policy_text);
        resend_code_btn = findViewById(R.id.resend_code_btn);
        timer_txt = findViewById(R.id.timer_txt);
        dialog = new LoadingDialog(this,"Logging in",null);

        editText0 = findViewById(R.id.editText);
        editText1 = findViewById(R.id.editText6);
        editText2 = findViewById(R.id.editText5);
        editText3 = findViewById(R.id.editText4);
        editText4 = findViewById(R.id.editText3);
        editText5 = findViewById(R.id.editText2);
        editText0.addTextChangedListener(new LoginOTPTextListener(editText0));
        editText1.addTextChangedListener(new LoginOTPTextListener(editText1));
        editText2.addTextChangedListener(new LoginOTPTextListener(editText2));
        editText3.addTextChangedListener(new LoginOTPTextListener(editText3));
        editText4.addTextChangedListener(new LoginOTPTextListener(editText4));
        editText5.addTextChangedListener(new LoginOTPTextListener(editText5));



    }

    private void startAccessLocationActivity() {
        Intent intent = new Intent(RegistrationActivity.this, AccessLocationActivity.class);
        startActivity(intent);
    }
    private void startUserInfoActivity() {
        Intent intent = new Intent(RegistrationActivity.this, UserInfoActivity.class);
        startActivity(intent);
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //FirebaseUser user = task.getResult().getUser();
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            firebaseUtilClass.createUser(user,RegistrationActivity.this);
                            dialog.show();

                        } else {
                            // Sign in failed, display a message and update the UI

                           // Snackbar.make(contextView,"Error: " + Objects.requireNonNull(task.getException()).getMessage(),Snackbar.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(RegistrationActivity.this, "Verification code is invalid", Toast.LENGTH_LONG).show();
                                resetCodeFields();
                            }
                            else {
                                Toast.makeText(RegistrationActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }



    public class LoginOTPTextListener implements TextWatcher {

        private View view;
        private LoginOTPTextListener(View view)
        {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()){
                case R.id.editText:
                    if(text.length() == 1){
                        editText0.setEnabled(false);
                        editText1.setEnabled(true);
                        editText1.requestFocus();
                        verificationCode += editText0.getText().toString();
                        showKeyboard();

                    }
                    break;

                case R.id.editText6:
                    if (text.length() == 1){
                        editText1.setEnabled(false);
                        editText2.setEnabled(true);
                        editText2.requestFocus();
                        verificationCode += editText1.getText().toString();
                        showKeyboard();
                    }

                    break;

                case R.id.editText5:
                    if(text.length() == 1){
                        editText2.setEnabled(false);
                        editText3.setEnabled(true);
                        editText3.requestFocus();
                        verificationCode += editText2.getText().toString();
                        showKeyboard();
                    }
                    break;
                case R.id.editText4:
                    if(text.length() == 1){
                        editText3.setEnabled(false);
                        editText4.setEnabled(true);
                        editText4.requestFocus();
                        verificationCode += editText3.getText().toString();
                        showKeyboard();
                    }
                    break;
                case R.id.editText3:
                    if(text.length() == 1){
                        editText4.setEnabled(false);
                        editText5.setEnabled(true);
                        editText5.requestFocus();
                        verificationCode += editText4.getText().toString();
                        showKeyboard();
                    }
                    break;
                case R.id.editText2:
                    if(text.length() == 1){
                        closeKeyboard();
                        verificationCode += editText5.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);

                    }
                    break;
            }
        }
    }

    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void resetCodeFields() {
        verificationCode = "";
        editText0.setText(null);
        editText1.setText(null);
        editText2.setText(null);
        editText3.setText(null);
        editText4.setText(null);
        editText5.setText(null);
        editText0.setEnabled(true);
        editText0.requestFocus();
    }


    @Override
    public void userAlreadyExists(User user) {
        dialog.dismiss();
        startAccessLocationActivity();

    }

    @Override
    public void onUserCreatedSuccessfully(User user) {
        dialog.dismiss();
        startUserInfoActivity();
    }



}

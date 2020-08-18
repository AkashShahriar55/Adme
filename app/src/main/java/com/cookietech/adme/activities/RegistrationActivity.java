package com.cookietech.adme.activities;

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

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.CustomToast;
import com.cookietech.adme.Helpers.LoadingDialog;
import com.cookietech.adme.Helpers.User;
import com.cookietech.adme.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
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

    private FirebaseUtilClass firebaseUtilClass = FirebaseUtilClass.getInstance();

    private SharedPreferences firstUsePreferences;
    private boolean isLocationSettingShowed = false;
    private CountryCodePicker ccp;
    private EditText phoneText;

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
    private String[] verificationCode = new String[6];
    private boolean codeSent = false;
    private boolean code0Ok = false, code1Ok = false, code2Ok = false, code3Ok = false, code4Ok =false, code5Ok = false;

    public static final String PHONE_NO = "phone_no";
    public static final String CURRENT_TIMER = "current_timer";
    public static final String IS_CODE_SENT = "is_countdown_started";
    private User new_user;



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

            Log.d("akash_debug", "onCreate: button clicked");
            sendVerificationCode();

        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException exception) {
                Log.d("akash_debug", "onVerificationFailed: " + exception);

                if(exception.getClass() == FirebaseAuthInvalidCredentialsException.class){
                    Log.d(TAG, "onVerificationFailed: invalid credential" );
                    CustomToast.makeErrorToast(RegistrationActivity.this,"Please enter a valid no!",Toast.LENGTH_SHORT).show();
                }else if(exception.getClass() == FirebaseTooManyRequestsException.class){
                    Log.d(TAG, "onVerificationFailed: too many request");
                    CustomToast.makeErrorToast(RegistrationActivity.this,"Too many request.Try again later!",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                codeSent = true;
                CustomToast.makeSuccessToast(RegistrationActivity.this,"A Verification Code is sent.",Toast.LENGTH_SHORT).show();
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


        if(savedInstanceState != null){
            String phone_no = savedInstanceState.getString(PHONE_NO);
            phoneText.setText(phone_no);
            boolean is_code_sent = savedInstanceState.getBoolean(IS_CODE_SENT);
            if(is_code_sent){
                codeSent = true;
                reg_join_btn.setVisibility(View.GONE);
                codeText.setVisibility(View.VISIBLE);
                policy_text.setVisibility(View.GONE);
                resend_code_btn.setVisibility(View.VISIBLE);
                timer_txt.setVisibility(View.VISIBLE);
                timeInMillSec = savedInstanceState.getLong(CURRENT_TIMER);
                if(timeInMillSec == 0){
                    resend_code_btn.setEnabled(true);
                    timer_txt.setText(null);
                    timer_txt.setVisibility(View.GONE);
                }else{
                    updateTimer();
                    StartTimer();
                }
            }
        }



    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(PHONE_NO,phoneText.getText().toString());
        if(codeSent){
            outState.putBoolean(IS_CODE_SENT,true);
            outState.putLong(CURRENT_TIMER,timeInMillSec);
        }


        super.onSaveInstanceState(outState);
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

    @Override
    protected void onResume() {
        super.onResume();

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
        ccp.registerCarrierNumberEditText(phoneText);
        ccp.detectSIMCountry(true);
        ccp.setCustomMasterCountries("NA,BD");
        codeText = findViewById(R.id.codeText);
        policy_text = findViewById(R.id.policy_text);
        resend_code_btn = findViewById(R.id.resend_code_btn);
        timer_txt = findViewById(R.id.timer_txt);
        dialog = new LoadingDialog(this,"Logging in","Please wait...");


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
                            createUser(user);
                            dialog.show();

                        } else {
                            // Sign in failed, display a message and update the UI

                           // Snackbar.make(contextView,"Error: " + Objects.requireNonNull(task.getException()).getMessage(),Snackbar.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                CustomToast.makeErrorToast(RegistrationActivity.this, "Verification code is invalid", Toast.LENGTH_LONG).show();
                                resetCodeFields();
                            }
                            else {
                                CustomToast.makeErrorToast(RegistrationActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
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
                        code0Ok = true;
                        editText1.requestFocus();
                        verificationCode[0] = editText0.getText().toString();
                        showKeyboard();
                        verifyCode();
                    }else{
                        code0Ok = false;
                    }
                    break;

                case R.id.editText6:
                    if (text.length() == 1){
                        code1Ok = true;
                        editText2.requestFocus();
                        verificationCode[1] = editText1.getText().toString();
                        verifyCode();
                        showKeyboard();
                    }else{
                        code1Ok = false;
                    }

                    break;

                case R.id.editText5:
                    if(text.length() == 1){
                        code2Ok = true;
                        editText3.requestFocus();
                        verificationCode[2] = editText2.getText().toString();
                        verifyCode();
                        showKeyboard();
                    }else{
                        code2Ok = true;
                    }
                    break;
                case R.id.editText4:
                    if(text.length() == 1){
                        code3Ok = true;
                        editText4.requestFocus();
                        verificationCode[3] = editText3.getText().toString();
                        verifyCode();
                        showKeyboard();
                    }else{
                        code3Ok = false;
                    }
                    break;
                case R.id.editText3:
                    if(text.length() == 1){
                        code4Ok = true;
                        editText5.requestFocus();
                        verificationCode[4] = editText4.getText().toString();
                        verifyCode();
                        showKeyboard();
                    }else{
                        code4Ok = false;
                    }
                    break;
                case R.id.editText2:
                    if(text.length() == 1){
                        code5Ok = true;
                        verificationCode[5] = editText5.getText().toString();
                        verifyCode();
                        showKeyboard();

                    }else{
                        code5Ok = false;
                    }
                    break;
            }
        }
    }

    private void verifyCode() {
        if(code0Ok && code1Ok && code2Ok && code3Ok && code4Ok && code5Ok){
            closeKeyboard();
            StringBuilder verificationCodeString = new StringBuilder();
            for (String s : verificationCode) {
                verificationCodeString.append(s);
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCodeString.toString());
            signInWithPhoneAuthCredential(credential);
        }
    }

    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void resetCodeFields() {
        timeInMillSec = 120000;
        verificationCode = new String[6];
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

    @Override
    public void onUserCreationFailed(FirebaseUser user) {
        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                CustomToast.makeErrorToast(RegistrationActivity.this,"User creation Failed !",Toast.LENGTH_SHORT).show();
                resetUi();
            }
        });
    }

    private void resetUi() {
        codeSent = false;
        resetCodeFields();
        countDownTimer.cancel();
        timeInMillSec = 120000;
        phoneText.setEnabled(true);
        reg_join_btn.setVisibility(View.VISIBLE);
        codeText.setVisibility(View.GONE);
        policy_text.setVisibility(View.VISIBLE);
        resend_code_btn.setVisibility(View.GONE);
        timer_txt.setVisibility(View.GONE);

    }

    private void createUser(FirebaseUser user) {
        String username;
        String email = null;
        String phone = null;
        String profile_photo_url = "default_avatar";
        String NULL = "";

        if (user.getDisplayName() != null){
            username = user.getDisplayName();
        }
        else{
            username = "Adme User";
        }

        if(user.getEmail() != null){
            email = user.getEmail();
        }

        if (user.getPhoneNumber() != null){
            phone = user.getPhoneNumber();
        }

        if (user.getPhotoUrl() !=null){
            profile_photo_url = "user_photo";
        }

        String joined = String.valueOf(Objects.requireNonNull(user.getMetadata()).getCreationTimestamp());
        String user_id = user.getUid();
        String phoneNoVerified = FirebaseUtilClass.ENTRY_PHONE_NO_NOT_VERIFIED;
        new_user = new User(username,email,phone,profile_photo_url,joined,user_id,"",phoneNoVerified);
        firebaseUtilClass.createUser(user,this,new_user);
    }


}

package com.cookietech.adme.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.CustomToast;
import com.cookietech.adme.Helpers.LoadingDialog;
import com.cookietech.adme.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_PICK_CODE = 11;
    private CircleImageView choose_photo,profile_photo;
    private TextInputLayout edt_profile_username;
    private EditText edt_phone_number;
    private CountryCodePicker ccp;
    private Button profile_continue_btn;
    private ConstraintLayout user_info_top_back;

    /** This will goto database**/
    private String phoneNumber;
    private String image_url = "default_avatar";
    private String user_name;
    private CollectionReference userRef;
    private FirebaseFirestore db;

    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://adme-production.appspot.com");
    private LoadingDialog dialog;
    private FirebaseUser user;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean codeSent = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ConstraintLayout codeText;
    private TextView resend_code_btn,timer_txt;
    private long timeInMillSec = 120000; // Two Minute
    private boolean timerRunning;
    private CountDownTimer countDownTimer;

    private EditText editText0,editText1,editText2,editText3,editText4,editText5;
    private boolean code0Ok = false, code1Ok = false, code2Ok = false, code3Ok = false, code4Ok =false, code5Ok = false;
    private String[] verificationCode = new String[6];


    // save the image uri
    private Uri imageUri;
    private boolean is_image_select = false;


    public static final String PHONE_NO = "phone_no";
    public static final String CURRENT_TIMER = "current_timer";
    public static final String IS_CODE_SENT = "is_countdown_started";
    private static final String USER_NAME = "user_name";
    private static final String IMAGE_URL ="image_url";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String BUNDLE = "bundle";
    private Bundle bundle;

    //Flags
    private boolean is_tried_once = false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initializeFields();

        choose_photo.setOnClickListener(v -> uploadImage());

        profile_continue_btn.setOnClickListener(v -> {

            //user login with phone

            if(bundle != null && user.getPhoneNumber()!= null){
                String login_method = bundle.getString("login_method");
                if(login_method.equals("phone")){

                    if(!validate_userName()){
                        CustomToast.makeErrorToast(UserInfoActivity.this,"Please enter a valid  username!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        user_name = edt_profile_username.getEditText().getText().toString().trim();

                        if (is_image_select){
                            uploadProfilePhoto(imageUri);
                        }
                        else{
                            updateToFireStore();
                        }
                    }


                }
            }

            else {
                if(!validate_userName()){
                    CustomToast.makeErrorToast(UserInfoActivity.this,"Please enter a valid  username!",Toast.LENGTH_SHORT).show();
                }
                else{
                    user_name = edt_profile_username.getEditText().getText().toString().trim();

                    if (is_image_select){
                        uploadProfilePhoto(imageUri);
                    }
                    else{
                        sendVerificationCode();
                    }
                }
            }







        });


        // Verification code listener
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                linkInWithPhoneAuthCredential(phoneAuthCredential);

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
                    CustomToast.makeErrorToast(UserInfoActivity.this,"Please enter a valid no!",Toast.LENGTH_SHORT).show();
                }else if(exception.getClass() == FirebaseTooManyRequestsException.class){
                    Log.d(TAG, "onVerificationFailed: too many request");
                    CustomToast.makeErrorToast(UserInfoActivity.this,"Too many request.Try again later!",Toast.LENGTH_SHORT).show();
                }
                profile_continue_btn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                codeSent = true;
                CustomToast.makeSuccessToast(UserInfoActivity.this,"A Verification Code is sent.",Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;
                mResendToken = token;
                edt_phone_number.setEnabled(false);
                ccp.setEnabled(false);
                profile_continue_btn.setVisibility(View.GONE);
                StartTimer();
                profile_continue_btn.setVisibility(View.GONE);
                codeText.setVisibility(View.VISIBLE);
                resend_code_btn.setVisibility(View.VISIBLE);
                timer_txt.setVisibility(View.VISIBLE);

            }
        };



        if(savedInstanceState != null){
            String phone_no = savedInstanceState.getString(PHONE_NO);
            user_name = savedInstanceState.getString(USER_NAME);
            image_url = savedInstanceState.getString(IMAGE_URL);
            phoneNumber = savedInstanceState.getString(PHONE_NUMBER);
            bundle = savedInstanceState.getBundle(BUNDLE);
            edt_phone_number.setText(phone_no);
            boolean is_code_sent = savedInstanceState.getBoolean(IS_CODE_SENT);
            if(is_code_sent){
                codeSent = true;
                codeText.setVisibility(View.VISIBLE);
                resend_code_btn.setVisibility(View.VISIBLE);
                profile_continue_btn.setVisibility(View.GONE);
                timer_txt.setVisibility(View.VISIBLE);
                timeInMillSec = savedInstanceState.getLong(CURRENT_TIMER);
                edt_phone_number.setEnabled(false);
                ccp.setEnabled(false);
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
        outState.putString(PHONE_NO,edt_phone_number.getText().toString());
        outState.putString(USER_NAME,user_name);
        outState.putString(IMAGE_URL,image_url);
        outState.putString(PHONE_NUMBER,phoneNumber);
        outState.putBundle(BUNDLE,bundle);
        if(codeSent){
            outState.putBoolean(IS_CODE_SENT,true);
            outState.putLong(CURRENT_TIMER,timeInMillSec);
        }


        super.onSaveInstanceState(outState);
    }

    private void linkInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        user.linkWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            //FirebaseUser user = task.getResult().getUser();
                            //updateUI(user);
                            updateToFireStore();
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                CustomToast.makeErrorToast(UserInfoActivity.this, "Verification code is invalid", Toast.LENGTH_LONG).show();
                                resetCodeFields();
                            }
                            else {
                                CustomToast.makeErrorToast(UserInfoActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                resetUI();
                            }

                        }

                        // ...
                    }
                });

    }

    private void resetUI() {
        codeSent = false;
        resetCodeFields();
        countDownTimer.cancel();
        timeInMillSec = 120000;


        if(bundle == null){
            edt_phone_number.setEnabled(true);
        }
        profile_continue_btn.setVisibility(View.VISIBLE);
        codeText.setVisibility(View.GONE);
        resend_code_btn.setVisibility(View.GONE);
        timer_txt.setVisibility(View.GONE);

    }

    private void updateToFireStore() {
        Map<String,Object> user_info = new HashMap<>();
        Map<String,Object> contact = new HashMap<>();

        contact.put("phone_no",phoneNumber);
        user_info.put("contacts",contact);
        user_info.put("profile_image_url",image_url);
        user_info.put("user_name",user_name);
        userRef.document(user.getUid()).update(user_info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startAccessLocationActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomToast.makeErrorToast(UserInfoActivity.this,"Something Went Wrong! Please Try Again Later!!",Toast.LENGTH_SHORT).show();
                profile_continue_btn.setVisibility(View.VISIBLE);
                is_tried_once = true;
            }
        });

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

    private void sendVerificationCode() {

        phoneNumber = ccp.getFullNumberWithPlus();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                UserInfoActivity.this,               // Activity (for callback binding)
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

    private boolean validate_userName() {
        String username = edt_profile_username.getEditText().getText().toString().trim();
        return !username.isEmpty();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setUserInfo() {



        if(user == null){
            return;
        }


        if(bundle != null && user.getPhoneNumber()!= null){
            String login_method = bundle.getString("login_method");
            if(login_method.equals("phone")){
                //Toast.makeText(this, "phone", Toast.LENGTH_SHORT).show();
                ccp.setFullNumber(user.getPhoneNumber());
                edt_phone_number.setEnabled(false);
                ccp.setEnabled(false);
                phoneNumber = user.getPhoneNumber();

            }
        }


        if(user.getPhotoUrl() != null){
            Log.d("akash_debug", "setUserInfo: " + user.getPhotoUrl());
            image_url = String.valueOf(user.getPhotoUrl());
            Glide.with(UserInfoActivity.this).load(user.getPhotoUrl()).into(profile_photo);

        }

        if (user.getDisplayName() != null){
            edt_profile_username.getEditText().setText(user.getDisplayName());
        }
        else {
            String username = "Adme User";
            edt_profile_username.getEditText().setText(username);
        }


    }

    private void initializeFields() {

        choose_photo = findViewById(R.id.choose_photo);
        profile_photo = findViewById(R.id.profile_photo);
        edt_profile_username = findViewById(R.id.edt_profile_username);
        profile_continue_btn = findViewById(R.id.profile_continue_btn);
        user_info_top_back = findViewById(R.id.user_info_top_back);
        edt_phone_number = findViewById(R.id.edt_phone_number);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(edt_phone_number);
        ccp.detectSIMCountry(true);
        ccp.setCustomMasterCountries("NA,BD");
        codeText = findViewById(R.id.codeText);
        resend_code_btn = findViewById(R.id.resend_code_btn);
        timer_txt = findViewById(R.id.timer_txt);
        bundle= getIntent().getExtras();

        dialog = new LoadingDialog(UserInfoActivity.this,"none","none");
        user = FirebaseAuth.getInstance().getCurrentUser();
        setUserInfo();


        editText0 = findViewById(R.id.editText);
        editText1 = findViewById(R.id.editText6);
        editText2 = findViewById(R.id.editText5);
        editText3 = findViewById(R.id.editText4);
        editText4 = findViewById(R.id.editText3);
        editText5 = findViewById(R.id.editText2);
        editText0.addTextChangedListener(new UserInfoActivity.LoginOTPTextListener(editText0));
        editText1.addTextChangedListener(new UserInfoActivity.LoginOTPTextListener(editText1));
        editText2.addTextChangedListener(new UserInfoActivity.LoginOTPTextListener(editText2));
        editText3.addTextChangedListener(new UserInfoActivity.LoginOTPTextListener(editText3));
        editText4.addTextChangedListener(new UserInfoActivity.LoginOTPTextListener(editText4));
        editText5.addTextChangedListener(new UserInfoActivity.LoginOTPTextListener(editText5));
        db = FirebaseFirestore.getInstance();
        userRef = db.collection(FirebaseUtilClass.USER_COLLECTION_ID);


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
            linkInWithPhoneAuthCredential(credential);

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


    private void uploadImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //Check for Permission
            if (checkPermission()){
                //Permission is already Granted
                PickImageFromGallery();
            }
            else{
                //permission is not granted
                requestPermission();
            }

        }

        else {
            //Do  Not Need Permission
            PickImageFromGallery();

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {


        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(UserInfoActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);

            builder.setTitle("External Storage Permission").setMessage("External storage permission is must to read your image gallery")
                    .setPositiveButton("Proceed", (dialog, which) -> {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(UserInfoActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(UserInfoActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    //Handle Request Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(UserInfoActivity.this, "Called", Toast.LENGTH_SHORT).show();

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                /*** If Storage Permission Is Given, Check External storage is available for read and write***/
                PickImageFromGallery();
                //Toast.makeText(UserInfoActivity.this, "Permission Granted.", Toast.LENGTH_LONG).show();

            }

            else {

                Toast.makeText(UserInfoActivity.this, "You Should Allow External Storage Permission To Upload image from gallery.", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void PickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, UserInfoActivity.IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){


            assert data != null;
            imageUri = data.getData();
            //profile_photo.setImageURI(data.getData());
            Glide.with(UserInfoActivity.this)
                    .load(data.getData())
                    .fitCenter()
                    .into(profile_photo);
            is_image_select = true;
        }
    }

    private void startAccessLocationActivity() {
        Intent intent = new Intent(UserInfoActivity.this, AccessLocationActivity.class);
        startActivity(intent);
    }


    public void uploadProfilePhoto(Uri uri){

        dialog.show();
        dialog.updateTitle("Uploading images");
        dialog.updateProgress("uploading: 0 mb");
        StorageReference profile_pic_ref = storage.getReference().child(FirebaseUtilClass.STORAGE_FOLDER_PROFILE_PICTURE);
        StorageReference image = profile_pic_ref.child(uri.getLastPathSegment());
        UploadTask uploadTask = image.putFile(uri);




        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return image.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    image_url = String.valueOf(downloadUri);


                    //user logged in with phone
                    if(bundle != null && user.getPhoneNumber()!= null){
                        String login_method = bundle.getString("login_method");
                        if(login_method.equals("phone")){
                            updateToFireStore();
                        }
                    }
                    else {
                        sendVerificationCode();
                    }


                } else {
                    // Handle failures
                    // ...
                }
            }
        });


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                CustomToast.makeErrorToast(UserInfoActivity.this,"Something Went Wrong! Please Try Again!!",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getUploadSessionUri();
                Log.d("akash_debug", "onSuccess: "+taskSnapshot.getUploadSessionUri());
                dialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                //long percent = (taskSnapshot.getBytesTransferred()/ finalTotal_size) * 100;
                String mb = String.format("%.2f",taskSnapshot.getBytesTransferred()/125000.0);
                dialog.updateProgress("Uploading: "+mb+" mb");
            }
        });

    }
}

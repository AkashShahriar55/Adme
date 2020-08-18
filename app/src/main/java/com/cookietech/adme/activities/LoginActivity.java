package com.cookietech.adme.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Architecture.LoginViewModel;
import com.cookietech.adme.Helpers.CustomToast;
import com.cookietech.adme.Helpers.LoadingDialog;
import com.cookietech.adme.Helpers.User;
import com.cookietech.adme.R;

import com.cookietech.adme.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements FirebaseUtilClass.CreateUserCommunicator {

    private Button login_skip_btn,login_google_btn,login_facebook_btn,login_phone_btn;

    public static final String WEB_CLIENT_ID =  "521991296159-sv73atipiqjmpkun0pmq7oiq1qnqe31v.apps.googleusercontent.com";

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private LoadingDialog dialog;

    //create database reference
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseUtilClass firebaseUtilClass = FirebaseUtilClass.getInstance();

    private User new_user;
    private boolean isLocationSettingShowed = false;

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        /*firstUsePreferences = getSharedPreferences(String.valueOf(R.string.SHARED_PREFERENCE_FIRST_USE),MODE_PRIVATE);
        isLocationSettingShowed = firstUsePreferences.getBoolean(String.valueOf(R.string.SP_IS_LOCATION_SETTING_SHOWED),false);*/
        initializeFields();


        //Goto Registration page when click on create an account text

        login_skip_btn.setOnClickListener(v ->{

        });

        binding.loginGoogleBtn.setOnClickListener(v ->{
            signInWithGoogle();
        } );

        login_facebook_btn.setOnClickListener(v ->{
            signInWithFacebook();
        } );

        login_phone_btn.setOnClickListener(v -> {

            //start Registration Activity
            startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));

        });




    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startAccessLocationActivity() {
        Intent intent = new Intent(LoginActivity.this, AccessLocationActivity.class);
        startActivity(intent);
    }

    private void startLandingActivity() {
        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(intent);
    }

    private void startUserInfoActivity() {
        Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
        intent.putExtra("user_data",new_user);
        startActivity(intent);
    }





    private void initializeFields() {
        ConstraintLayout container = findViewById(R.id.login_container);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animate);
        container.startAnimation(hyperspaceJumpAnimation);

        login_skip_btn = findViewById(R.id.login_skip_btn);
        login_google_btn = findViewById(R.id.login_google_btn);
        login_facebook_btn = findViewById(R.id.login_facebook_btn);
        login_phone_btn= findViewById(R.id.login_phone_btn);
        dialog = new LoadingDialog(this,"Logging in","Please wait...");

        mAuth = FirebaseAuth.getInstance();

        //For Google SignUp
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /** To hide the keyboard after clicking a  button**/
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithFacebook() {

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                dialog.show();
                handleFacebookAccessToken(loginResult.getAccessToken());


            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                dialog.show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        else{
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;
                        createUser(user);




                    } else {

                        dialog.dismiss();

                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

                        CustomToast.makeErrorToast(LoginActivity.this,"Authentication Failed. Please Try Again",Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
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


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        createUser(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        dialog.show();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        CustomToast.makeErrorToast(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }



    @Override
    public void onBackPressed() {
        if(Build.VERSION.SDK_INT>=16){
            finishAffinity();
        } else{
            finish();
            System.exit(0);
        }
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
                CustomToast.makeErrorToast(LoginActivity.this,"User creation Failed !",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

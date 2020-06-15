package com.example.adme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.LoadingDialog;
import com.example.adme.Helpers.User;
import com.example.adme.R;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements FirebaseUtilClass.CreateUserCommunicator {

    private TextView txt_create_account;
    private Button login_skip_btn,login_google_btn,login_facebook_btn,login_btn;
    private TextInputLayout login_email, login_password;

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private LoadingDialog dialog;

    //create database reference
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection(FirebaseUtilClass.USER_COLLECTION_ID);

    private User mCurrentUser ;

    private FirebaseUtilClass firebaseUtilClass = new FirebaseUtilClass();

    private View contextView;

    private SharedPreferences firstUsePreferences;
    private boolean isLocationSettingShowed = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        contextView = findViewById(R.id.contextView);

        firstUsePreferences = getSharedPreferences(String.valueOf(R.string.SHARED_PREFERENCE_FIRST_USE),MODE_PRIVATE);
        isLocationSettingShowed = firstUsePreferences.getBoolean(String.valueOf(R.string.SP_IS_LOCATION_SETTING_SHOWED),false);
        initializeFields();


        //Goto Registration page when click on create an account text
        txt_create_account.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        login_skip_btn.setOnClickListener(v ->{

        });

        login_google_btn.setOnClickListener(v -> signInWithGoogle());

        login_facebook_btn.setOnClickListener(v -> signInWithFacebook());

        login_btn.setOnClickListener(v -> {
            if(!login_email.getEditText().getText().toString().trim().isEmpty() && !login_email.getEditText().getText().toString().trim().isEmpty()){
                String email = Objects.requireNonNull(login_email.getEditText()).getText().toString();
                String pass = Objects.requireNonNull(login_password.getEditText()).getText().toString();
                signInWithEmailAndPassword(email,pass);
            }else{
                Snackbar.make(contextView,"Enter your email and password",Snackbar.LENGTH_SHORT).show();
            }

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



    private void initializeFields() {
        ConstraintLayout container = findViewById(R.id.login_container);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animate);
        container.startAnimation(hyperspaceJumpAnimation);

        txt_create_account = findViewById(R.id.txt_create_account);
        login_skip_btn = findViewById(R.id.login_skip_btn);
        login_google_btn = findViewById(R.id.login_google_btn);
        login_facebook_btn = findViewById(R.id.login_facebook_btn);
        login_btn= findViewById(R.id.login_btn);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        dialog = new LoadingDialog(this,"Logging in",null);

        mAuth = FirebaseAuth.getInstance();

        //For Google SignUp
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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
                        firebaseUtilClass.createUser(user,this);



                    } else {

                        dialog.show();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

                        Toast.makeText(LoginActivity.this,"Authentication Failed. Please Try Again",Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
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
                        firebaseUtilClass.createUser(user,this);
                    } else {
                        // If sign in fails, display a message to the user.
                        dialog.show();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }



    private void signInWithEmailAndPassword(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                         FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                        //updateUI(user);
                        dialog.show();
                        firebaseUtilClass.createUser(user,LoginActivity.this);


                    }else{
                        Snackbar.make(contextView,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
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
}

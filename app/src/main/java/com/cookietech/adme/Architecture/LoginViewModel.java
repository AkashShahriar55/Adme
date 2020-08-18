package com.cookietech.adme.Architecture;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.cookietech.adme.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginViewModel extends AndroidViewModel {
    public static final String TAG = LoginViewModel.class.getName();
    private Application application;
    private SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance();
    private GoogleSignInClient mGoogleSignInClient;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        Log.d(TAG, "LoginViewModel: constructed");
    }


    public void signUpWithGoogle() {
        //For Google SignUp
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(application.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(application, gso);
    }
}

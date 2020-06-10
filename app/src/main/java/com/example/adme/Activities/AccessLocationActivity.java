package com.example.adme.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adme.Helpers.FirebaseUtilClass;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.LoadingDialog;
import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.PermissionHelper;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessLocationActivity extends AppCompatActivity implements GoogleMapHelper.GoogleMapHelperCommunicator {
    private static final String TAG = "AccessLocationActivity";
    boolean location_setting_checked = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    boolean isUseCurrentLocationButtonClicked = false;
    boolean isFindALocationButtonClicked = false;

    //firebase
    //create database reference
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Adme_User");

    //google map helper
    private GoogleMapHelper googleMapHelper = new GoogleMapHelper(this,this);

    //Firebase utility class
    private FirebaseUtilClass firebaseUtilClass = new FirebaseUtilClass();


    User mCurrentUser;

    private LoadingDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_location);
        Button useCurrentLocation = findViewById(R.id.use_current_location_btn);
        Button findALocation = findViewById(R.id.find_a_location_btn);
        TextView skipNowBtn = findViewById(R.id.skip_access_location_button);

        initialization();

        mCurrentUser = (User) getIntent().getParcelableExtra("current_user");

        if(PermissionHelper.requestLocationPermission(this)){
            checkSettings();
        }

        useCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUseCurrentLocationButtonClicked = true;
                if(location_setting_checked){

                    startLocationUpdates();
                }else{
                    if(PermissionHelper.requestLocationPermission(AccessLocationActivity.this)){
                        checkSettings();
                    }
                }
            }
        });

        findALocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFindALocationButtonClicked = true;
                if(location_setting_checked){
                    startLocationUpdates();
                }else{
                    if(PermissionHelper.requestLocationPermission(AccessLocationActivity.this)){
                        checkSettings();
                    }
                }
            }
        });

        skipNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLandingActivity(mCurrentUser);
            }
        });


    }

    private void initialization() {
        dialog = new LoadingDialog(this,"Welcome");
    }

    private void startLandingActivity(User currentUser) {
        dialog.dismiss();
        Intent intent = new Intent(this, LandingActivity.class);
        intent.putExtra(FirebaseUtilClass.CURRENT_USER_ID,currentUser);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }else{
                    for (Location location : locationResult.getLocations()) {
                        if(location != null){
                            Log.i(TAG, "onLocationResult: "+location.getLongitude() + location.getLatitude());
                            fusedLocationProviderClient.removeLocationUpdates(this);
                            String latitude = String.valueOf(location.getLatitude());
                            String longitude = String.valueOf(location.getLongitude());
                            googleMapHelper.getCurrentLocationAddress(latitude,longitude);
                        }
                    }
                }
            }
        };
    }

    private void startFindLocationActivity(User mCurrentUser,MyPlaces place) {
        Intent intent = new Intent(AccessLocationActivity.this,FindLocationActivity.class);
        intent.putExtra(FirebaseUtilClass.CURRENT_USER_ID,mCurrentUser);
        intent.putExtra("current_place",place);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode){
            case PermissionHelper.REQUEST_CHECK_SETTINGS:
                if(resultCode == RESULT_OK){
                    Log.i(TAG, "onActivityResult: setting was set");
                    location_setting_checked = true;
                    if(isUseCurrentLocationButtonClicked){
                        startLocationUpdates();
                    }
                    if(isFindALocationButtonClicked){
                        startLocationUpdates();
                    }
                }else{
                    Log.i(TAG, "onActivityResult: setting was not set");
                    location_setting_checked = false;
                }
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettings();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: permission denied");
                location_setting_checked = false;
            }
        }
    }




    private void checkSettings(){
        PermissionHelper.checkSettingsForLocation(this).addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(AccessLocationActivity.this,
                            PermissionHelper.REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    Log.i(TAG, "onFailure: " + sendEx.getLocalizedMessage());
                }
            }
        }).addOnSuccessListener((OnSuccessListener) o -> {
            if(isUseCurrentLocationButtonClicked){
                startLocationUpdates();
            }
            if(isFindALocationButtonClicked){
                startLocationUpdates();
            }
            location_setting_checked = true;
            Log.i(TAG, "onRequestPermissionsResult: setting was set");
        });
    }



    private void startLocationUpdates() {
        if(isUseCurrentLocationButtonClicked){
            dialog.show();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onPlacesSearchComplete(List<MyPlaces> placesList) {

    }

    @Override
    public void onCurrentLocationAddressFetched(MyPlaces place) {
        if(isUseCurrentLocationButtonClicked){
            firebaseUtilClass.updateUserLocation(mCurrentUser, place, user -> startLandingActivity(user));
        }else if(isFindALocationButtonClicked){
            startFindLocationActivity(mCurrentUser,place);
        }

    }
}

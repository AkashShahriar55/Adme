package com.example.adme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adme.Helpers.FirebaseUtilClass;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.LoadingDialog;
import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class FindLocationActivity extends AppCompatActivity implements GoogleMapHelper.GoogleMapHelperCommunicator, PlaceSearchAdapter.PlaceSearchItemListener {
    private static final String TAG = "FindLocationActivity";

    private List<MyPlaces> placesList = new ArrayList<>();
    private PlaceSearchAdapter adapter;
    private User mCurrentUser;
    private ProgressBar placeSearchProgressBar;
    private MyPlaces currentPlace;
    GoogleMapHelper mapHelper;

    //firebase
    //create database reference
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Adme_User");

    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_location);

        mapHelper = new GoogleMapHelper(this,this);
        mCurrentUser = getIntent().getParcelableExtra(FirebaseUtilClass.CURRENT_USER_ID);

        initialization();

        EditText placeSearchEditText = findViewById(R.id.place_search_edittext);
        Button placeSearchButton = findViewById(R.id.place_search_btn);

        placeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!placeSearchEditText.getText().toString().trim().isEmpty()){
                    placeSearchProgressBar.setVisibility(View.VISIBLE);
                    String query = placeSearchEditText.getText().toString().trim();
                    mapHelper.searchPlaces(query, mCurrentUser.getLatitude(), mCurrentUser.getLongitude());
                    // Check if no view has focus:
                    hideKeyboard();
                }
            }
        });

    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initialization() {
        dialog = new LoadingDialog(this,"Welcome");
        RecyclerView recyclerView = findViewById(R.id.places_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceSearchAdapter(this,placesList,this);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d(TAG, "onChanged: ");
            }
        });

        mapHelper.getCurrentLocationAddress(mCurrentUser.getLatitude(),mCurrentUser.getLongitude());

        placeSearchProgressBar = findViewById(R.id.place_search_progressbar);

        ConstraintLayout currentLocationItem = findViewById(R.id.place_search_item);
        currentLocationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();


                updateUserLocation(currentPlace);
            }
        });


    }

    private void startLandingActivity(User currentUser) {
        dialog.dismiss();
        Intent intent = new Intent(FindLocationActivity.this, LandingActivity.class);
        intent.putExtra(FirebaseUtilClass.CURRENT_USER_ID,currentUser);
        startActivity(intent);
    }

    @Override
    public void onPlacesSearchComplete(List<MyPlaces> placesList) {
        placeSearchProgressBar.setVisibility(View.GONE);
        Log.d(TAG, "onPlacesSearchComplete: "+ placesList.size());
        this.placesList = placesList;
        Log.d(TAG, "onPlacesSearchComplete: "+ this.placesList.size());
        adapter.setMyPlacesList(placesList);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onCurrentLocationAddressFetched(MyPlaces place) {
        TextView currentLocationName = findViewById(R.id.place_name);
        TextView currentLocationDetails = findViewById(R.id.place_details);
        currentPlace = place;
        currentLocationName.setText(place.getName());
        currentLocationDetails.setText(place.getFormattedAddress());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onPlaceSelected(MyPlaces place) {
        dialog.show();
        updateUserLocation(place);

    }

    private void updateUserLocation(MyPlaces place) {
        mCurrentUser.setLatitude(String.valueOf(place.getLatitude()));
        mCurrentUser.setLongitude(String.valueOf(place.getLongitude()));
        userRef.document(mCurrentUser.getUserId()).set(mCurrentUser, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startLandingActivity(mCurrentUser);
            }
        });
    }
}

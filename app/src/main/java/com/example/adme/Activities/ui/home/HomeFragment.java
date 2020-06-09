package com.example.adme.Activities.ui.home;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.adme.Activities.LandingActivity;
import com.example.adme.Activities.ui.today.AdServiceAdapter;
import com.example.adme.Activities.ui.today.Notification_Fragment;
import com.example.adme.Activities.ui.today.TodayBottomDetailsFragment;
import com.example.adme.Activities.ui.today.TodayFragment;
import com.example.adme.Activities.ui.today.TodayViewModel;
import com.example.adme.Activities.ui.today.ViewServiceDetails;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static androidx.constraintlayout.motion.widget.MotionScene.TAG;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeFragment";

    private HomeViewModel mViewModel;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private TodayViewModel homeViewModel;
    private GoogleMap mMap;
    private FloatingActionButton locationButton;
    private ImageView client_notification_btn, bottomDetailsButton;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.home_fragment, container, false);
        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {

        checkPermission();
        RecyclerView available_service_rv = root.findViewById(R.id.available_service_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        available_service_rv.setLayoutManager(layoutManager);
        available_service_rv.setHasFixedSize(true);
        AvailableServiceAdapter available_service_adapter = new AvailableServiceAdapter("home");
        available_service_rv.setAdapter(available_service_adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationButton = view.findViewById(R.id.client_location_button);
        client_notification_btn = view.findViewById(R.id.client_notification_btn);
        bottomDetailsButton = view.findViewById(R.id.bottom_details_button);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

        locationButton.setOnClickListener(v -> checkPermission());

        client_notification_btn.setOnClickListener(v -> goToNotificationFragment());

        bottomDetailsButton.setOnClickListener(v -> goToBottomDetails());
    }

    @Override
    public void onStart() {
        super.onStart();
        mCurrentUser = ((LandingActivity)requireActivity()).getmCurrentUser();

    }

    private void markUserLocationInMap() {
        Double lat = Double.valueOf(mCurrentUser.getLatitude());
        Double lng = Double.valueOf(mCurrentUser.getLongitude());
        LatLng currentLocation = new LatLng(lat,lng);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true).title(requireContext().getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,DEFAULT_ZOOM));
    }

    private void checkPermission() {
        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                builder.setTitle("Location Permission").setMessage("Location permission is must for the map features")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(requireActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            if(mMap == null){
                setUpMap();
            }else{
                if(mCurrentUser != null){
                    if(mCurrentUser.getLatitude() != null && mCurrentUser.getLongitude() != null){
                        markUserLocationInMap();
                    }else{
                        GoogleMapHelper.markCurrentLocation(getContext(),mMap);
                    }
                }
            }

        }
    }

    private void setUpMap() {
        new Thread(() -> {
            try {
                SupportMapFragment mf = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction()
                        .add(R.id.map, mf)
                        .commit();
                requireActivity().runOnUiThread(() -> mf.getMapAsync(HomeFragment.this));
            }catch (Exception ignored){

            }
        }).start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
        if(mCurrentUser != null){
            if(mCurrentUser.getLatitude() != null && mCurrentUser.getLongitude() != null){
                markUserLocationInMap();
            }else{
                GoogleMapHelper.markCurrentLocation(getContext(),mMap);
            }
        }

    }


    private void goToNotificationFragment() {

        Fragment notificationFragment = new Notification_Fragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.replace(R.id.nav_host_fragment, notificationFragment);
        fragmentTransaction.commit();
    }

    private void goToBottomDetails() {
        new Thread(() -> {
            Log.i(TAG, "run: "+Thread.currentThread().getName());
            Fragment nextFragment = new HomeBottomDetailsFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.nav_host_fragment, nextFragment);
            fragmentTransaction.commit();

        }).start();

        Log.i(TAG, "run: "+Thread.currentThread().getName());


    }

}

package com.example.adme.Activities.ui.today;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TodayFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "TodayFragment";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private TodayViewModel homeViewModel;
    private GoogleMap mMap;
    private boolean isOnline = false;
    private ConstraintLayout todayIncome,todayDue,todayPressed,todayRequested,todayCompleted;
    private ImageView bottomDetailsButton,notificationButton;
    private View bottomSheet;
    private FloatingActionButton locationButton;
    private Switch todayStatusSwitch;
    private BottomSheetBehavior bottomSheetBehavior ;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_today, container, false);

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        todayCompleted.setOnClickListener(v -> goToBottomDetails());
        todayIncome.setOnClickListener(v -> goToBottomDetails());
        todayDue.setOnClickListener(v -> goToBottomDetails());
        todayPressed.setOnClickListener(v -> goToBottomDetails());
        todayRequested.setOnClickListener(v -> goToBottomDetails());
        bottomDetailsButton.setOnClickListener(v -> goToBottomDetails());




        locationButton.setOnClickListener(v -> checkPermission());



        notificationButton.setOnClickListener(v -> {
            goToNotificationFragment();
        });



        todayStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                buttonView.setText(R.string.online_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_active));
                isOnline = true;
            }else{
                buttonView.setText(R.string.offline_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_not_active));
                isOnline = false;
            }
        });

       checkPermission();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        bottomDetailsButton = view.findViewById(R.id.bottom_details_button);


        bottomSheet = view.findViewById(R.id.bottom_details);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        todayCompleted = view.findViewById(R.id.today_completed);
        todayIncome = view.findViewById(R.id.today_income);
        todayDue = view.findViewById(R.id.today_due);
        todayPressed = view.findViewById(R.id.today_pressed);
        todayRequested = view.findViewById(R.id.today_requested);
        locationButton = view.findViewById(R.id.today_location_button);
        notificationButton = view.findViewById(R.id.today_notification_btn);
        todayStatusSwitch = view.findViewById(R.id.today_status_switch);


    }


    private void goToNotificationFragment() {

        Fragment notificationFragment = new Notification_Fragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.replace(R.id.nav_host_fragment, notificationFragment);
        fragmentTransaction.commit();
    }





    private void goToBottomDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: "+Thread.currentThread().getName());
                Fragment nextFragment = new TodayBottomDetailsFragment(isOnline);
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.nav_host_fragment, nextFragment);
                fragmentTransaction.commit();

            }
        }).start();

        Log.i(TAG, "run: "+Thread.currentThread().getName());


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
            setUpMap();
        }
    }

    private void setUpMap() {
        new Thread(() -> {
            try {
                SupportMapFragment mf = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction()
                        .add(R.id.map, mf)
                        .commit();
                requireActivity().runOnUiThread(() -> mf.getMapAsync(TodayFragment.this));
            }catch (Exception ignored){

            }
        }).start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
    }





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}

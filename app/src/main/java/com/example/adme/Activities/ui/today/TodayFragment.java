package com.example.adme.Activities.ui.today;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.LandingActivity;
import com.example.adme.Helpers.FirebaseUtilClass;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TodayFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
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

    //create database reference
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference userRef = db.collection("Adme_User");
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser =mAuth.getCurrentUser();

    private User mCurrentUser ;
    private Map<String,String> location;

    private TextView tv_income_today,tv_due, tv_pending_today, tv_appointments_today,tv_completed_today,tv_income_total;
    RecyclerView appointmentRecyclerView,serviceRecyclerView;
    Button todayAddService;

    CoordinatorLayout layout_coordinator;
    CardView bottom_details_toolbar;
    RecyclerView.Adapter appointmentAdapter,serviceAdapter;
    boolean isMapLoaded=false;
    boolean isbottomSheetVisible=false;
    int oldPeekHeight;

    List<Service> services = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_today, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        updateUi();
        super.onActivityCreated(savedInstanceState);
    }

    private void initialization(View view) {
        bottomSheet = view.findViewById(R.id.bottom_details);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        layout_coordinator = view.findViewById(R.id.layout_coordinator);
        bottom_details_toolbar = view.findViewById(R.id.bottom_details_toolbar);

//        todayCompleted = view.findViewById(R.id.today_completed);
//        todayIncome = view.findViewById(R.id.today_income);
//        todayDue = view.findViewById(R.id.today_due);
//        todayPressed = view.findViewById(R.id.today_pressed);
//        todayRequested = view.findViewById(R.id.today_requested);

//        todayCompleted.setOnClickListener(v -> goToBottomDetails());
//        todayIncome.setOnClickListener(v -> goToBottomDetails());
//        todayDue.setOnClickListener(v -> goToBottomDetails());
//        todayPressed.setOnClickListener(v -> goToBottomDetails());
//        todayRequested.setOnClickListener(v -> goToBottomDetails());
//        bottomDetailsButton.setOnClickListener(v -> goToBottomDetails());

        tv_income_today = view.findViewById(R.id.tv_income_today);
        tv_due =view.findViewById(R.id.tv_due);
        tv_completed_today = view.findViewById(R.id.tv_completed_today);
        tv_appointments_today = view.findViewById(R.id.tv_appointments_today);
        tv_pending_today = view.findViewById(R.id.tv_pending_today);
        tv_income_total = view.findViewById(R.id.tv_total_income);

        appointmentRecyclerView = view.findViewById(R.id.appointment_container);
        serviceRecyclerView = view.findViewById(R.id.service_container);

        notificationButton = view.findViewById(R.id.client_notification_btn);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotificationFragment();
            }
        });

        todayStatusSwitch = view.findViewById(R.id.today_status_switch);
        todayStatusSwitch.setChecked(isOnline);
        if(isOnline){
            todayStatusSwitch.setText(R.string.online_status);
            todayStatusSwitch.setTextColor(getResources().getColor(R.color.color_active));
        }else{
            todayStatusSwitch.setText(R.string.offline_status);
            todayStatusSwitch.setTextColor(getResources().getColor(R.color.color_not_active));
        }
        todayStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                buttonView.setText(R.string.online_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_active));
                mCurrentUser.setStatus(FirebaseUtilClass.STATUS_ONLINE);
                isOnline = true;
            }else{
                buttonView.setText(R.string.offline_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_not_active));
                mCurrentUser.setStatus(FirebaseUtilClass.STATUS_OFFLINE);
                isOnline = false;
            }
        });

        bottomDetailsButton = view.findViewById(R.id.bottom_details_button);
        bottomDetailsButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        todayAddService = view.findViewById(R.id.today_add_service);
        todayAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addServiceActivityIntent = new Intent(requireContext(),AddServicesActivity.class);
                requireContext().startActivity(addServiceActivityIntent);
            }
        });

        locationButton = view.findViewById(R.id.today_location_button);
        locationButton.setOnClickListener(v -> checkPermission());

    }

    private void updateUi() {
        mCurrentUser = ((LandingActivity)requireActivity()).getmCurrentUser();

        location = mCurrentUser.getLocation();
        setUpMap();

        Map<String,String> serviceProviderInfo = mCurrentUser.getService_provider_info();
        tv_income_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_INCOME_TODAY));
        tv_completed_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_COMPLETED_TODAY));
        tv_due.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_DUE));
        tv_pending_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_PENDING_TODAY));
        tv_appointments_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_APPOINTMENTS_TODAY));
        tv_income_total.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_INCOME_TOTAL));

        if(mCurrentUser.getStatus().equals(FirebaseUtilClass.STATUS_ONLINE)){
            todayStatusSwitch.setChecked(true);
        }else if(mCurrentUser.getStatus().equals(FirebaseUtilClass.STATUS_OFFLINE)){
            todayStatusSwitch.setChecked(false);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        appointmentRecyclerView.setHasFixedSize(true);
        appointmentRecyclerView.setLayoutManager(layoutManager);
        appointmentAdapter = new AppointmentAdapter(getContext(),getParentFragmentManager());

        RecyclerView.LayoutManager serviceLayoutManager = new LinearLayoutManager(getContext());
        serviceRecyclerView.setHasFixedSize(true);
        serviceRecyclerView.setLayoutManager(serviceLayoutManager);
        serviceAdapter = new ServiceAdapter(getContext(),services);

        oldPeekHeight = bottomSheetBehavior.getPeekHeight();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback (new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                        if(isbottomSheetVisible) {
                            bottomSheetBehavior.setPeekHeight(bottom_details_toolbar.getHeight()+5, true);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                    break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initialization(view);
    }

    private void goToNotificationFragment() {
        Fragment notificationFragment = new Notification_Fragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.nav_host_fragment,notificationFragment,"notificationFragment");
        fragmentTransaction.commit();
    }

    private void goToBottomDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: "+Thread.currentThread().getName());
                Fragment nextFragment = new TodayBottomDetailsFragment(mCurrentUser);
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.nav_host_fragment, nextFragment);
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
            if(mMap == null){
                setUpMap();
            }else{
                GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
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
                requireActivity().runOnUiThread(() -> mf.getMapAsync(TodayFragment.this));
            }catch (Exception ignored){

            }
        }).start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
        //GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
        if(location.get(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE)!=null &&location.get(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE)!=null){
            mMap.clear();
            double latitude = Double.parseDouble(Objects.requireNonNull(mCurrentUser.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE)));
            double longitude = Double.parseDouble(Objects.requireNonNull(mCurrentUser.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE)));
            LatLng currentLocation = new LatLng(latitude,longitude);
            mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true).title(requireContext().getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,DEFAULT_ZOOM));
        }else{
            GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mMap == null){
                        setUpMap();
                    }else{
                        GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
                    }

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

    @Override
    public void onMapLoaded() {
        isMapLoaded=true;
        updateView();
    }

    public void updateView() {
        if (isMapLoaded && !isbottomSheetVisible && ((LandingActivity)getActivity()).isTodayVisible()) {
            MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    appointmentRecyclerView.setAdapter(appointmentAdapter);
                    serviceRecyclerView.setAdapter(serviceAdapter);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    isbottomSheetVisible=true;
                    Log.d("LeaderBoardFragment", "queueIdle: called2");
                    return false;
                }
            };
            Looper.myQueue().addIdleHandler(handler);
        }
        bottomSheetBehavior.setPeekHeight(oldPeekHeight, true);
//        Log.d(TAG, "onViewCreated: "+bottomSheetBehavior.getPeekHeight());
    }

}

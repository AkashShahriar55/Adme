package com.example.adme.Activities.ui.today;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.LandingActivity;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.AppointmentRef;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.UiHelper;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class TodayFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {
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

    ConstraintLayout empty_recyclerview,empty_recyclerview_appointment;
    CoordinatorLayout layout_coordinator;
    CardView bottom_details_toolbar;
    RecyclerView.Adapter appointmentAdapter;
    ServiceAdapter serviceAdapter;
    boolean isMapLoaded=false;
    boolean isbottomSheetVisible=false;
    int oldPeekHeight;

    List<Map<String,String>> services = new ArrayList<>();
//    List<AppointmentRef> appointmentRefList = new ArrayList<>();
    List<Appointment> appointmentList = new ArrayList<>();

    TodayViewModel todayViewModel;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_today, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //updateUi();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        todayViewModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        final Observer<User> userDataObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mCurrentUser = user;
                todayViewModel.fatchActiveAppointmentList(user.getmUserId());
                CookieTechUtilityClass.setSharedPreferences("mUserId", user.getmUserId(), getContext());
                Log.d("view-model", "onChanged:  bottom details" + user.getStatus());
                if(user.getService_reference().size() > 0){
                    services = user.getService_reference();
                }
                updateUi();
            }
        };

        todayViewModel.getUserData().observe(requireActivity(),userDataObserver);

        todayViewModel.getAppointmentList().observe(this, new Observer<List<Appointment>>() {
            @Override
            public void onChanged(List<Appointment> appointments) {
                Log.d(TAG, "onChanged: appointments "+appointments.size());
                appointmentList.clear();
                appointmentList.addAll(appointments);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appointmentAdapter.notifyDataSetChanged();
                        if(appointmentList.size() == 0){
                            empty_recyclerview_appointment.setVisibility(View.VISIBLE);
                        }else{
                            empty_recyclerview_appointment.setVisibility(View.GONE);
                        }

                        updateMapMarkers();
                    }
                });
            }
        });

    }

    private void updateMapMarkers() {


    }

    private void initialization(View view) {
        bottomSheet = view.findViewById(R.id.bottom_details);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        layout_coordinator = view.findViewById(R.id.layout_coordinator);
        bottom_details_toolbar = view.findViewById(R.id.bottom_details_toolbar);


        tv_income_today = view.findViewById(R.id.tv_income_today);
        tv_due =view.findViewById(R.id.tv_due);
        tv_completed_today = view.findViewById(R.id.tv_completed_today);
        tv_appointments_today = view.findViewById(R.id.tv_pressed_today);
        tv_pending_today = view.findViewById(R.id.tv_requested_today);
        tv_income_total = view.findViewById(R.id.tv_total_income);

        appointmentRecyclerView = view.findViewById(R.id.appointment_container);
        serviceRecyclerView = view.findViewById(R.id.service_recyclerview);
        empty_recyclerview = view.findViewById(R.id.empty_recyclerview);
        empty_recyclerview_appointment = view.findViewById(R.id.empty_recyclerview_appointment);

        if(services.size() == 0){
            empty_recyclerview.setVisibility(View.VISIBLE);
        }else{
            empty_recyclerview.setVisibility(View.GONE);
        }

        if(appointmentList.size() == 0){
            empty_recyclerview_appointment.setVisibility(View.VISIBLE);
        }else{
            empty_recyclerview_appointment.setVisibility(View.GONE);
        }

        notificationButton = view.findViewById(R.id.client_notification_btn);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                goToNotificationFragment();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("mode", FirebaseUtilClass.MODE_SERVICE_PROVIDER);
                startActivity(intent);
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
                todayViewModel.updateStatus(FirebaseUtilClass.STATUS_ONLINE);
            }else{
                buttonView.setText(R.string.offline_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_not_active));
                mCurrentUser.setStatus(FirebaseUtilClass.STATUS_OFFLINE);
                isOnline = false;
                todayViewModel.updateStatus(FirebaseUtilClass.STATUS_OFFLINE);
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
                addServiceActivityIntent.putExtra(FirebaseUtilClass.CURRENT_USER_ID,mCurrentUser);
                requireContext().startActivity(addServiceActivityIntent);
            }
        });

        locationButton = view.findViewById(R.id.today_location_button);
        locationButton.setOnClickListener(v -> checkPermission());

        oldPeekHeight = bottomSheetBehavior.getPeekHeight();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback (new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                        if(isbottomSheetVisible) {
                            new UiHelper(requireContext()).setMargins(locationButton,0,0,0,70);
                            locationButton.requestLayout();
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        appointmentRecyclerView.setHasFixedSize(true);
        appointmentRecyclerView.setLayoutManager(layoutManager);
        appointmentAdapter = new AppointmentAdapter(getContext(), appointmentList);

        RecyclerView.LayoutManager serviceLayoutManager = new LinearLayoutManager(getContext());
        serviceRecyclerView.setHasFixedSize(true);
        serviceRecyclerView.setLayoutManager(serviceLayoutManager);
        serviceAdapter = new ServiceAdapter(getContext(),services,mCurrentUser);

    }

    private void updateUi() {
        //mCurrentUser = ((LandingActivity)requireActivity()).getmCurrentUser();

        location = mCurrentUser.getLocation();
        setUpMap();

        Map<String,String> serviceProviderInfo = mCurrentUser.getService_provider_info();
        tv_income_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_INCOME_TODAY));
        tv_completed_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_COMPLETED_TODAY));
        tv_due.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_DUE));
        tv_pending_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_REQUESTED_TODAY));
        tv_appointments_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_PRESSED_TODAY));
        tv_income_total.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_INCOME_TOTAL));

        if(mCurrentUser.getStatus().equals(FirebaseUtilClass.STATUS_ONLINE)){
            todayStatusSwitch.setChecked(true);
        }else if(mCurrentUser.getStatus().equals(FirebaseUtilClass.STATUS_OFFLINE)){
            todayStatusSwitch.setChecked(false);
        }

        serviceAdapter.setServices(services);
        serviceAdapter.setmCurrentUser(mCurrentUser);

        if(services.size() == 0){
            empty_recyclerview.setVisibility(View.VISIBLE);
        }else{
            empty_recyclerview.setVisibility(View.GONE);
        }

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
            updateCurrentAppointmentsPin();
            mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true).title(requireContext().getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,DEFAULT_ZOOM));
        }else{
            GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                return null;
            }
        });


        LatLng dhaka = new LatLng(23.777176,90.399452);
        Marker maarker = mMap.addMarker(new MarkerOptions().position(dhaka).title("test").icon(BitmapDescriptorFactory.fromBitmap(generateMarkerBitmap())));

        setMarkerBounce(maarker);



    }

    private void setMarkerBounce(final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed/duration), 0);
                marker.setAnchor(0.5f, 1.0f +  t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                } else {
                    setMarkerBounce(marker);
                }
            }
        });
    }

    private void updateCurrentAppointmentsPin() {
        LatLng location1 = new LatLng(24.8215038,88.3160202);
        LatLng location2 = new LatLng(24.8213869,88.3198963);
        mMap.addMarker(new MarkerOptions()
                .position(location1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.client))
                .title("location1")
                .snippet("something"));

        mMap.addMarker(new MarkerOptions()
                .position(location2)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.client))
                .title("location2")
                .snippet("something"));


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
                    new UiHelper(requireContext()).setMargins(locationButton,0,0,0,170);
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

    public Bitmap generateMarkerBitmap(){
        Bitmap background = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
        Canvas mainCanvas = new Canvas(background);


        Bitmap markerImage = BitmapFactory.decodeResource(getResources(),R.drawable.marker_with_photo);
        markerImage = Bitmap.createScaledBitmap(markerImage,200,200,false);
        mainCanvas.drawBitmap(markerImage,0,0,null);

        Bitmap roundedImage = Bitmap.createBitmap(130,
                130, Bitmap.Config.ARGB_8888);
        Canvas profileImageCanvas = new Canvas(roundedImage);
        Bitmap mainProfileImage = BitmapFactory.decodeResource(getResources(),R.drawable.test_image);
        mainProfileImage = Bitmap.createScaledBitmap(mainProfileImage,130,130,false);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, 130, 130);


        paint.setAntiAlias(true);
        profileImageCanvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        profileImageCanvas.drawCircle(65, 65,
                65, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        profileImageCanvas.drawBitmap(mainProfileImage, rect, rect, paint);

        mainCanvas.drawBitmap(roundedImage,35,8,null);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(background);
        Bitmap result = bitmapDrawable.getBitmap();
        return result;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}

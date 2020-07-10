package com.example.adme.Activities.ui.home;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adme.Activities.LandingActivity;
import com.example.adme.Activities.ui.today.NotificationActivity;
import com.example.adme.Activities.ui.today.Notification_Fragment;
import com.example.adme.Activities.ui.today.TodayViewModel;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Architecture.UserDataModel;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;
import com.example.adme.R;
import com.google.android.gms.common.util.ScopeUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.FirestoreGrpc;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback, ServiceSearchAdapter.ServiceSearchAdapterListener {
    private static final String TAG = "HomeFragment";
    private HomeViewModel mViewModel;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    private FloatingActionButton locationButton;
    private ImageView client_notification_btn, bottomDetailsButton;
    private RecyclerView search_service_rv;
    private List<Service> serviceProvidersList = new ArrayList<>();
    private ServiceSearchAdapter serviceSearchAdapter;
    private CardView cv_search;
    private User mCurrentUser;

    UserDataModel userDataModel;

    private Marker userLocation;


    private List<Service> localizedServiceList = new ArrayList<>();
    private boolean isLocalizedServiceListObserverSet = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.home_fragment, container, false);
        initializeFields(root);

        return root;
    }

    private void initializeFields(View root) {


        RecyclerView available_service_rv = root.findViewById(R.id.available_service_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        available_service_rv.setLayoutManager(layoutManager);
        available_service_rv.setHasFixedSize(true);
        AvailableServiceAdapter available_service_adapter = new AvailableServiceAdapter("home");
        available_service_rv.setAdapter(available_service_adapter);

        cv_search = root.findViewById(R.id.cv_search);
        cv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSearchFragment();
            }
        });
        //setUpMap();
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

        userDataModel = new ViewModelProvider(this).get(UserDataModel.class);
        userDataModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mCurrentUser = user;
                CookieTechUtilityClass.setSharedPreferences("mUserId", user.getmUserId(), getContext());
            }
        });

        locationButton.setOnClickListener(v -> checkPermission());
        client_notification_btn.setOnClickListener(v -> goToNotificationFragment());
        bottomDetailsButton.setOnClickListener(v -> goToBottomDetails());
    }

    @Override
    public void onStart() {
        super.onStart();
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        final Observer<User> userDataObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mCurrentUser = user;
                Log.d("akash_debug", "on user data update: " + localizedServiceList.size());
                checkPermission();


            }
        };

        homeViewModel.getUserData().observe(requireActivity(),userDataObserver);


    }


    private void setUpLocalizedServices(){
        homeViewModel.getAllServiceList().observe(this, new Observer<List<Service>>() {
            @Override
            public void onChanged(List<Service> services) {
                serviceProvidersList = services;
                Log.d("akash_debug", "on service provider list update: " + localizedServiceList.size());
                if(mCurrentUser.getLocation() != null && mMap != null){
                    Map<String,String> location = mCurrentUser.getLocation();
                    String latitude = location.get(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE);
                    String longitude = location.get(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE);
                    updateLocalizedServiceList(latitude,longitude);
                }
            }
        });
    }

    private  void updateLocalizedServiceList(String latitude,String longitude){

        double centerLat = Double.parseDouble(latitude);
        double centerLng = Double.parseDouble(longitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Service> localizedServiceListTemp = new ArrayList<>();
                for(Service service:serviceProvidersList){

                    double endLat = Double.parseDouble(Objects.requireNonNull(service.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE)));
                    double endLng = Double.parseDouble(Objects.requireNonNull(service.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE)));
                    float[] results = new float[1];
                    Location.distanceBetween(centerLat,centerLng,endLat,endLng,results);
                    float distanceInMeters = results[0];
                    if(distanceInMeters<1000000){
                        localizedServiceListTemp.add(service);
                    }
                }
                localizedServiceList = localizedServiceListTemp;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        setMarkerOfLocalizedServices();
                    }
                });

            }

        }).start();

    }

    private void setMarkerOfLocalizedServices() {
        Log.d("akash_debug", "on set marker update: " + localizedServiceList.size());
        for(Service service:localizedServiceList){
            Map<String,String> location = service.getLocation();
            double lat = Double.parseDouble(location.get(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE));
            double lng = Double.parseDouble(location.get(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE));
            LatLng latLng = new LatLng(lat,lng);
            Marker something = mMap.addMarker(new MarkerOptions().draggable(true).position(latLng).title(service.getUser_name()));
            Log.d("akash_debug", "setMarkerOfLocalizedServices: " + (mMap != null));
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void markUserLocationInMap() {
//        Double lat = Double.valueOf(mCurrentUser.getLatitude());
//        Double lng = Double.valueOf(mCurrentUser.getLongitude());
        //LatLng currentLocation = new LatLng(lat,lng);
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true).title(requireContext().getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,DEFAULT_ZOOM));
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
        if(mCurrentUser.getLocation()!= null){
            String latitude = mCurrentUser.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE);
            String longitude = mCurrentUser.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE);
            if( latitude != null && longitude != null){
                double lat = Double.parseDouble(latitude);
                double lng = Double.parseDouble(longitude);
                LatLng position = new LatLng(lat,lng);
                if(userLocation == null){
                    userLocation = mMap.addMarker(new MarkerOptions().draggable(true).position(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
                }else{
                    userLocation.setPosition(position);
                }

            }
        }
        GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
//        if(mCurrentUser != null){
//            if(mCurrentUser != null){
//                if(mCurrentUser.getLatLng() != null){
////                    markUserLocationInMap();
//                    GoogleMapHelper.markLocation(getContext(),mMap,mCurrentUser.getLatLng());
//                }else{
//                    GoogleMapHelper.markCurrentLocation(getContext(),mMap);
//                }
//            }
//        }
        setUpLocalizedServices();

    }

    @Override
    public void onServiceProviderSelected(Service serviceProvider) {
        Intent intent = new Intent(getContext(), ServiceProviderDetailsActivity.class);
        intent.putExtra("serviceProviderObject", serviceProvider);
        getContext().startActivity(intent);
//        Toast.makeText(getContext(), "Selected: " + serviceProvider.getUser_name() , Toast.LENGTH_LONG).show();
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

    private void goToNotificationFragment() {
//        Fragment notificationFragment = new Notification_Fragment();
//        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.add(R.id.nav_host_fragment, notificationFragment,"notificationFragment");
//        fragmentTransaction.commit();
        Intent intent = new Intent(getActivity(), NotificationActivity.class);
        intent.putExtra("mode", FirebaseUtilClass.MODE_CLIENT);
        startActivity(intent);
    }

    private void goToSearchFragment() {
//        Fragment searchFragment = new SearchFragment();
//        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.add(R.id.nav_host_fragment, searchFragment,"searchFragment");
//        fragmentTransaction.commit();
        Intent intent = new Intent(getActivity(), ServiceProviderSearchActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

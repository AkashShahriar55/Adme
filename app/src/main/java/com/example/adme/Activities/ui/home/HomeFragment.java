package com.example.adme.Activities.ui.home;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.adme.Activities.LandingActivity;
import com.example.adme.Activities.ui.today.Notification_Fragment;

import com.example.adme.Activities.ui.today.TodayBottomDetailsFragment;
import com.example.adme.Activities.ui.today.TodayFragment;

import com.example.adme.Activities.ui.today.TodayViewModel;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, ServiceSearchAdapter.ServiceSearchAdapterListener {


    private static final String TAG = "HomeFragment";

    private HomeViewModel mViewModel;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private TodayViewModel homeViewModel;
    private GoogleMap mMap;
    private FloatingActionButton locationButton;
    private ImageView client_notification_btn, bottomDetailsButton;

    private RecyclerView search_service_rv;
    private List<ServiceProvider> serviceProvidersList;
    private ServiceSearchAdapter serviceSearchAdapter;
    private SearchView serviceSearchView;

    FirebaseFirestore db;
    private User mCurrentUser;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        search_service_rv = root.findViewById(R.id.rv_service_result);
        serviceProvidersList = new ArrayList<>();
        serviceSearchAdapter = new ServiceSearchAdapter(getContext(), serviceProvidersList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        search_service_rv.setLayoutManager(mLayoutManager);
        search_service_rv.setItemAnimator(new DefaultItemAnimator());
        search_service_rv.setAdapter(serviceSearchAdapter);
//        getFirebaseData();
//        getFirebaseDataList();

        serviceSearchView = root.findViewById(R.id.searchView);
        serviceSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // searchView expanded
                    serviceProvidersList.clear();
                    serviceSearchAdapter.notifyDataSetChanged();
                    search_service_rv.setVisibility(View.VISIBLE);
                    locationButton.setVisibility(View.GONE);
                } else {
                    // searchView not expanded
                    serviceSearchView.setIconified(true);
                    serviceSearchView.clearFocus();
                    search_service_rv.setVisibility(View.GONE);
                    locationButton.setVisibility(View.VISIBLE);
                }
            }
        });
        serviceSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<String> queryList = new ArrayList<String>(Arrays.asList(query.split(" ")));
                getFirebaseQueryList(queryList);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<String> queryList = new ArrayList<String>(Arrays.asList(newText.split(" ")));
                getFirebaseQueryList(queryList);
                return false;
            }
        });
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
//        Double lat = Double.valueOf(mCurrentUser.getLatitude());
//        Double lng = Double.valueOf(mCurrentUser.getLongitude());
        //LatLng currentLocation = new LatLng(lat,lng);
        mMap.clear();
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
            }else{
                if(mCurrentUser != null){
//                    if(mCurrentUser.getLatitude() != null && mCurrentUser.getLongitude() != null){
//                        markUserLocationInMap();
//                    }else{
//                        GoogleMapHelper.markCurrentLocation(getContext(),mMap);
//                    }
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
//            if(mCurrentUser.getLatitude() != null && mCurrentUser.getLongitude() != null){
//                markUserLocationInMap();
//            }else{
//                GoogleMapHelper.markCurrentLocation(getContext(),mMap);
//            }
        }

    }

    private void goToNotificationFragment() {

        Fragment notificationFragment = new Notification_Fragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.replace(R.id.nav_host_fragment, notificationFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onServiceProviderSelected(ServiceProvider serviceProvider) {
        Toast.makeText(getContext(), "Selected: " + serviceProvider.getUser_name() , Toast.LENGTH_LONG).show();
    }

    private void getFirebaseData() {
        db = FirebaseFirestore.getInstance();
        ///Adme_Service_list/service1
//        final DocumentReference docRef = db.collection("Adme_Service_list").document("service1");
        final DocumentReference docRef = db.document("Adme_Service_list/service1");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    ServiceProvider sv= snapshot.toObject(ServiceProvider.class);
                    serviceProvidersList.add(sv);
                    serviceSearchAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Log.d(TAG, "Current2 data: " + sv.getUser_name());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void setFirebaseData(ServiceProvider sv, String doc){
        db.collection("Adme_Service_list").document(doc)
                .set(sv)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void getFirebaseDataList() {
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_Service_list")
                .whereArrayContainsAny("tag", Arrays.asList("artist", "paint"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        serviceProvidersList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            ServiceProvider sv= doc.toObject(ServiceProvider.class);
                            serviceProvidersList.add(sv);
                            serviceSearchAdapter.notifyDataSetChanged();

//                            for(int i=0; i<sv.getTag().size(); i++){
//                                sv.getTag().set(i , sv.getTag().get(i).toLowerCase());
//                                Log.d(TAG, doc.getId()+" Current3 data: " + sv.getTag().get(i));
//                            }
//                            setFirebaseData(sv, doc.getId());
                        }
//                        Log.d(TAG, "Current2 data: " + serviceProvidersList.get(3).getUser_name());
                    }
                });

    }

    private void getFirebaseQueryList(List<String> queryArray) {
        Log.d(TAG, " Current4 data: " + queryArray);
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_Service_list")
//                .startAt(queryArray.get(0))
//                .endAt(queryArray.get(0)+"\uf8ff")
                .whereArrayContainsAny("tag", queryArray)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        serviceProvidersList.clear();
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                        } else {
                            for (QueryDocumentSnapshot doc : value) {
                                ServiceProvider sv = doc.toObject(ServiceProvider.class);
                                serviceProvidersList.add(sv);

//                            for(int i=0; i<sv.getTag().size(); i++){
//                                sv.getTag().set(i , sv.getTag().get(i).toLowerCase());
//                                Log.d(TAG, doc.getId()+" Current3 data: " + sv.getTag().get(i));
//                            }
//                            setFirebaseData(sv, doc.getId());

                            }
                        }
                        serviceSearchAdapter.notifyDataSetChanged();
                    }
                });
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

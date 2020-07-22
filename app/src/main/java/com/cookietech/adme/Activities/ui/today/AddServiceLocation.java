package com.cookietech.adme.Activities.ui.today;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.GoogleMapHelper;
import com.cookietech.adme.Helpers.Service;
import com.cookietech.adme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class AddServiceLocation extends Fragment implements AddServicesActivity.SaveFragmentListener, OnMapReadyCallback {

    private static final String TAG = "AddServiceLocation";

    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;
    private CheckBox testCheckbox;
    private TextView addressTextView;
    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private Button btn_fetch_your_location;

    private Service newService;

    private String latitude;
    private String longitude;
    private String display_name;
    private String full_address;

    private boolean isEditing= false;

    public AddServiceLocation(Service newService,boolean isEditing) {
        this.newService = newService;
        this.isEditing = isEditing;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_service_location_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_fetch_your_location = view.findViewById(R.id.btn_fetch_your_location);
        addressTextView = view.findViewById(R.id.add_service_location_address);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_fetch_your_location.setEnabled(false);
        btn_fetch_your_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
                GoogleMapHelper.getCurrentLocationAddress(requireContext(), new GoogleMapHelper.OnLocationAddressCallback() {
                    @Override
                    public void locationAddressFetched(Address address) {
                        isValidationChecked = true;
                        addressTextView.setText(address.getAddressLine(0));
                        latitude = String.valueOf(address.getLatitude());
                        longitude = String.valueOf(address.getLongitude());
                        full_address = address.getAddressLine(0);
                    }
                });
            }
        });

        checkPermission();
    }

    @Override
    public boolean isDataSaved() {
        return isDataSaved;
    }

    @Override
    public void saveData() {
        if(isValidationChecked){
            Map<String,String > location = new HashMap<>();
            location.put(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE,latitude);
            location.put(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE,longitude);
            location.put(FirebaseUtilClass.ENTRY_LOCATION_DISPLAY_NAME,display_name);
            location.put(FirebaseUtilClass.ENTRY_LOCATION_ADDRESS,full_address);
            newService.setLocation(location);
            isDataSaved = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        btn_fetch_your_location.setEnabled(true);
        if(isEditing){
            latitude = newService.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LATITUDE);
            longitude = newService.getLocation().get(FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE);
            if(!latitude.isEmpty() && !longitude.isEmpty()){
                isValidationChecked = true;
                double lat = Double.parseDouble(latitude);
                double lng = Double.parseDouble(longitude);
                LatLng latLng = new LatLng(lat,lng);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(requireContext().getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                return;
            }
        }
        GoogleMapHelper.markCurrentLocation(requireContext(),mMap);
        GoogleMapHelper.getCurrentLocationAddress(requireContext(), new GoogleMapHelper.OnLocationAddressCallback() {
            @Override
            public void locationAddressFetched(Address address) {
                isValidationChecked = true;
                addressTextView.setText(address.getAddressLine(0));
                latitude = String.valueOf(address.getLatitude());
                longitude = String.valueOf(address.getLongitude());
                display_name = address.getFeatureName();
                full_address = address.getAddressLine(0);
            }
        });
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
                        .add(R.id.add_service_location_map, mf)
                        .commit();
                requireActivity().runOnUiThread(() -> mf.getMapAsync(AddServiceLocation.this));
            }catch (Exception ignored){

            }
        }).start();

    }
}

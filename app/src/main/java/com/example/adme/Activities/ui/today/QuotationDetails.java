package com.example.adme.Activities.ui.today;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;

public class QuotationDetails extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "QuotationDetails";

    private QuotationDetailsViewModel mViewModel;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private GoogleMap mMap;

    public static QuotationDetails newInstance() {
        return new QuotationDetails();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quotation_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(QuotationDetailsViewModel.class);
        // TODO: Use the ViewModel

        checkPermission();
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
                        .add(R.id.appointment_map, mf)
                        .commit();
                requireActivity().runOnUiThread(() -> mf.getMapAsync(QuotationDetails.this));
            }catch (Exception ignored){

            }
        }).start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new Thread(new Runnable() {
            @Override
            public void run() {
                FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
                locationProviderClient.getLastLocation().addOnSuccessListener( requireActivity(), location -> {
                    if(location != null){
                        LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true).title(getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,DEFAULT_ZOOM));
                                //24.819978, 88.323079
                                //24.820738, 88.319482
                                LatLng origin = new LatLng(24.819978,88.323079);
                                LatLng dest = new LatLng(24.820738,88.319482);
                                mMap.addMarker(new MarkerOptions().position(origin).title(getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.service_provider)));
                                mMap.addMarker(new MarkerOptions().position(dest).title(getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.client)));
                                LatLngBounds zoomBound = LatLngBounds.builder().include(origin).include(dest).build();

                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin,17));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 100));
                                GoogleMapHelper helper = new GoogleMapHelper(mMap);

                                // Getting URL to the Google Directions API
                                String url = helper.getDirectionsUrl(origin, dest);

                                Log.i(TAG, "run: " + url);

                                helper.downloadJson(url);

                            }
                        });

                    }

                }).addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).start();




    }

}

package com.example.adme.Activities.ui.today;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.adme.Helpers.AdServiceDialog;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class ViewServiceDetails extends AppCompatActivity implements OnMapReadyCallback,AdServiceDialog.AdServiceDialogListener {
    private static final String TAG = "ViewServiceDetails";
    private TextView ad_service_btn;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private final String calledFrom = "activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service_details);

        initializeFields();
    }

    private void initializeFields() {
        SliderView sliderView = findViewById(R.id.imageSlider);

        ViewServiceImageSliderAdapter adapter = new ViewServiceImageSliderAdapter(this);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);

        //Setup Map
        checkPermission();

        //Set Ad service recyclerView
        RecyclerView ad_service_recyclerView = findViewById(R.id.ad_service_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewServiceDetails.this);
        ad_service_recyclerView.setLayoutManager(layoutManager);
        ad_service_recyclerView.setHasFixedSize(true);
        AdServiceAdapter service_adapter = new AdServiceAdapter();
        ad_service_recyclerView.setAdapter(service_adapter);

        //open Ad service Dialog
        ad_service_btn = findViewById(R.id.ad_service_btn);
        ad_service_btn.setOnClickListener(v -> {

            openDialogFromActivity();

        });
    }


    private void openDialogFromActivity() {

        AdServiceDialog dialog = new AdServiceDialog(calledFrom);
        dialog.show(getSupportFragmentManager(),"Ad Service Dialog");
    }


    private void checkPermission() {
        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(ViewServiceDetails.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ViewServiceDetails.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewServiceDetails.this);

                builder.setTitle("Location Permission").setMessage("Location permission is must for the map features")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(ViewServiceDetails.this,
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
                ActivityCompat.requestPermissions(ViewServiceDetails.this,
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
                GoogleMapHelper.markCurrentLocation(ViewServiceDetails.this,mMap);
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapHelper.markCurrentLocation(ViewServiceDetails.this,mMap);
    }

    private void setUpMap() {
        new Thread(() -> {
            try {
                SupportMapFragment mf = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.map, mf)
                        .commit();
                runOnUiThread(() -> mf.getMapAsync(ViewServiceDetails.this));
            }catch (Exception ignored){

            }
        }).start();

    }


    @Override
    public void dialogText(String service_name, String service_description, String service_charge) {
        Log.i("service_name",service_name);
        Log.i("service_description",service_description);
        Log.i("service_charge",service_charge);
    }
}

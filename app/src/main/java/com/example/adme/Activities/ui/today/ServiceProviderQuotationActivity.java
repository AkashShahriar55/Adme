package com.example.adme.Activities.ui.today;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.adme.Activities.ui.home.ServiceProviderDetailsActivity;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.Notification;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServiceProviderQuotationActivity  extends AppCompatActivity  implements OnMapReadyCallback {
    private static final String TAG = "QuotationDetails";

    private QuotationDetailsViewModel mViewModel;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15;
    private GoogleMap mMap;
    private TextView tv_distance,tv_clint_time,tv_clint_money,tv_clint_name,tv_clint_address,tv_clint_text,tv_service_title,tv_service_list,tv_money,tv_state;
    private EditText tv_service_time, tv_service_date, tv_service_money,tv_service_quotation;
    private Button send_button,bt_approve,bt_decline;
    private ImageView im_state;
    private Calendar myCalendar;
    private TextInputLayout til_service_time,til_service_date;
    ConstraintLayout inputField;
    FirebaseFirestore db;
    Appointment appointment;
    LatLng currentLocation;
    private View bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    String appointmentID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quotation_details_fragment);
//        checkPermission();
        initializeFields();

        appointmentID = getIntent().getStringExtra("reference");
        getFirebaseData();
    }

    private void initializeFields() {
        bottomSheet = findViewById(R.id.appointment_bottom_details);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        tv_distance = findViewById(R.id.tv_distance);
        tv_clint_time = findViewById(R.id.tv_clint_time);
        tv_clint_money = findViewById(R.id.tv_clint_money);
        tv_clint_name = findViewById(R.id.tv_clint_name);
        tv_clint_address = findViewById(R.id.tv_clint_address);
        tv_clint_text = findViewById(R.id.tv_clint_text);
        tv_service_title = findViewById(R.id.tv_service_title);
        tv_service_list = findViewById(R.id.tv_service_list);
        tv_money = findViewById(R.id.tv_money);
        tv_state = findViewById(R.id.tv_state);
        im_state = findViewById(R.id.im_state);
        inputField = findViewById(R.id.constraintLayout6);

        tv_service_time = findViewById(R.id.tv_service_time);
        tv_service_date = findViewById(R.id.tv_service_date);
        tv_service_money = findViewById(R.id.tv_service_money);
        tv_service_quotation = findViewById(R.id.tv_service_quotation);
        til_service_time = (TextInputLayout) findViewById(R.id.textInputLayout2);
        til_service_date = (TextInputLayout) findViewById(R.id.textInputLayout5);

        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tv_service_date.setText(sdf.format(myCalendar.getTime()));
                til_service_date.setError(null);
            }
        };

        tv_service_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(ServiceProviderQuotationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);
                        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        tv_service_time.setText(sdfs.format(myCalendar.getTime()));
                        til_service_time.setError(null);
                        Log.d(TAG, myCalendar.getTimeInMillis() + " getTimeInMillis");
                    }
                }, hour, minute, false);
                mTimePicker.show();
            }
        });

        tv_service_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog1 = new DatePickerDialog(ServiceProviderQuotationActivity.this, date1,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialog1.show();
            }
        });

        //send button setting
        send_button = findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInputError()){
                    setFirebaseData();
                }
            }
        });

        //approve button setting
        bt_approve = findViewById(R.id.bt_approve);
        bt_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //decline button setting
        bt_decline = findViewById(R.id.bt_decline);
        bt_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if(mMap == null){
            setUpMap();
        }
    }

    public void updateView() {
//        Intent intent = getIntent();
//        appointment = intent.getParcelableExtra("appointment");
//        assert appointment != null;
//        tv_distance,tv_clint_time,tv_clint_money,tv_clint_name,tv_clint_address,tv_clint_text,tv_service_title,tv_service_list,

        runOnUiThread(new Runnable(){
            public void run() {
                if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_SEND) && !isClientMode()) {
                    String servicetext = appointment.getServices();
                    if (servicetext.contains(",,,")) {
                        servicetext = servicetext.replace(",,,", "\n");
                    }
                    tv_distance.setText(appointment.getDistance() + " Miles");
                    tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa, dd MMM yyyy"));
                    tv_clint_money.setText("$ " + appointment.getPrice_requested());
                    tv_clint_name.setText(appointment.getClint_name());
                    String loc = appointment.getClint_location().getName().substring(0, appointment.getClint_location().getName().lastIndexOf(","));
                    tv_clint_address.setText(loc);
                    tv_clint_text.setText(appointment.getClint_text());
                    tv_service_list.setText(servicetext);
                    tv_money.setText("Requested Money : $" + appointment.getPrice_requested());
                    tv_service_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa"));
                    tv_service_date.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "dd MMM yyyy"));
                } else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_SEND) && isClientMode()) {
                    bt_approve.setVisibility(View.VISIBLE);
                    bt_decline.setVisibility(View.VISIBLE);
                    inputField.setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    String servicetext = appointment.getServices();
                    if (servicetext.contains(",,,")) {
                        servicetext = servicetext.replace(",,,", "\n");
                    }
                    tv_distance.setText(appointment.getDistance() + " Miles");
                    tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getService_provider_time(), "hh:mm aa, dd MMM yyyy"));
                    tv_clint_money.setText("$ " + appointment.getPrice_needed());
                    tv_clint_name.setText(appointment.getService_provider_name());
                    String loc = appointment.getService_provider_location().getName().substring(0, appointment.getService_provider_location().getName().lastIndexOf(","));
                    tv_clint_address.setText(loc);
                    tv_clint_text.setText(appointment.getService_provider_text());
                    tv_service_list.setText(servicetext);
                    tv_money.setText("Needed Money : $" + appointment.getPrice_needed());
                } else if(!isClientMode()) {
                    inputField.setVisibility(View.GONE);
                    tv_state.setVisibility(View.VISIBLE);
                    im_state.setVisibility(View.VISIBLE);
                    String servicetext = appointment.getServices();
                    if (servicetext.contains(",,,")) {
                        servicetext = servicetext.replace(",,,", "\n");
                    }
                    tv_service_list.setText(servicetext);
                    tv_distance.setText(appointment.getDistance() + " Miles");
                    tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa, dd MMM yyyy"));
                    tv_clint_money.setText("$ " + appointment.getPrice_requested());
                    tv_clint_name.setText(appointment.getClint_name());
                    String loc = appointment.getClint_location().getName().substring(0, appointment.getClint_location().getName().lastIndexOf(","));
                    tv_clint_address.setText(loc);
                    tv_clint_text.setText(appointment.getClint_text());
                    tv_money.setText("Requested Money : $" + appointment.getPrice_requested());
                    if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_FINISHED)){
                        tv_state.setText("State : Finished");
                    }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_CANCELED)){
                        tv_state.setText("State : Canceled by client");
                    }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_CANCELED)){
                        tv_state.setText("State : Canceled by service provider");
                    }else{
                        tv_state.setText("State : Pending");
                    }
                } else {
                    inputField.setVisibility(View.GONE);
                    tv_state.setVisibility(View.VISIBLE);
                    im_state.setVisibility(View.VISIBLE);
                    String servicetext = appointment.getServices();
                    if (servicetext.contains(",,,")) {
                        servicetext = servicetext.replace(",,,", "\n");
                    }
                    tv_service_list.setText(servicetext);
                    tv_distance.setText(appointment.getDistance() + " Miles");
                    tv_clint_time.setText(CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa, dd MMM yyyy"));
                    tv_clint_money.setText("$ " + appointment.getPrice_requested());
                    tv_clint_name.setText(appointment.getService_provider_name());
                    String loc = appointment.getService_provider_location().getName().substring(0, appointment.getService_provider_location().getName().lastIndexOf(","));
                    tv_clint_address.setText(loc);
                    tv_clint_text.setText(appointment.getClint_text());
                    tv_money.setText("Requested Money : $" + appointment.getPrice_requested());
                    if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_FINISHED)){
                        tv_state.setText("State : Finished");
                    }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_CANCELED)){
                        tv_state.setText("State : Canceled by client");
                    }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_CANCELED)){
                        tv_state.setText("State : Canceled by service provider");
                    }else{
                        tv_state.setText("State : Pending");
                    }
                }
            }
        });
    }

    private void getFirebaseData() {
        db = FirebaseFirestore.getInstance();
        db.collection("Adme_Appointment_list").document(appointmentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        appointment = documentSnapshot.toObject(Appointment.class);
                        MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
                            @Override
                            public boolean queueIdle() {
                                updateView();
                                updateMap();
                                return false;
                            }
                        };
                        Looper.myQueue().addIdleHandler(handler);
                    }
                });
    }

    private void setFirebaseData(){
        db = FirebaseFirestore.getInstance();
        appointment.setPrice_needed(tv_service_money.getText().toString());
        appointment.setService_provider_text(tv_service_quotation.getText().toString());
        appointment.setState(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_SEND);

        String givenDateString = tv_service_time.getText().toString()+" "+tv_service_date.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa dd MMM yyyy");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();
            appointment.setService_provider_time(String.valueOf(timeInMilliseconds));
//            Log.d(TAG, CookieTechUtilityClass.getTimeDate(String.valueOf(timeInMilliseconds),"hh:mm aa, dd MMM yyyy")+" in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        db.collection("Adme_Appointment_list").document(appointmentID)
                .set(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        String newDocumentID = String.valueOf(Calendar.getInstance().getTimeInMillis());
                        Notification notification = new Notification();
                        notification.setSeen(false);
                        notification.setTime(newDocumentID);
                        notification.setText(appointment.getClint_name()+" response to your request");
                        notification.setMode(FirebaseUtilClass.MODE_CLIENT);
                        notification.setType(FirebaseUtilClass.NOTIFICATION_APPOINTMENT_TYPE);
                        notification.setReference(appointmentID);

                        db.collection("Adme_User/"+appointment.getClint_ref()+"/notification_list")
                                .document(newDocumentID)
                                .set(notification)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Notification successfully written!");
                                        Toast.makeText(getApplicationContext(), "Successfully send quotation to client.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private boolean isInputError(){
        if(tv_service_time.getText().toString().trim().equals("")){
            til_service_time.setError("Select a time");
            return true;
        } else if(tv_service_date.getText().toString().trim().equals("")){
            til_service_date.setError("Select a date");
            return true;
        } else {
            return false;
        }
    }

    private void checkPermission() {
        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(ServiceProviderQuotationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ServiceProviderQuotationActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProviderQuotationActivity.this);

                builder.setTitle("Location Permission").setMessage("Location permission is must for the map features")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(ServiceProviderQuotationActivity.this,
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
                ActivityCompat.requestPermissions(ServiceProviderQuotationActivity.this,
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
//                updateMap();
            }
        }
    }

    private void setUpMap() {
        new Thread(() -> {
            try {
                SupportMapFragment mf = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.map, mf)
                        .commit();
                runOnUiThread(() -> mf.getMapAsync(ServiceProviderQuotationActivity.this));
            }catch (Exception ignored){

            }
        }).start();
    }

    public void updateMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (appointment.getState().equals("clientSent")) {
                    FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(ServiceProviderQuotationActivity.this);
                    locationProviderClient.getLastLocation().addOnSuccessListener(ServiceProviderQuotationActivity.this, location -> {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.addMarker(new MarkerOptions().position(currentLocation).title(getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.service_provider, 90, 78))));
                                    mMap.addMarker(new MarkerOptions().position(appointment.getClintLatLng()).title(getString(R.string.client_current_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.client, 90, 78))));

                                    LatLngBounds zoomBound = LatLngBounds.builder().include(currentLocation).include(appointment.getClintLatLng()).build();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 300));

                                    // Getting URL to the Google Directions API
                                    GoogleMapHelper helper = new GoogleMapHelper(mMap);
                                    String url = helper.getDirectionsUrl(currentLocation, appointment.getClintLatLng());
                                    Log.i(TAG, "run: " + url);
                                    helper.downloadJson(url);
                                }
                            });
                        }
                    }).addOnFailureListener(ServiceProviderQuotationActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_SEND)) {
                    if (appointment.getClintLatLng() != null && appointment.getServiceProviderLatLng() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions().position(appointment.getClintLatLng()).title(getString(R.string.your_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.client, 90, 78))));
                                mMap.addMarker(new MarkerOptions().position(appointment.getServiceProviderLatLng()).title(getString(R.string.sv_current_location)).icon(BitmapDescriptorFactory.fromBitmap(getIcon(R.drawable.service_provider, 90, 78))));

                                LatLngBounds zoomBound = LatLngBounds.builder().include(appointment.getServiceProviderLatLng()).include(appointment.getClintLatLng()).build();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(appointment.getClintLatLng(), 10));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBound, 300));

                                // Getting URL to the Google Directions API
                                GoogleMapHelper helper = new GoogleMapHelper(mMap);
                                String url = helper.getDirectionsUrl(appointment.getServiceProviderLatLng(), appointment.getClintLatLng());
                                Log.i(TAG, "run: " + url);
                                helper.downloadJson(url);
                            }
                        });
                    } else {
                        Log.w(TAG, "Some ones loaction null");
                        Toast.makeText(getApplicationContext(), "Failed to get location info. Please, try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    public Bitmap getIcon(int name,int height, int width){
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(name);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return marker;
    }

    public boolean isClientMode(){
        SharedPreferences preferences=getSharedPreferences("Settings", MODE_PRIVATE);
        if(preferences.getBoolean("isClient",true)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}

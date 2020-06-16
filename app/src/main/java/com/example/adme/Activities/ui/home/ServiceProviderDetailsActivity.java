package com.example.adme.Activities.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.adme.Activities.ui.income.RatingHistoryAdapter;
import com.example.adme.Activities.ui.today.ViewServiceImageSliderAdapter;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.GoogleMapHelper;
import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.SelectServiceItem;
import com.example.adme.Helpers.Service;
import com.example.adme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_ADDRESS;
import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_DISPLAY_NAME;
import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_LATITUDE;
import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE;
import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_SERVICE_DESCRIPTION;
import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_SERVICE_PRICE;
import static com.example.adme.Architecture.FirebaseUtilClass.ENTRY_SERVICE_TITLE;

public class ServiceProviderDetailsActivity  extends AppCompatActivity implements OnMapReadyCallback, SelectServiceAdapter.SelectServiceAdapterListener {
    private static final String TAG = "ServiceProviderDetails";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private EditText tv_service_added, tv_service_time, tv_service_date, tv_service_money,tv_service_quotation;
    TextView tv_item_count,tv_total_price,tv_username,tv_work_done,tv_catagory,tv_short_discription,tv_working_hour,tv_location, tv_location_short,tv_location_details,tv_distance,tv_services,tv_reviews;
    private Button send_button;
    private Calendar myCalendar;
    private TextInputLayout til_service_added,til_service_time,til_service_date;
    SelectServiceAdapter service_adapter;
    RatingHistoryAdapter reviewAdapter;
    private List<SelectServiceItem> selectServiceList = new ArrayList<>();
    RecyclerView select_service_recyclerView,review_recyclerView;
    CircleImageView circleImageView;
    RatingBar ratingBar;
    List<String> selectServices = new ArrayList<>();
    List<String> selectServicesPrice = new ArrayList<>();
    SliderView sliderView;
    ViewServiceImageSliderAdapter feature_image_adapter;
    LatLng serviceProviderLocation;
    Location clentLocation;
    FirebaseFirestore db;
    double distance=0;
    Service service;
    String selected_service_text;
    String servicetext="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_view_service_provider);
        initializeFields();
    }

    private void initializeFields() {
        circleImageView = findViewById(R.id.circleImageView);
        ratingBar = findViewById(R.id.ratingBar);
        tv_username = findViewById(R.id.tv_username);
        tv_work_done = findViewById(R.id.tv_work_done);
        tv_catagory = findViewById(R.id.tv_catagory);
        tv_short_discription = findViewById(R.id.tv_short_discription);
        tv_working_hour = findViewById(R.id.tv_working_hour);
        tv_location = findViewById(R.id.tv_location);
        tv_location_short = findViewById(R.id.tv_location_short);
        tv_location_details = findViewById(R.id.tv_location_details);
        tv_distance = findViewById(R.id.tv_distance);
        tv_services = findViewById(R.id.tv_services);
        tv_reviews = findViewById(R.id.tv_reviews);

        tv_item_count = findViewById(R.id.tv_item_count);
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_service_added = findViewById(R.id.tv_service_added);
        tv_service_time = findViewById(R.id.tv_service_time);
        tv_service_date = findViewById(R.id.tv_service_date);
        tv_service_money = findViewById(R.id.tv_service_money);
        tv_service_quotation = findViewById(R.id.tv_service_quotation);
        til_service_added= (TextInputLayout) findViewById(R.id.textInputLayout51);
        til_service_time= (TextInputLayout) findViewById(R.id.textInputLayout2);
        til_service_date= (TextInputLayout) findViewById(R.id.textInputLayout5);

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
                TimePickerDialog mTimePicker = new TimePickerDialog(ServiceProviderDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);
                        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                        tv_service_time.setText(sdfs.format(myCalendar.getTime()));
                        til_service_time.setError(null);
//                        SimpleDateFormat sd = new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa", Locale.getDefault());
//                        Log.d(TAG, myCalendar.getTimeInMillis()+" initializeFields: "+sd.format(myCalendar.getTime()));
                        Log.d(TAG, myCalendar.getTimeInMillis()+" getTimeInMillis");
                    }
                }, hour, minute, false);
                mTimePicker.show();
            }
        });

//        tv_service_date.setClickable(true);
//        tv_service_date.setFocusable(false);
//        tv_service_date.setInputType(InputType.TYPE_NULL);
        tv_service_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog1 =new DatePickerDialog(ServiceProviderDetailsActivity.this, date1,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialog1.show();
            }
        });

        //feature image adapter setting
        sliderView = findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
//        feature_image_adapter = new ViewServiceImageSliderAdapter(this);
//        sliderView.setSliderAdapter(feature_image_adapter);

        //select service adapter setting
        select_service_recyclerView = findViewById(R.id.ad_service_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ServiceProviderDetailsActivity.this);
        select_service_recyclerView.setLayoutManager(layoutManager);
        select_service_recyclerView.setHasFixedSize(true);
        service_adapter = new SelectServiceAdapter(ServiceProviderDetailsActivity.this,selectServiceList,this);
//        select_service_recyclerView.setAdapter(service_adapter);

        //review adapter setting
        review_recyclerView = findViewById(R.id.review_recyclerView);
        RecyclerView.LayoutManager review_layoutManager =  new LinearLayoutManager(ServiceProviderDetailsActivity.this);
        review_recyclerView.setLayoutManager(review_layoutManager);
        review_recyclerView.setHasFixedSize(true);
        reviewAdapter = new RatingHistoryAdapter();
//        review_recyclerView.setAdapter(reviewAdapter);

        //send button setting
        send_button = findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInputError()){
                    setFirebaseData("appointment10");
                }
            }
        });


        updateView();
    }

    public void updateView() {
        Intent intent = getIntent();
        service = intent.getParcelableExtra("serviceProviderObject");
        assert service != null;

        tv_username.setText(service.getUser_name());
        tv_work_done.setText(service.getReviews());
        tv_catagory.setText(service.getCategory());
        tv_short_discription.setText(service.getDescription());
        tv_working_hour.setText(service.getWorking_hour());
        tv_location_short.setText(service.getLocation().get(ENTRY_LOCATION_DISPLAY_NAME));
        tv_location_details.setText(service.getLocation().get(ENTRY_LOCATION_ADDRESS));
        ratingBar.setRating(Float.parseFloat(service.getRating()));

        serviceProviderLocation = new LatLng(Double.parseDouble(service.getLocation().get(ENTRY_LOCATION_LATITUDE)),Double.parseDouble(service.getLocation().get(ENTRY_LOCATION_LONGITUDE)));

        Glide.with(ServiceProviderDetailsActivity.this)
                .load(service.getPic_url())
                .apply(RequestOptions.circleCropTransform())
                .into(circleImageView);

        feature_image_adapter = new ViewServiceImageSliderAdapter(this, service.getFeature_images());

        for (int i = 0; i < service.getServices().size(); i++) {
            SelectServiceItem selectServiceItem= new SelectServiceItem();
            selectServiceItem.setService_title(service.getServices().get(i).get(ENTRY_SERVICE_TITLE));
            selectServiceItem.setService_details(service.getServices().get(i).get(ENTRY_SERVICE_DESCRIPTION));
            selectServiceItem.setService_price(service.getServices().get(i).get(ENTRY_SERVICE_PRICE));
            selectServiceList.add(selectServiceItem);
        }

        MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                sliderView.setSliderAdapter(feature_image_adapter);
                select_service_recyclerView.setAdapter(service_adapter);
                review_recyclerView.setAdapter(reviewAdapter);
                return false;
            }
        };
        Looper.myQueue().addIdleHandler(handler);

        runOnUiThread(new Runnable(){
            public void run() {
                service_adapter.notifyDataSetChanged();
            }
        });

        //Setup Map
        checkPermission();
    }

    private boolean isInputError(){
        if(selectServices.size()==0){
            til_service_added.setError("Select a service");
            return true;
        } else if(tv_service_time.getText().toString().trim().equals("")){
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

        if (ContextCompat.checkSelfPermission(ServiceProviderDetailsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ServiceProviderDetailsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProviderDetailsActivity.this);

                builder.setTitle("Location Permission").setMessage("Location permission is must for the map features")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(ServiceProviderDetailsActivity.this,
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
                ActivityCompat.requestPermissions(ServiceProviderDetailsActivity.this,
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
//                GoogleMapHelper.markCurrentLocation(ServiceProviderDetailsActivity.this,mMap);
                GoogleMapHelper.markLocation(ServiceProviderDetailsActivity.this, mMap, serviceProviderLocation);
//                tv_distance.setText(GoogleMapHelper.getCurrentLocationDistance(ServiceProviderDetailsActivity.this, mMap, serviceProviderLocation)+"");
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
                runOnUiThread(() -> mf.getMapAsync(ServiceProviderDetailsActivity.this));
            }catch (Exception ignored){

            }
        }).start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        GoogleMapHelper.markCurrentLocation(ServiceProviderDetailsActivity.this,mMap);
        GoogleMapHelper.markLocation(ServiceProviderDetailsActivity.this, mMap, serviceProviderLocation);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(ServiceProviderDetailsActivity.this);
                locationProviderClient.getLastLocation().addOnSuccessListener((Activity) ServiceProviderDetailsActivity.this, location -> {
                    if(location != null){
                        clentLocation=location;
                        distance = GoogleMapHelper.getDistanceInMiles(location.getLatitude(), location.getLongitude(), serviceProviderLocation.latitude, serviceProviderLocation.longitude);
                        tv_distance.setText(String.format("%.1f", distance));
                        Log.i(TAG, "distance: "+distance);
                    }
                }).addOnFailureListener((Activity) ServiceProviderDetailsActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //25.763402, 88.908705
                    }
                });

            }
        }).start();
    }

    @Override
    public void onSelectServiceSelected(SelectServiceItem selectServiceItem) {
        if (!selectServices.contains(selectServiceItem.getService_title())) {
            selectServices.add(selectServiceItem.getService_title());
            selectServicesPrice.add(selectServiceItem.getService_price());
            Toast.makeText(getApplicationContext(), selectServiceItem.getService_title()+ " Added", Toast.LENGTH_SHORT).show();
        } else {
            selectServices.remove(selectServiceItem.getService_title());
            selectServicesPrice.remove(selectServiceItem.getService_price());
            Toast.makeText(getApplicationContext(), selectServiceItem.getService_title()+ " Removed", Toast.LENGTH_SHORT).show();
        }
        setServiceOnBottomSheet();
    }

    private void initia() {
        SelectServiceItem selectServiceItem1=new SelectServiceItem("Pattern Paint","It is call pattern paint.","90");
        SelectServiceItem selectServiceItem2=new SelectServiceItem("Rubber Paint","It is call Rubber paint.","70");
        SelectServiceItem selectServiceItem3=new SelectServiceItem("Artist Paint","It is call Artist paint.","110");
        selectServiceList.add(selectServiceItem1);
        selectServiceList.add(selectServiceItem2);
        selectServiceList.add(selectServiceItem3);
        runOnUiThread(new Runnable(){
            public void run() {
                service_adapter.notifyDataSetChanged();
            }
        });
    }

    public void setServiceOnBottomSheet(){
        selected_service_text = "No Service Added";
        String selected_service_count = "0 Service";
        for (int i=0; i<selectServices.size(); i++) {
            selected_service_count = (i+1) + " Services";
            if(i==0) {
                selected_service_text = selectServices.get(i)+ " ($" + selectServicesPrice.get(i) + ")";
            }else if(i==1) {
                selected_service_text = i + ". " + selected_service_text + "\n" + (i+1) + ". " + selectServices.get(i)+ " ($" + selectServicesPrice.get(i) + ")";
            } else {
                selected_service_text += "\n"+ (i+1) + ". " + selectServices.get(i)+ " ($" + selectServicesPrice.get(i) + ")";
            }
        }
        tv_service_added.setText(selected_service_text);
        tv_item_count.setText(selected_service_count);
        float sum = 0;
        for(String f : selectServicesPrice)
            sum += Float.parseFloat(f);
        tv_total_price.setText("$ "+sum);
        til_service_added.setError(null);

        if(selected_service_text.contains("\n")){
            servicetext = selected_service_text.replace("\n",",,,");
        } else {
            servicetext = selected_service_text;
        }
//        String[] lines = servicetext.split(",,,");
//        Log.d(TAG, servicetext+" setServiceOnBottomSheet: "+lines[0]);
    }

    private void setFirebaseData(String doc){
        Appointment appointment= new Appointment();

        appointment.setClint_name("Amanullah Asraf");
        appointment.setClint_phone("01532146589");
        appointment.setClint_ref("Adme/user2");
        MyPlaces clintPlace = new MyPlaces("Bashbari","Bashbari, Saidpur", String.valueOf(clentLocation.getLatitude()), String.valueOf(clentLocation.getLongitude()));
        appointment.setClint_location(clintPlace);

        appointment.setServices(servicetext);
        appointment.setClint_time(String.valueOf(myCalendar.getTimeInMillis()));
        appointment.setPrice_requested(tv_service_money.getText().toString());
        appointment.setClint_text(tv_service_quotation.getText().toString());
        appointment.setDistance(String.format("%.1f", distance));
        appointment.setService_provider_name(service.getUser_name());
        appointment.setService_provider_ref(service.getUser_ref());
        appointment.setState("clientSent");

        db = FirebaseFirestore.getInstance();
        db.collection("Adme_Appointment_list").document(doc)
                .set(appointment)
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

}

package com.cookietech.adme.activities.ui.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cookietech.adme.activities.ui.income.RatingHistoryAdapter;
import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.AdServiceDialog;
import com.cookietech.adme.Helpers.SelectServiceItem;
import com.cookietech.adme.Helpers.Service;
import com.cookietech.adme.Helpers.User;
import com.cookietech.adme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cookietech.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_ADDRESS;
import static com.cookietech.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_DISPLAY_NAME;
import static com.cookietech.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_LATITUDE;
import static com.cookietech.adme.Architecture.FirebaseUtilClass.ENTRY_LOCATION_LONGITUDE;

public class ViewServiceDetails extends AppCompatActivity implements OnMapReadyCallback,AdServiceDialog.AdServiceDialogListener {
    private static final String TAG = "ViewServiceDetails";
    private TextView ad_service_btn;
    private final String calledFrom = "activity";

    private List<Map<String,String>> services = new ArrayList<>();

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private EditText tv_service_added, tv_service_time, tv_service_date, tv_service_money,tv_service_quotation;
    TextView tv_edit_service,tv_item_count,tv_total_price,tv_username,tv_work_done,tv_catagory,tv_short_discription,tv_working_hour,tv_location, tv_location_short,tv_location_details,tv_distance,tv_services,tv_reviews;
    private Button send_button;
    private Calendar myCalendar;
    private TextInputLayout til_service_added,til_service_time,til_service_date;
    AdServiceAdapter service_adapter;
    RatingHistoryAdapter reviewAdapter;
    private List<SelectServiceItem> selectServiceList;
    RecyclerView ad_service_recyclerView,review_recyclerView;
    CircleImageView circleImageView;
    RatingBar ratingBar;
    List<String> selectServices = new ArrayList<>();
    List<String> selectServicesPrice = new ArrayList<>();
    SliderView sliderView;
    ViewServiceImageSliderAdapter feature_image_adapter;
    LatLng serviceProviderLocation;

    ViewServiceDetailsViewModel viewModel;
    Service mCurrentService;

    User mCurrentUser ;

    int serviceIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service_details);
        String service_id = getIntent().getStringExtra("service_id");
        mCurrentUser = getIntent().getParcelableExtra(FirebaseUtilClass.CURRENT_USER_ID);
        serviceIndex = getIntent().getIntExtra("service_index",-1);
        Log.d("akash_debug", "checking user data: "+mCurrentUser.getService_reference().size());
        viewModel = new ViewModelProvider(this).get(ViewServiceDetailsViewModel.class);
        final Observer<Service> serviceDataObserver = new Observer<Service>() {
            @Override
            public void onChanged(Service service) {
                mCurrentService = service;
                Log.d("view-model", "onChanged:  bottom details" + service.getStatus());
                updateUi();
            }
        };
        viewModel.getServiceData(service_id).observe(this,serviceDataObserver);
        initializeFields();
    }

    private void updateUi() {
        tv_username.setText(mCurrentService.getUser_name());
        tv_work_done.setText(mCurrentService.getReviews());
        tv_catagory.setText(mCurrentService.getCategory());
        tv_short_discription.setText(mCurrentService.getDescription());
        tv_working_hour.setText(mCurrentService.getWorking_hour());
        tv_location_short.setText(mCurrentService.getLocation().get(ENTRY_LOCATION_DISPLAY_NAME));
        tv_location_details.setText(mCurrentService.getLocation().get(ENTRY_LOCATION_ADDRESS));
        ratingBar.setRating(Float.parseFloat(mCurrentService.getRating()));

        serviceProviderLocation = new LatLng(Double.parseDouble(mCurrentService.getLocation().get(ENTRY_LOCATION_LATITUDE)),Double.parseDouble(mCurrentService.getLocation().get(ENTRY_LOCATION_LONGITUDE)));

        if(mCurrentService.getPic_url().equals(FirebaseUtilClass.VALUE_DEFAULT_AVATAR)){
            Glide.with(ViewServiceDetails.this)
                    .load(mCurrentService.getPic_url())
                    .apply(RequestOptions.circleCropTransform())
                    .into(circleImageView);
        }else if(mCurrentService.getPic_url().equals(FirebaseUtilClass.VALUE_USER_PHOTO)){
            FirebaseUser user = FirebaseUtilClass.getInstance().getCurrentUser();
            Glide.with(ViewServiceDetails.this)
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(circleImageView);
        }else{
            int drawableResourceId = this.getResources().getIdentifier("ic_avatar", "drawable", this.getPackageName());
            Glide.with(ViewServiceDetails.this)
                    .load(getDrawable(drawableResourceId))
                    .apply(RequestOptions.circleCropTransform())
                    .into(circleImageView);
        }




        MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {

                ad_service_recyclerView.setAdapter(service_adapter);
                review_recyclerView.setAdapter(reviewAdapter);
                return false;
            }
        };
        Looper.myQueue().addIdleHandler(handler);

        runOnUiThread(new Runnable(){
            public void run() {
                service_adapter.setServiceList(mCurrentService.getServices());
                feature_image_adapter.setFeature_image_url_list(mCurrentService.getFeature_images());
            }
        });

        setUpMap();


    }

    private void initializeFields() {
        selectServiceList = new ArrayList<>();

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

        tv_edit_service = findViewById(R.id.tv_edit_service);


        SliderView sliderView = findViewById(R.id.imageSlider);
        feature_image_adapter = new ViewServiceImageSliderAdapter(this, new ArrayList<>());
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setSliderAdapter(feature_image_adapter);


        //Set Ad service recyclerView
        ad_service_recyclerView = findViewById(R.id.ad_service_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewServiceDetails.this);
        ad_service_recyclerView.setLayoutManager(layoutManager);
        ad_service_recyclerView.setHasFixedSize(true);
        service_adapter = new AdServiceAdapter(this, services, new AdServiceAdapter.AddServiceAdapterListener() {
            @Override
            public void deleteService(int position) {

            }
        });
        ad_service_recyclerView.setAdapter(service_adapter);

        //open Ad service Dialog
        ad_service_btn = findViewById(R.id.ad_service_btn);
        ad_service_btn.setOnClickListener(v -> {

            openDialogFromActivity();

        });


        //review adapter setting

        RecyclerView review_recyclerView = findViewById(R.id.review_recyclerView);
        RecyclerView.LayoutManager review_layoutManager =  new LinearLayoutManager(ViewServiceDetails.this);
        review_recyclerView.setLayoutManager(review_layoutManager);
        review_recyclerView.setHasFixedSize(true);
        ReviewAdapter reviewAdapter = new ReviewAdapter();
        review_recyclerView.setAdapter(reviewAdapter);

        tv_edit_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewServiceDetails.this,AddServicesActivity.class);
                intent.putExtra("is_editing",true);
                intent.putExtra("edit_service",mCurrentService);
                intent.putExtra(FirebaseUtilClass.CURRENT_USER_ID,mCurrentUser);
                intent.putExtra("service_index",serviceIndex);
                startActivity(intent);
            }
        });




    }


    private void openDialogFromActivity() {

        AdServiceDialog dialog = new AdServiceDialog(calledFrom);
        dialog.show(getSupportFragmentManager(),"Ad Service Dialog");
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(serviceProviderLocation).draggable(true).title(this.getString(R.string.your_current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(serviceProviderLocation, 15));
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

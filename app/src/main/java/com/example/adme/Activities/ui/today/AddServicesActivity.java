package com.example.adme.Activities.ui.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adme.R;

public class AddServicesActivity extends AppCompatActivity {

    private static final String OVERVIEW = "overview";
    private static final int STATE_OVERVIEW = 1;
    private static final int STATE_SERVICES = 2;
    private static final int STATE_GALLERY = 3;
    private static final int STATE_LOCATION = 4;
    private static final String LOCATION = "location";
    private static final String GALLERY = "gallery";
    private static final String SERVICES = "services";
    private int state = -1;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageView addServiceOne,addServiceTwo,addServiceThree,addServiceFour;
    TextView addServiceOverview,addServiceServices,addServiceGallery,addServiceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);

        addServiceOne = findViewById(R.id.add_service_1_image);
        addServiceTwo = findViewById(R.id.add_service_2_image);
        addServiceThree = findViewById(R.id.add_service_3_image);
        addServiceFour = findViewById(R.id.add_service_4_image);
        addServiceOverview = findViewById(R.id.add_service_overview_text);
        addServiceServices = findViewById(R.id.add_service_services_text);
        addServiceGallery = findViewById(R.id.add_service_gallery_text);
        addServiceLocation = findViewById(R.id.add_service_location_text);

        fragmentManager = getSupportFragmentManager();

        showFragment(new AddServiceOverview(),OVERVIEW,STATE_OVERVIEW);

    }


    public void saveButtonClicked(View view){
        switch (state){
            case STATE_OVERVIEW:
                setActive(STATE_SERVICES);
                setCompleted(STATE_OVERVIEW);
                showFragment(new AddServiceServices(),SERVICES,STATE_SERVICES);
                break;
            case STATE_SERVICES:
                setActive(STATE_GALLERY);
                setCompleted(STATE_SERVICES);
                showFragment(new AddServiceGallery(),GALLERY,STATE_GALLERY);
                break;
            case STATE_GALLERY:
                setActive(STATE_LOCATION);
                setCompleted(STATE_GALLERY);
                showFragment(new AddServiceLocation(),LOCATION,STATE_LOCATION);
                break;
            case STATE_LOCATION:
                saveDataAndFinish();
                break;
        }
    }

    private void setCompleted(int state) {
        switch (state){
            case STATE_OVERVIEW:
                addServiceOne.setImageResource(R.drawable.ic_1_green);
                addServiceOverview.setTextColor(ContextCompat.getColor(this, R.color.color_active));
                break;
            case STATE_SERVICES:
                addServiceTwo.setImageResource(R.drawable.ic_2_green);
                addServiceServices.setTextColor(ContextCompat.getColor(this, R.color.color_active));
                break;
            case STATE_GALLERY:
                addServiceThree.setImageResource(R.drawable.ic_3_green);
                addServiceGallery.setTextColor(ContextCompat.getColor(this, R.color.color_active));
                break;
        }

    }

    private void setActive(int state) {
        switch (state){
            case STATE_OVERVIEW:
                addServiceOne.setImageResource(R.drawable.ic_1_blue);
                addServiceOverview.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                break;
            case STATE_SERVICES:
                addServiceTwo.setImageResource(R.drawable.ic_2_blue);
                addServiceServices.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                break;
            case STATE_GALLERY:
                addServiceThree.setImageResource(R.drawable.ic_3_blue);
                addServiceGallery.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                break;
            case STATE_LOCATION:
                addServiceFour.setImageResource(R.drawable.ic_4_blue);
                addServiceLocation.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                break;
        }
    }

    private void showFragment(Fragment fragment, String tag, int state) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.add_service_fragment_host,fragment,"overview");
        if(state != STATE_OVERVIEW){
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
        this.state = state;
    }

    private void saveDataAndFinish() {

    }


}

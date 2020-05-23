package com.example.adme.Activities.ui.today;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adme.R;

public class AddServicesActivity extends AppCompatActivity {

    private static final String TAG = "AddServicesActivity";

    private static final int STATE_OVERVIEW = 1;
    private static final int STATE_SERVICES = 2;
    private static final int STATE_GALLERY = 3;
    private static final int STATE_LOCATION = 4;
    private static final String OVERVIEW = "overview";
    private static final String LOCATION = "location";
    private static final String GALLERY = "gallery";
    private static final String SERVICES = "services";
    private int state = -1;
    private boolean overviewCompleted = false;
    private boolean servicesCompleted = false;
    private boolean galleryCompleted = false;
    private boolean locationCompleted = false;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Button addServiceSaveButton;
    ImageView addServiceOne,addServiceTwo,addServiceThree,addServiceFour;
    TextView addServiceOverview,addServiceServices,addServiceGallery,addServiceLocation,addServiceCancelButton;
    Fragment fragmentOverview,fragmentServices,fragmentGallery,fragmentLocation;
    SaveFragmentListener saveFragmentListener;


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

        addServiceCancelButton = findViewById(R.id.add_service_cancel_button);
        addServiceSaveButton = findViewById(R.id.add_service_save_button);


        addServiceCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        fragmentOverview = new AddServiceOverview();
        fragmentServices = new AddServiceServices();
        fragmentGallery = new AddServiceGallery();
        fragmentLocation = new AddServiceLocation();
        fragmentManager = getSupportFragmentManager();

        showFragment(fragmentOverview,OVERVIEW,STATE_OVERVIEW);

    }



    public void stepNavigationButtonClicked(View view){
        int id = view.getId();
        switch (id){
            case R.id.add_service_overview_text:
                popFragment(OVERVIEW,fragmentOverview,STATE_OVERVIEW);
                break;
            case R.id.add_service_services_text:
                popFragment(SERVICES,fragmentServices,STATE_SERVICES);
                break;
            case R.id.add_service_gallery_text:
                popFragment(GALLERY,fragmentGallery,STATE_GALLERY);
                break;
            case R.id.add_service_location_text:
                popFragment(LOCATION,fragmentLocation,STATE_LOCATION);
                break;
        }
    }

    private void popFragment(String name,Fragment fragment,int state) {
        boolean isSaved = saveFragmentListener.isDataSaved();
        Log.i(TAG, "popFragment: "+isSaved);
        switch (this.state){
            case STATE_OVERVIEW:
                overviewCompleted = isSaved;
                break;
            case STATE_SERVICES:
                servicesCompleted = isSaved;
                break;
            case STATE_GALLERY:
                galleryCompleted = isSaved;
                break;
            case STATE_LOCATION:
                locationCompleted = isSaved;
                break;
        }
        showFragment(fragment,name,state);
    }


    public void saveButtonClicked(View view){
        saveFragmentListener.saveData();
        boolean isSaved = saveFragmentListener.isDataSaved();
        if(isSaved)
            switch (state){
                case STATE_OVERVIEW:
                    overviewCompleted = true;
                    showFragment(fragmentServices,SERVICES,STATE_SERVICES);
                    break;
                case STATE_SERVICES:
                    servicesCompleted = true;
                    showFragment(fragmentGallery,GALLERY,STATE_GALLERY);
                    break;
                case STATE_GALLERY:
                    galleryCompleted = true;
                    showFragment(fragmentLocation,LOCATION,STATE_LOCATION);
                    break;
                case STATE_LOCATION:
                    locationCompleted = true;
                    saveDataAndFinish();
                    break;
            }
    }



    private void showFragment(Fragment fragment, String tag, int state) {
        saveFragmentListener = (SaveFragmentListener) fragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.add_service_fragment_host,fragment,tag);
        fragmentTransaction.commit();
        this.state = state;
        updateStepNavigationUi();
        Log.i(TAG, "showFragment: fragment no = "+fragmentManager.getBackStackEntryCount());
    }

    private void saveDataAndFinish() {
        if(!overviewCompleted){
            Toast.makeText(this,"Complete and save Overview",Toast.LENGTH_SHORT).show();
            showFragment(fragmentOverview,OVERVIEW,STATE_OVERVIEW);
        }else if(!servicesCompleted){
            Toast.makeText(this,"Complete and save Services",Toast.LENGTH_SHORT).show();
            showFragment(fragmentServices,SERVICES,STATE_SERVICES);
        }else if(!galleryCompleted){
            Toast.makeText(this,"Complete and save Gallery",Toast.LENGTH_SHORT).show();
            showFragment(fragmentGallery,GALLERY,STATE_GALLERY);
        }else if(!locationCompleted){
            Toast.makeText(this,"Complete and save Location",Toast.LENGTH_SHORT).show();
            showFragment(fragmentLocation,LOCATION,STATE_LOCATION);
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitAlert = new AlertDialog.Builder(this);
        exitAlert.setTitle("Are you sure?")
                .setMessage("Do you want to exit? It will delete all your saved progress")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        exitAlert.show();
    }

    public interface SaveFragmentListener{
        boolean isDataSaved();
        void saveData();
    }

    private void updateStepNavigationUi(){

        if(overviewCompleted){
            addServiceOne.setImageResource(R.drawable.ic_1_green);
            addServiceOverview.setTextColor(ContextCompat.getColor(this, R.color.color_active));
        }else{
            addServiceOne.setImageResource(R.drawable.ic_1_gray);
            addServiceOverview.setTextColor(ContextCompat.getColor(this, R.color.add_service_step_txt_gray));
        }

        if(servicesCompleted){
            addServiceTwo.setImageResource(R.drawable.ic_2_green);
            addServiceServices.setTextColor(ContextCompat.getColor(this, R.color.color_active));
        }else{
            addServiceTwo.setImageResource(R.drawable.ic_2_gray);
            addServiceServices.setTextColor(ContextCompat.getColor(this, R.color.add_service_step_txt_gray));
        }

        if(galleryCompleted){
            addServiceThree.setImageResource(R.drawable.ic_3_green);
            addServiceGallery.setTextColor(ContextCompat.getColor(this, R.color.color_active));
        }else{
            addServiceThree.setImageResource(R.drawable.ic_3_gray);
            addServiceGallery.setTextColor(ContextCompat.getColor(this, R.color.add_service_step_txt_gray));
        }

        if(locationCompleted){
            addServiceFour.setImageResource(R.drawable.ic_4_green);
            addServiceLocation.setTextColor(ContextCompat.getColor(this, R.color.color_active));
        }else{
            addServiceFour.setImageResource(R.drawable.ic_4_gray);
            addServiceLocation.setTextColor(ContextCompat.getColor(this, R.color.add_service_step_txt_gray));
        }

        switch (state){
            case STATE_OVERVIEW:
                addServiceOne.setImageResource(R.drawable.ic_1_blue);
                addServiceOverview.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                addServiceSaveButton.setText("Save and Continue");
                break;
            case STATE_SERVICES:
                addServiceTwo.setImageResource(R.drawable.ic_2_blue);
                addServiceServices.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                addServiceSaveButton.setText("Save and Continue");
                break;
            case STATE_GALLERY:
                addServiceThree.setImageResource(R.drawable.ic_3_blue);
                addServiceGallery.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                addServiceSaveButton.setText("Save and Continue");
                break;
            case STATE_LOCATION:
                addServiceFour.setImageResource(R.drawable.ic_4_blue);
                addServiceLocation.setTextColor(ContextCompat.getColor(this, R.color.txt_highlight));
                addServiceSaveButton.setText("Save and Finish");
                break;
        }
    }
}

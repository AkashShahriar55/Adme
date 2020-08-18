package com.cookietech.adme.activities.ui.today;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.LoadingDialog;
import com.cookietech.adme.Helpers.Service;
import com.cookietech.adme.Helpers.User;
import com.cookietech.adme.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    User currentUser;

    Service newService = new Service();
    Uri[] imageUris = new Uri[3];
    int main_count = 0,download_count = 0;
    private String serviceRef = null;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://adme-bf48a.appspot.com");
    StorageReference service_portfolio_ref = storage.getReference().child(FirebaseUtilClass.STORAGE_FOLDER_SERVICE_PORTFOLIO);
    UploadTask uploadTask ;
    FirebaseUtilClass firebaseUtilClass =FirebaseUtilClass.getInstance();
    LoadingDialog dialog;

    private boolean isEditing = false;
    List<String> imageUrls = new ArrayList<>();
    List<String> previousGalleryList = new ArrayList<>();

    int serviceIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);

        dialog = new LoadingDialog(this,"none","none");

        isEditing = getIntent().getBooleanExtra("is_editing",false);

        if(isEditing){
            newService = getIntent().getParcelableExtra("edit_service");
            serviceRef = newService.getmServiceId();
            previousGalleryList.addAll(newService.getFeature_images());
            imageUrls = newService.getFeature_images();
            currentUser = getIntent().getParcelableExtra(FirebaseUtilClass.CURRENT_USER_ID);
            serviceIndex = getIntent().getIntExtra("service_index",-1);
            overviewCompleted = true;
            servicesCompleted = true;
            galleryCompleted = true;
            locationCompleted = true;
        }else{
            currentUser = getIntent().getParcelableExtra(FirebaseUtilClass.CURRENT_USER_ID);
        }




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


        fragmentOverview = new AddServiceOverview(newService,isEditing);
        fragmentServices = new AddServiceServices(newService,isEditing);
        fragmentGallery = new AddServiceGallery(imageUris,imageUrls,isEditing,serviceRef);
        fragmentLocation = new AddServiceLocation(newService,isEditing);
        fragmentManager = getSupportFragmentManager();

        showFragment(fragmentOverview,OVERVIEW,STATE_OVERVIEW);
        updateStepNavigationUi();
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
        Log.d(TAG, "saveButtonClicked: "+newService.getCategory()+" "+newService.getDescription()+" "+newService.getWorking_hour());
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

            if(isEditing){
                Log.d("akash_debug", "saveDataAndFinish: ");
                updateService();
            }else{
                finishAddingNewService();
            }


        }
    }

    private void updateService() {
        List<String> tags = new ArrayList<>();
        String tokens[] = newService.getCategory().toLowerCase().split("\\s+");
        tags.addAll(Arrays.asList(tokens));
        for(Map<String,String> service: newService.getServices()){
            tokens = service.get(FirebaseUtilClass.ENTRY_SERVICE_TITLE).toLowerCase().split("\\s+");
            tags.addAll(Arrays.asList(tokens));
        }

        newService.setTags(tags);
        Log.d("akash_debug", "onCreate: size of gallary"+previousGalleryList.size());
        for(String url:previousGalleryList){
            Log.d("akash_debug", "updateService: all image in previous"+url);
            if(!imageUrls.contains(url)){
                Log.d("akash_debug", "updateService: image to be deleted"+url);
                firebaseUtilClass.deleteImageFromDatabase(url);
            }
        }

        uploadImageToServer();
    }

    private void finishAddingNewService() {
        newService.setUser_name(currentUser.getUser_name());
        newService.setUser_ref(currentUser.getmUserId());
        newService.setStatus(currentUser.getStatus());
        List<String> tags = new ArrayList<>();
        String tokens[] = newService.getCategory().toLowerCase().split("\\s+");
        tags.addAll(Arrays.asList(tokens));
        for(Map<String,String> service: newService.getServices()){
            tokens = service.get(FirebaseUtilClass.ENTRY_SERVICE_TITLE).toLowerCase().split("\\s+");
            tags.addAll(Arrays.asList(tokens));
        }

        newService.setTags(tags);
        newService.setStatus(currentUser.getStatus());
        FirebaseUser user = firebaseUtilClass.getCurrentUser();
        newService.setPic_url(String.valueOf(user.getPhotoUrl()));
        uploadImageToServer();
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
                if(isEditing){
                    addServiceSaveButton.setText("Update and Finish");
                }else{
                    addServiceSaveButton.setText("Save and Finish");
                }

                break;
        }
    }

    private void uploadImageToServer() {

        for(Uri uri:imageUris){
            if(uri!=null){
                main_count+=1;
            }
        }
        if(main_count <= 0){
            if(isEditing){
                updateToDatabase();
            }
            else{
                uploadToDatabase();
            }
            return;
        }
        dialog.show();
        dialog.updateTitle("Uploading images");
        dialog.updateProgress("uploading: 0 mb");
        for(Uri uri:imageUris){
            if(uri != null){
                StorageReference image = service_portfolio_ref.child(uri.getLastPathSegment());
                uploadTask = image.putFile(uri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return image.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            handleUploadedImageUri(downloadUri);
                            Log.d("akash_debug", "onComplete: "+downloadUri);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }

        }

        if(main_count > 0){
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getUploadSessionUri();
                    Log.d("akash_debug", "onSuccess: "+taskSnapshot.getUploadSessionUri());
                    dialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    //long percent = (taskSnapshot.getBytesTransferred()/ finalTotal_size) * 100;
                    String mb = String.format("%.2f",taskSnapshot.getBytesTransferred()/125000.0);
                    dialog.updateProgress("Uploading: "+mb+" mb");
                }
            });
        }


    }


    private void handleUploadedImageUri(Uri uri) {
        imageUrls.add(String.valueOf(uri));
        download_count+=1;
        if(main_count==download_count){
            newService.setFeature_images(imageUrls);
            if(isEditing){
                updateToDatabase();
            }
            else{
                uploadToDatabase();
            }

        }
    }

    private void updateToDatabase() {
        dialog.show();
        dialog.updateTitle("Updating service");
        dialog.updateProgress("Please wait");
        Log.d("akash_debug", "service id: "+newService.getUser_name());
        firebaseUtilClass.updateUserService(newService.getmServiceId(), newService, new FirebaseUtilClass.DatabaseOperationListener() {
            @Override
            public void onSuccess(Object object) {
                firebaseUtilClass.updateServiceInUserInfo(newService, currentUser, serviceIndex, new FirebaseUtilClass.DatabaseOperationListener() {
                    @Override
                    public void onSuccess(Object object) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void uploadToDatabase() {
        dialog.show();
        dialog.updateTitle("Creating service");
        dialog.updateProgress("Please wait");
        firebaseUtilClass.uploadUserService(newService, new FirebaseUtilClass.DatabaseOperationListener() {
            @Override
            public void onSuccess(Object object) {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }
}

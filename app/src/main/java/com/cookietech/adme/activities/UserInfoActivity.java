package com.cookietech.adme.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookietech.adme.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_PICK_CODE = 11;
    private CircleImageView choose_photo,profile_photo;
    private TextInputLayout edt_profile_username;
    private Button profile_continue_btn;
    private ConstraintLayout user_info_top_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initializeFields();

        choose_photo.setOnClickListener(v -> uploadImage());
        profile_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccessLocationActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserInfo();
    }

    private void setUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        if(user.getPhotoUrl() != null){
            Log.d("akash_debug", "setUserInfo: " + user.getPhotoUrl());
            Glide.with(UserInfoActivity.this).load(user.getPhotoUrl()).into(profile_photo);
        }

        if (user.getDisplayName() != null){
            edt_profile_username.getEditText().setText(user.getDisplayName());
        }
        else {
            String username = "Adme User";
            edt_profile_username.getEditText().setText(username);
        }
    }

    private void initializeFields() {

        choose_photo = findViewById(R.id.choose_photo);
        profile_photo = findViewById(R.id.profile_photo);
        edt_profile_username = findViewById(R.id.edt_profile_username);
        profile_continue_btn = findViewById(R.id.profile_continue_btn);
        user_info_top_back = findViewById(R.id.user_info_top_back);


    }


    private void uploadImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //Check for Permission
            if (checkPermission()){
                //Permission is already Granted
                PickImageFromGallery();
            }
            else{
                //permission is not granted
                requestPermission();
            }

        }

        else {
            //Do  Not Need Permission
            PickImageFromGallery();

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {


        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(UserInfoActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);

            builder.setTitle("External Storage Permission").setMessage("External storage permission is must to read your image gallery")
                    .setPositiveButton("Proceed", (dialog, which) -> {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(UserInfoActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(UserInfoActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    //Handle Request Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(UserInfoActivity.this, "Called", Toast.LENGTH_SHORT).show();

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                /*** If Storage Permission Is Given, Check External storage is available for read and write***/
                PickImageFromGallery();
                //Toast.makeText(UserInfoActivity.this, "Permission Granted.", Toast.LENGTH_LONG).show();

            }

            else {

                Toast.makeText(UserInfoActivity.this, "You Should Allow External Storage Permission To Upload image from gallery.", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void PickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, UserInfoActivity.IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            assert data != null;
            //profile_photo.setImageURI(data.getData());
            Glide.with(UserInfoActivity.this)
                    .load(data.getData())
                    .fitCenter()
                    .into(profile_photo);
        }
    }

    private void startAccessLocationActivity() {
        Intent intent = new Intent(UserInfoActivity.this, AccessLocationActivity.class);
        startActivity(intent);
    }
}

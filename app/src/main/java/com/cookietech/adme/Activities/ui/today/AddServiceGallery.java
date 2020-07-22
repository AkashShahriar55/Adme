package com.cookietech.adme.Activities.ui.today;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.R;

import java.util.List;

import static android.app.Activity.RESULT_OK;


public class AddServiceGallery extends Fragment implements AddServicesActivity.SaveFragmentListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_PICK_CODE_1 = 100,IMAGE_PICK_CODE_2 = 101, IMAGE_PICK_CODE_3 = 102;
    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;
    private Uri imageUris[];

    private ImageView service_image_1,service_image_2,service_image_3;
    private int code;

    private ImageButton btn_delete_image_1,btn_delete_image_2,btn_delete_image_3;

    private boolean isEditing;
    private List<String> imageUrls;
    private String serviceRef= "";
    private FirebaseUtilClass firebaseUtilClass = new FirebaseUtilClass();


    public AddServiceGallery(Uri imageUris[],List<String> imageUrls,boolean isEditing,String serviceRef ) {
        this.imageUris = imageUris;
        this.imageUrls = imageUrls;
        this.isEditing = isEditing;
        this.serviceRef = serviceRef;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_service_gallery_fragment, container, false);

        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {
        service_image_1 = root.findViewById(R.id.service_image_1);
        service_image_2 = root.findViewById(R.id.service_image_2);
        service_image_3 = root.findViewById(R.id.service_image_3);
        btn_delete_image_1 = root.findViewById(R.id.btn_delete_image_1);
        btn_delete_image_2 = root.findViewById(R.id.btn_delete_image_2);
        btn_delete_image_3 = root.findViewById(R.id.btn_delete_image_3);

        service_image_1.setOnClickListener(v -> {
            code = IMAGE_PICK_CODE_1;
            uploadImage();


        });

        service_image_2.setOnClickListener(v -> {
            code = IMAGE_PICK_CODE_2;
            uploadImage();



        });

        service_image_3.setOnClickListener(v -> {
            code = IMAGE_PICK_CODE_3;
            uploadImage();


        });

        if(isEditing){
            updateUi();
        }





    }

    private void updateUi() {
        ImageView[] imageViewList = {service_image_1,service_image_2,service_image_3};
        ImageButton[] deleteButtonList = {btn_delete_image_1,btn_delete_image_2,btn_delete_image_3};
        for (int i = 0; i < imageUrls.size(); i++) {
            final int position = i;
            Glide.with(requireContext())
                    .load(imageUrls.get(i))
                    .fitCenter()
                    .into(imageViewList[i]);
            isValidationChecked = true;
            imageViewList[i].setClickable(false);
            deleteButtonList[i].setVisibility(View.VISIBLE);
            deleteButtonList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageViewList[position].setClickable(true);
                    deleteButtonList[position].setVisibility(View.INVISIBLE);
                    int drawableResourceId = getActivity().getResources().getIdentifier("ic_upload", "drawable", getActivity().getPackageName());
                    Glide.with(requireContext())
                            .load(drawableResourceId)
                            .centerInside()
                            .into(imageViewList[position]);
                    imageUrls.remove(position);
                }
            });
        }
    }

    private void uploadImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //Check for Permission
            if (checkPermission()){
                //Permission is already Granted
                PickImageFromGallery(code);
            }
            else{
                //permission is not granted
                requestPermission();
            }

        }

        else {
            //Do  Not Need Permission
            PickImageFromGallery(code);

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {


        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            builder.setTitle("External Storage Permission").setMessage("External storage permission is must to read your image gallery")
                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // No explanation needed, we can request the permission.
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_CODE);
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
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    //Handle Request Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Toast.makeText(getContext(), "Called", Toast.LENGTH_SHORT).show();

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                /*** If Storage Permission Is Given, Check External storage is available for read and write***/
                PickImageFromGallery(code);
                Toast.makeText(getContext(), "Permission Granted.", Toast.LENGTH_LONG).show();

            }

            else {

                Toast.makeText(getContext(), "You Should Allow External Storage Permission To Upload image from gallery.", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void PickImageFromGallery(int code){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,code);
    }

    //Handle Image Picker Result


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_CODE_1){
                assert data != null;
                imageUris[0] = data.getData();
                Glide.with(requireContext())
                        .load(data.getData())
                        .fitCenter()
                        .into(service_image_1);
                isValidationChecked = true;
            }

            else if (requestCode == IMAGE_PICK_CODE_2){
                assert data != null;
                imageUris[1] = data.getData();
                Glide.with(requireContext())
                        .load(data.getData())
                        .fitCenter()
                        .into(service_image_2);
                isValidationChecked = true;
            }

            else if (requestCode == IMAGE_PICK_CODE_3){
                assert data != null;
                imageUris[2] = data.getData();
                Glide.with(requireContext())
                        .load(data.getData())
                        .fitCenter()
                        .into(service_image_3);
                isValidationChecked = true;
            }

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(imageUris[0]!= null){
            Glide.with(requireContext())
                    .load(imageUris[0])
                    .fitCenter()
                    .into(service_image_1);
        }
        if (imageUris[1]!=null){
            Glide.with(requireContext())
                    .load(imageUris[1])
                    .fitCenter()
                    .into(service_image_2);
        }
        if (imageUris[2]!=null){
            Glide.with(requireContext())
                    .load(imageUris[2])
                    .fitCenter()
                    .into(service_image_3);
        }
    }

    @Override
    public boolean isDataSaved() {
        return isDataSaved;
    }

    @Override
    public void saveData() {
        if(isValidationChecked){
            isDataSaved = true;
        }
    }





}

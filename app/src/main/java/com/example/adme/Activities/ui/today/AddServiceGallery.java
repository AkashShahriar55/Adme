package com.example.adme.Activities.ui.today;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.adme.R;

public class AddServiceGallery extends Fragment implements AddServicesActivity.SaveFragmentListener {

    private AddServiceGalleryViewModel mViewModel;
    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_service_gallery_fragment, container, false);

        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

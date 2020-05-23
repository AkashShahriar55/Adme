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

public class AddServiceOverview extends Fragment implements AddServicesActivity.SaveFragmentListener {

    private AddServiceOverviewViewModel mViewModel;
    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;
    private CheckBox testCheckbox;


    public static AddServiceOverview newInstance() {
        return new AddServiceOverview();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_service_overview_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testCheckbox = view.findViewById(R.id.overview_checkbox);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        testCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isValidationChecked = isChecked;

            }
        });
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

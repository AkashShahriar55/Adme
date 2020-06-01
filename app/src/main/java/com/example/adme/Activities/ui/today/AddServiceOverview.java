package com.example.adme.Activities.ui.today;

import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.adme.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.collection.LLRBNode;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Objects;

public class AddServiceOverview extends Fragment implements AddServicesActivity.SaveFragmentListener {

    private static final String TAG = "AddServiceOverview";

    private AddServiceOverviewViewModel mViewModel;
    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;
    private Spinner service_category_spinner;
    private Button start_time_btn,end_time_btn;
    private TextInputLayout edt_service_description;
    private Calendar startTime;
    private Calendar endTime;
    private TextView timeErrorTextView;
    private String descriptionText = "";
    private int previousSpinnerPosition = 0;




    public static AddServiceOverview newInstance() {
        return new AddServiceOverview();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_service_overview_fragment, container, false);
        initializeFields(root);

        return  root;
    }

    private void initializeFields(View root) {
        service_category_spinner = root.findViewById(R.id.service_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),R.array.service_category,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        service_category_spinner.setAdapter(adapter);
        start_time_btn = root.findViewById(R.id.start_time_btn);
        end_time_btn = root.findViewById(R.id.end_time_btn);
        edt_service_description = root.findViewById(R.id.edt_service_description);
        timeErrorTextView = root.findViewById(R.id.timeError);

        if(!descriptionText.isEmpty()){
            edt_service_description.getEditText().setText(descriptionText);
        }

        Objects.requireNonNull(edt_service_description.getEditText()).setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus && descriptionText.isEmpty()){
                edt_service_description.getEditText().setText(R.string.i_will_provide_with_clause);
            }

        });

        edt_service_description.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() != 0){
                    isDataSaved = false;
                    Log.i(TAG, "beforeTextChanged: "+isDataSaved);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length() > 20){
                    descriptionText = s.toString();
                }else{
                    descriptionText = "";
                }

                if(s.length() < 40){
                    edt_service_description.setErrorEnabled(true);
                    edt_service_description.setError("40 characters minimum");
                    isValidationChecked = false;
                }
                else{
                    isValidationChecked = true;
                    edt_service_description.setErrorEnabled(false);
                }
            }
        });


        service_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemSelected: "+isDataSaved+" "+previousSpinnerPosition + " "+position);
                Log.i(TAG, "onItemSelected: "+(position == previousSpinnerPosition));
                if(previousSpinnerPosition != position){
                    previousSpinnerPosition = position;
                    isDataSaved = false;
                    Log.i(TAG, "onItemSelected: "+isDataSaved+" "+previousSpinnerPosition + " "+position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY,10);
        startTime.set(Calendar.MINUTE,0);
        endTime.set(Calendar.HOUR_OF_DAY,16);
        endTime.set(Calendar.MINUTE,0);

        start_time_btn.setOnClickListener(v -> {



            TimePickerDialog dialog = new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    startTime.set(Calendar.MINUTE,minute);

                    String AM_PM;

                    Log.i(TAG, "onTimeSet: "+ hourOfDay + " "+ minute);

                    if (hourOfDay<12){
                        AM_PM = "AM";
                    }
                    else {
                        AM_PM = "PM";
                    }

                    if (hourOfDay % 12 == 0){
                        hourOfDay = 12;
                    }
                    else{
                        hourOfDay = hourOfDay % 12;
                    }

                    checkTimeValidation();

                    String s = new DecimalFormat("00").format(hourOfDay) + ":" + new DecimalFormat("00").format(minute) + " " + AM_PM;
                    start_time_btn.setText(s);
                    isDataSaved = false;
                    Log.i(TAG, "onTimeSet: "+isDataSaved);

                }
            },startTime.get(Calendar.HOUR_OF_DAY),startTime.get(Calendar.MINUTE),false);
            dialog.show();


        });


        end_time_btn.setOnClickListener(v -> {



            TimePickerDialog dialog = new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    endTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    endTime.set(Calendar.MINUTE,minute);
                    String AM_PM;

                    if (hourOfDay<12){
                        AM_PM = "AM";
                    }
                    else {
                        AM_PM = "PM";
                    }

                    if (hourOfDay % 12 == 0){
                        hourOfDay = 12;
                    }
                    else{
                        hourOfDay = hourOfDay % 12;
                    }
                    checkTimeValidation();
                    String s = new DecimalFormat("00").format(hourOfDay) + ":" + new DecimalFormat("00").format(minute) + " " + AM_PM;
                    end_time_btn.setText(s);
                    isDataSaved = false;
                    Log.i(TAG, "onTimeSet: "+isDataSaved);

                }
            },endTime.get(Calendar.HOUR_OF_DAY),endTime.get(Calendar.MINUTE),false);
            dialog.show();

        });

    }

    private void checkTimeValidation() {
        if(endTime.get(Calendar.HOUR_OF_DAY)-startTime.get(Calendar.HOUR_OF_DAY) < 0){
            isValidationChecked = false;
            timeErrorTextView.setVisibility(View.VISIBLE);
        }else if ( endTime.get(Calendar.HOUR_OF_DAY)== startTime.get(Calendar.HOUR_OF_DAY) && endTime.get(Calendar.MINUTE)-startTime.get(Calendar.MINUTE)<0){
            timeErrorTextView.setVisibility(View.VISIBLE);
            isValidationChecked = false;
        }else{
            isValidationChecked =true;
            timeErrorTextView.setVisibility(View.GONE);
        }
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
        Log.i(TAG, "isDataSaved: "+isDataSaved);
        return isDataSaved;
    }

    @Override
    public void saveData() {


        if(isValidationChecked){
            isDataSaved = true;
        }
    }
}

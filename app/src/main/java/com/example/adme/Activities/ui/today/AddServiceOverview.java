package com.example.adme.Activities.ui.today;

import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.adme.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Objects;

public class AddServiceOverview extends Fragment implements AddServicesActivity.SaveFragmentListener {

    private AddServiceOverviewViewModel mViewModel;
    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;
    private Spinner service_category_spinner;
    private Button start_time_btn,end_time_btn;



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
        Calendar c = Calendar.getInstance();


        /*DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate2();
            }
        };

        img_calender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog1 =new DatePickerDialog(getContext(), date1,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
//                mDatePickerDialog1.getDatePicker().setMaxDate(myCalendar.get(Calendar.DAY_OF_MONTH));
//                mDatePickerDialog1.getDatePicker().setMinDate((myCalendar.get(Calendar.DAY_OF_MONTH)-10));
                mDatePickerDialog1.show();
            }
        });*/

        start_time_btn.setOnClickListener(v -> {

            c.set(Calendar.HOUR_OF_DAY,10);
            c.set(Calendar.MINUTE,0);

            TimePickerDialog dialog = new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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

                    String s = new DecimalFormat("00").format(hourOfDay) + ":" + new DecimalFormat("00").format(minute) + " " + AM_PM;
                    start_time_btn.setText(s);

                }
            },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false);
            dialog.show();

        });

        end_time_btn.setOnClickListener(v -> {

            c.set(Calendar.HOUR_OF_DAY,16);
            c.set(Calendar.MINUTE,0);

            TimePickerDialog dialog = new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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

                    String s = new DecimalFormat("00").format(hourOfDay) + ":" + new DecimalFormat("00").format(minute) + " " + AM_PM;
                    end_time_btn.setText(s);

                }
            },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false);
            dialog.show();

        });

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

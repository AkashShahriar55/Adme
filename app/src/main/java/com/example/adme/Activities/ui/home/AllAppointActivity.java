package com.example.adme.Activities.ui.home;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.ui.income.IncomeViewModel;
import com.example.adme.Activities.ui.today.AppointmentAdapter;
import com.example.adme.Activities.ui.today.TodayViewModel;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.Helpers.LoadingDialog;
import com.example.adme.Helpers.RatingItem;
import com.example.adme.Helpers.UiHelper;
import com.example.adme.Helpers.User;
import com.example.adme.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllAppointActivity extends AppCompatActivity {
    private static final String TAG = "AllAppointActivity";
    ConstraintLayout empty_recyclerview_appointment;
    RecyclerView appointmentRecyclerView;
    RecyclerView.Adapter appointmentAdapter;
    ImageView img_back;
    TodayViewModel todayViewModel;
    User mCurrentUser ;
    LoadingDialog dialog;
    List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_all_appointments);

        todayViewModel = new ViewModelProvider(this).get(TodayViewModel.class);
        todayViewModel.getUserData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mCurrentUser = user;
                todayViewModel.fatchAllAppointmentList(user.getmUserId());
                Log.d("view-model", "onChanged:  bottom details" + user.getStatus());
            }
        });

        todayViewModel.getAllAppointmentList().observe(this, new Observer<List<Appointment>>() {
            @Override
            public void onChanged(List<Appointment> appointments) {
                Log.d(TAG, "onChanged: appointments "+appointments.size());
                appointmentList.clear();
                appointmentList.addAll(appointments);
                Collections.sort(appointmentList, new Comparator<Appointment>(){
                    public int compare(Appointment obj1, Appointment obj2) {
                        // ## Descending order
                        return obj2.getAppointmentID().compareToIgnoreCase(obj1.getAppointmentID()); // To compare string values
                        // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appointmentAdapter.notifyDataSetChanged();
                        if(appointmentList.size() == 0){
                            empty_recyclerview_appointment.setVisibility(View.VISIBLE);
                        }else{
                            empty_recyclerview_appointment.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        initializeFields();
    }

    private void initializeFields() {
        empty_recyclerview_appointment = findViewById(R.id.empty_recyclerview_appointment);
        appointmentRecyclerView = findViewById(R.id.appointment_container);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AllAppointActivity.this);
        appointmentRecyclerView.setHasFixedSize(true);
        appointmentRecyclerView.setLayoutManager(layoutManager);
        appointmentAdapter = new AppointmentAdapter(AllAppointActivity.this, appointmentList);

        if(appointmentList.size() == 0){
            empty_recyclerview_appointment.setVisibility(View.VISIBLE);
        }else{
            empty_recyclerview_appointment.setVisibility(View.GONE);
        }

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        updateView();
    }

    public void updateView() {
        MessageQueue.IdleHandler handler = new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                appointmentRecyclerView.setAdapter(appointmentAdapter);
                return false;
            }
        };
        Looper.myQueue().addIdleHandler(handler);
    }


}

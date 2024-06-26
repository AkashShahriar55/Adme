package com.example.adme.Activities.ui.servicehistory;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adme.Activities.ui.home.AllAppointActivity;
import com.example.adme.Activities.ui.home.AllAppointAdapter;
import com.example.adme.Activities.ui.today.AppointmentAdapter;
import com.example.adme.Activities.ui.today.TodayViewModel;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.LoadingDialog;
import com.example.adme.Helpers.User;
import com.example.adme.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServiceHistoryFragment extends Fragment {
    private static final String TAG = "ServiceHistoryFragment";
    ConstraintLayout empty_recyclerview_appointment;
    RecyclerView appointmentRecyclerView;
    RecyclerView.Adapter appointmentAdapter;
    ServiceHistoryViewModel todayViewModel;
    private User mCurrentUser ;
    LoadingDialog dialog;
    List<Appointment> appointmentList = new ArrayList<>();

    public static ServiceHistoryFragment newInstance() {
        return new ServiceHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_history_fragment, container, false);
        initializeFields(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        todayViewModel = new ViewModelProvider(requireActivity()).get(ServiceHistoryViewModel.class);
        todayViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mCurrentUser = user;
                todayViewModel.fatchAllAppointmentList(user.getmUserId());
                Log.d("view-model", "onChanged:  bottom details" + user.getStatus());
            }
        });

        todayViewModel.getAllAppointmentList().observe(getViewLifecycleOwner(), new Observer<List<Appointment>>() {
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
                requireActivity().runOnUiThread(new Runnable() {
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
    }

    private void initializeFields(View view) {
        empty_recyclerview_appointment = view.findViewById(R.id.empty_recyclerview_appointment);
        appointmentRecyclerView = view.findViewById(R.id.appointment_container);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        appointmentRecyclerView.setHasFixedSize(true);
        appointmentRecyclerView.setLayoutManager(layoutManager);
        appointmentAdapter = new AllAppointAdapter(getContext(), appointmentList);

        if(appointmentList.size() == 0){
            empty_recyclerview_appointment.setVisibility(View.VISIBLE);
        }else{
            empty_recyclerview_appointment.setVisibility(View.GONE);
        }
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

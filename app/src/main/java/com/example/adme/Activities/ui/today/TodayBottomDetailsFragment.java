package com.example.adme.Activities.ui.today;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.adme.R;

public class TodayBottomDetailsFragment extends Fragment {

    private TodayBottomDetailsViewModel mViewModel;
    private boolean isOnline;
    private View view;

    public TodayBottomDetailsFragment(boolean isOnline) {
        this.isOnline = isOnline;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.today_bottom_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button todayAddService = view.findViewById(R.id.today_add_service);
        this.view = view;

        ImageView notificationButton = view.findViewById(R.id.client_notification_btn);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotificationFragment();
            }
        });

        Switch todayStatusSwitch = view.findViewById(R.id.today_status_switch);
        todayStatusSwitch.setChecked(isOnline);
        if(isOnline){
            todayStatusSwitch.setText(R.string.online_status);
            todayStatusSwitch.setTextColor(getResources().getColor(R.color.color_active));
        }else{
            todayStatusSwitch.setText(R.string.offline_status);
            todayStatusSwitch.setTextColor(getResources().getColor(R.color.color_not_active));
        }
        todayStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                buttonView.setText(R.string.online_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_active));
                isOnline = true;
            }else{
                buttonView.setText(R.string.offline_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_not_active));
                isOnline = false;
            }
        });

        ImageView bottomDetailsButton = view.findViewById(R.id.bottom_details_button);
        bottomDetailsButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();

        });

        todayAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addServiceActivityIntent = new Intent(requireContext(),AddServicesActivity.class);
                requireContext().startActivity(addServiceActivityIntent);
            }
        });
    }

    private void goToNotificationFragment() {

        Fragment notificationFragment = new Notification_Fragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.replace(R.id.nav_host_fragment, notificationFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        RecyclerView appointmentRecyclerView = view.findViewById(R.id.appointment_container);
        RecyclerView serviceRecyclerView = view.findViewById(R.id.service_container);

        super.onActivityCreated(savedInstanceState);

        new Thread(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            appointmentRecyclerView.setHasFixedSize(true);
            appointmentRecyclerView.setLayoutManager(layoutManager);

            RecyclerView.Adapter adapter = new AppointmentAdapter(getContext(),getParentFragmentManager());
            appointmentRecyclerView.setAdapter(adapter);


            RecyclerView.LayoutManager serviceLayoutManager = new LinearLayoutManager(getContext());
            serviceRecyclerView.setHasFixedSize(true);
            serviceRecyclerView.setLayoutManager(serviceLayoutManager);

            RecyclerView.Adapter serviceAdapter = new ServiceAdapter(getContext());
            serviceRecyclerView.setAdapter(serviceAdapter);
        }).start();



    }

}

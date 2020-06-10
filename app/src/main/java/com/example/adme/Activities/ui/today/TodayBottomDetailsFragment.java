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
import android.widget.TextView;

import com.example.adme.Helpers.FirebaseUtilClass;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;
import com.example.adme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TodayBottomDetailsFragment extends Fragment {

    private TodayBottomDetailsViewModel mViewModel;
    private boolean isOnline;
    private View view;
    private User mCurrentUser;

    private TextView tv_income_today,tv_due, tv_pending_today, tv_appointments_today,tv_completed_today,tv_income_total;
    RecyclerView appointmentRecyclerView,serviceRecyclerView;
    Switch todayStatusSwitch;
    Button todayAddService;
    ImageView bottomDetailsButton,notificationButton;

    List<Service> services = new ArrayList<>();

    public TodayBottomDetailsFragment(User user) {
        this.mCurrentUser = user;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.today_bottom_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        
        initialization(view);

    }

    private void initialization(View view) {

        tv_income_today = view.findViewById(R.id.tv_income_today);
        tv_due =view.findViewById(R.id.tv_due);
        tv_completed_today = view.findViewById(R.id.tv_completed_today);
        tv_appointments_today = view.findViewById(R.id.tv_appointments_today);
        tv_pending_today = view.findViewById(R.id.tv_pending_today);
        tv_income_total = view.findViewById(R.id.tv_total_income);

        appointmentRecyclerView = view.findViewById(R.id.appointment_container);
        serviceRecyclerView = view.findViewById(R.id.service_container);

        todayAddService = view.findViewById(R.id.today_add_service);

        notificationButton = view.findViewById(R.id.client_notification_btn);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotificationFragment();
            }
        });

        todayStatusSwitch = view.findViewById(R.id.today_status_switch);
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

        bottomDetailsButton = view.findViewById(R.id.bottom_details_button);
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



        super.onActivityCreated(savedInstanceState);

        updateUi();



    }

    private void updateUi() {

        Map<String,String> serviceProviderInfo = mCurrentUser.getService_provider_info();
        tv_income_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_INCOME_TODAY));
        tv_completed_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_COMPLETED_TODAY));
        tv_due.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_DUE));
        tv_pending_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_PENDING_TODAY));
        tv_appointments_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_APPOINTMENTS_TODAY));
        tv_income_total.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_INCOME_TOTAL));

        if(mCurrentUser.getStatus().equals(FirebaseUtilClass.STATUS_ONLINE)){
            todayStatusSwitch.setChecked(true);
        }else if(mCurrentUser.getStatus().equals(FirebaseUtilClass.STATUS_OFFLINE)){
            todayStatusSwitch.setChecked(false);
        }

        new Thread(() -> {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            appointmentRecyclerView.setHasFixedSize(true);
            appointmentRecyclerView.setLayoutManager(layoutManager);

            RecyclerView.Adapter adapter = new AppointmentAdapter(getContext(),getParentFragmentManager());
            appointmentRecyclerView.setAdapter(adapter);


            RecyclerView.LayoutManager serviceLayoutManager = new LinearLayoutManager(getContext());
            serviceRecyclerView.setHasFixedSize(true);
            serviceRecyclerView.setLayoutManager(serviceLayoutManager);

            RecyclerView.Adapter serviceAdapter = new ServiceAdapter(getContext(),services);
            serviceRecyclerView.setAdapter(serviceAdapter);
        }).start();
    }

}

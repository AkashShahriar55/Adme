package com.cookietech.adme.activities.ui.today;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.Constants;
import com.cookietech.adme.Helpers.User;
import com.cookietech.adme.Architecture.UserDataModel;
import com.cookietech.adme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TodayBottomDetailsFragment extends Fragment {

    private static final String TAG = "TodayBottomDetailsFragm";

    private TodayBottomDetailsViewModel mViewModel;
    private boolean isOnline;
    private View view;
    private User mCurrentUser;

    private UserDataModel userDataModel;

    private TextView tv_income_today,tv_due, tv_pending_today, tv_appointments_today,tv_completed_today,tv_income_total;
    RecyclerView appointmentRecyclerView,serviceRecyclerView;
    Switch todayStatusSwitch;
    Button todayAddService;
    ImageView bottomDetailsButton,notificationButton;

    List<Map<String,String>> services = new ArrayList<>();

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDataModel = new ViewModelProvider(requireActivity()).get(UserDataModel.class);

        final Observer<User> userDataObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {

                mCurrentUser = user;
                Log.d("view-model", "onChanged:  bottom details" + user.getStatus());
                updateUi();
            }
        };

        userDataModel.getCurrentUser().observe(this,userDataObserver);
    }

    private void initialization(View view) {

        tv_income_today = view.findViewById(R.id.tv_income_today);
        tv_due =view.findViewById(R.id.tv_due);
        tv_completed_today = view.findViewById(R.id.tv_completed_today);
        tv_appointments_today = view.findViewById(R.id.tv_pressed_today);
        tv_pending_today = view.findViewById(R.id.tv_requested_today);
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
                mCurrentUser.setStatus(FirebaseUtilClass.STATUS_ONLINE);
                userDataModel.setCurrentUser(mCurrentUser);
            }else{
                buttonView.setText(R.string.offline_status);
                buttonView.setTextColor(getResources().getColor(R.color.color_not_active));
                mCurrentUser.setStatus(FirebaseUtilClass.STATUS_OFFLINE);
                userDataModel.setCurrentUser(mCurrentUser);
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
                requireActivity().startActivityForResult(addServiceActivityIntent, Constants.REQUEST_CODE_ADD_SERVICE_ACTIVITY);
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
        tv_pending_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_REQUESTED_TODAY));
        tv_appointments_today.setText(serviceProviderInfo.get(FirebaseUtilClass.ENTRY_PRESSED_TODAY));
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

            //RecyclerView.Adapter serviceAdapter = new ServiceAdapter(getContext(),services);
            //serviceRecyclerView.setAdapter(serviceAdapter);
        }).start();
    }

}

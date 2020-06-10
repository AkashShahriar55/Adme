package com.example.adme.Activities.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.adme.Activities.ui.today.Notification_Fragment;
import com.example.adme.R;


public class HomeBottomDetailsFragment extends Fragment {


    public HomeBottomDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home_bottom_details, container, false);

        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {
        RecyclerView available_service_rv = root.findViewById(R.id.available_service_rv);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        available_service_rv.setLayoutManager(layoutManager);
        available_service_rv.setHasFixedSize(true);
        AvailableServiceAdapter available_service_adapter = new AvailableServiceAdapter("homeBottomDetails");
        available_service_rv.setAdapter(available_service_adapter);



        //For Top Rated Service Provider
        RecyclerView top_rated_service_provider_rv =  root.findViewById(R.id.top_rated_service_provider_rv);
        RecyclerView.LayoutManager top_rated_layoutManager = new LinearLayoutManager(getContext());
        top_rated_service_provider_rv.setLayoutManager(top_rated_layoutManager);
        top_rated_service_provider_rv.setHasFixedSize(true);
        TopRatedServiceProviderAdapter topRatedServiceProviderAdapter = new TopRatedServiceProviderAdapter();
        top_rated_service_provider_rv.setAdapter(topRatedServiceProviderAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView bottomDetailsButton = view.findViewById(R.id.bottom_details_button);
        bottomDetailsButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();

        });


        ImageView client_notification_btn = view.findViewById(R.id.client_notification_btn);
        client_notification_btn.setOnClickListener(v -> goToNotificationFragment());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void goToNotificationFragment() {

        Fragment notificationFragment = new Notification_Fragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.replace(R.id.nav_host_fragment, notificationFragment);
        fragmentTransaction.commit();
    }
}

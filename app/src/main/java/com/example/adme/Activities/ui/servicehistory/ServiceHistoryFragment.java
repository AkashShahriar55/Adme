package com.example.adme.Activities.ui.servicehistory;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adme.Activities.Notification_class;
import com.example.adme.Activities.ui.today.NotificationItemInventoryAdapter;
import com.example.adme.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceHistoryFragment extends Fragment {

    private ServiceHistoryViewModel mViewModel;
    private List<ServiceHistoryItem> itemList = new ArrayList<>();
    ServiceListAdapter mAdapter;
    public static ServiceHistoryFragment newInstance() {
        return new ServiceHistoryFragment();
    }

    RecyclerView recyclerView_service_history;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_history_fragment, container, false);
        mAdapter = new ServiceListAdapter(itemList,getContext());
        recyclerView_service_history = view.findViewById(R.id.service_history_list);
        recyclerView_service_history = view.findViewById(R.id.notification);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_service_history.setLayoutManager(mLayoutManager);
        recyclerView_service_history.setItemAnimator(new DefaultItemAnimator());
        recyclerView_service_history.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ServiceHistoryViewModel.class);
        // TODO: Use the ViewModel
    }

}

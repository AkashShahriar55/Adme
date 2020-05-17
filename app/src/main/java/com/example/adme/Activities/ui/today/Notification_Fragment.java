package com.example.adme.Activities.ui.today;

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
import android.widget.ImageView;

import com.example.adme.Activities.Notification_class;
import com.example.adme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Notification_Fragment extends Fragment {

    private NotificationViewModel mViewModel;
    private RecyclerView recyclerView_notificaiton;
    private List<Notification_class> itemList = new ArrayList<>();
    private NotificationItemInventoryAdapter mAdapter;
    public static Notification_Fragment newInstance() {
        return new Notification_Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notification__fragment, container, false);


        mAdapter = new NotificationItemInventoryAdapter(itemList,getContext());

        recyclerView_notificaiton = view.findViewById(R.id.notification);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_notificaiton.setLayoutManager(mLayoutManager);
        recyclerView_notificaiton.setItemAnimator(new DefaultItemAnimator());
        recyclerView_notificaiton.setAdapter(mAdapter);
        ImageView button_back = view.findViewById(R.id.back_button);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        // TODO: Use the ViewModel

    }


}

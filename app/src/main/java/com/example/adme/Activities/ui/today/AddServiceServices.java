package com.example.adme.Activities.ui.today;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.adme.Activities.ui.leaderboard.LeaderBoardAdapter;
import com.example.adme.R;

public class AddServiceServices extends Fragment implements AddServicesActivity.SaveFragmentListener {

    private AddServiceServicesViewModel mViewModel;
    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;

    private RecyclerView ad_service_recyclerView;;
    private AdServiceAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public static AddServiceServices newInstance() {
        return new AddServiceServices();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_service_services_fragment, container, false);
        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {

        ad_service_recyclerView = root.findViewById(R.id.ad_service_recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        ad_service_recyclerView.setLayoutManager(layoutManager);
        ad_service_recyclerView.setHasFixedSize(true);
        adapter = new AdServiceAdapter();
        ad_service_recyclerView.setAdapter(adapter);

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

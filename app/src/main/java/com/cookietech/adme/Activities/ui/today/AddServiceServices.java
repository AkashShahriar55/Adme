package com.cookietech.adme.Activities.ui.today;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.AdServiceDialog;
import com.cookietech.adme.Helpers.Service;
import com.cookietech.adme.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddServiceServices extends Fragment implements AddServicesActivity.SaveFragmentListener, AdServiceDialog.AdServiceDialogListener, AdServiceAdapter.AddServiceAdapterListener {

    private boolean isValidationChecked= true;
    private boolean isDataSaved = false;
    private TextView ad_service_btn;
    private final String calledFrom = "fragment";
    private AdServiceAdapter adServiceAdapter;
    LinearLayout empty_recyclerview_layout;
    RecyclerView ad_service_recyclerView;

    private Service newService;

    private List<Map<String,String>> services = new ArrayList<>();

    private boolean isEditing;

    public AddServiceServices(Service newService,boolean isEditing) {
        this.newService = newService;
        this.isEditing = isEditing;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_service_services_fragment, container, false);
        initializeFields(root);
        return root;
    }

    private void initializeFields(View root) {
        empty_recyclerview_layout = root.findViewById(R.id.empty_recyclerview_layout);
        ad_service_recyclerView = root.findViewById(R.id.ad_service_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ad_service_recyclerView.setLayoutManager(layoutManager);
        ad_service_recyclerView.setHasFixedSize(true);
        adServiceAdapter = new AdServiceAdapter(requireContext(),services,this);
        ad_service_recyclerView.setAdapter(adServiceAdapter);
        ad_service_btn = root.findViewById(R.id.ad_service_btn);

        if(isEditing){
            services = newService.getServices();
            adServiceAdapter.setServiceList(services);
        }



        if(services.size()<=0){
            ad_service_recyclerView.setVisibility(View.GONE);
            empty_recyclerview_layout.setVisibility(View.VISIBLE);
        }



        ad_service_btn.setOnClickListener(v -> {
            openDialogFromFragment();
        });


    }


    private void openDialogFromFragment() {

        AdServiceDialog dialog = new AdServiceDialog(calledFrom);
        dialog.setTargetFragment(AddServiceServices.this,1);
        dialog.show(getParentFragmentManager(),"Ad Service Dialog");
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
            newService.setServices(services);
            isDataSaved = true;
        }
    }

    @Override
    public void dialogText(String service_name, String service_description, String service_charge) {
        isDataSaved = false;
        if (services.size() <= 0){
            ad_service_recyclerView.setVisibility(View.VISIBLE);
            empty_recyclerview_layout.setVisibility(View.GONE);
        }
        Map<String,String> service = new HashMap<>();
        service.put(FirebaseUtilClass.ENTRY_SERVICE_TITLE,service_name);
        service.put(FirebaseUtilClass.ENTRY_SERVICE_DESCRIPTION,service_description);
        service.put(FirebaseUtilClass.ENTRY_SERVICE_PRICE,service_charge);
        services.add(service);
        adServiceAdapter.setServiceList(services);


        Log.i("service_name",service_name);
        Log.i("service_description",service_description);
        Log.i("service_charge",service_charge);

    }

    @Override
    public void deleteService(int position) {
        isDataSaved = false;
        services.remove(position);
        adServiceAdapter.setServiceList(services);
        if (services.size() <= 0){
            ad_service_recyclerView.setVisibility(View.GONE);
            empty_recyclerview_layout.setVisibility(View.VISIBLE);
        }
    }
}

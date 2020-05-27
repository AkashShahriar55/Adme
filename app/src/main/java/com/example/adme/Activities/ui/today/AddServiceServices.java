package com.example.adme.Activities.ui.today;

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
import android.widget.TextView;

import com.example.adme.Helpers.AdServiceDialog;
import com.example.adme.R;

public class AddServiceServices extends Fragment implements AddServicesActivity.SaveFragmentListener, AdServiceDialog.AdServiceDialogListener {

    private AddServiceServicesViewModel mViewModel;
    private boolean isValidationChecked= false;
    private boolean isDataSaved = false;
    private TextView ad_service_btn;

    ;

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

        RecyclerView ad_service_recyclerView = root.findViewById(R.id.ad_service_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ad_service_recyclerView.setLayoutManager(layoutManager);
        ad_service_recyclerView.setHasFixedSize(true);
        AdServiceAdapter adapter = new AdServiceAdapter();
        ad_service_recyclerView.setAdapter(adapter);
        ad_service_btn = root.findViewById(R.id.ad_service_btn);

        ad_service_btn.setOnClickListener(v -> {
            openDialog();
        });

    }

    private void openDialog() {

        AdServiceDialog dialog = new AdServiceDialog();
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
            isDataSaved = true;
        }
    }

    @Override
    public void dialogText(String service_name, String service_description, String service_charge) {

        Log.i("service_name",service_name);
        Log.i("service_description",service_description);
        Log.i("service_charge",service_charge);

    }
}

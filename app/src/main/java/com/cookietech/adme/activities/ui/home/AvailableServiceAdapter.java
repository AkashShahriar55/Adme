package com.cookietech.adme.activities.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.adme.R;

public class AvailableServiceAdapter extends RecyclerView.Adapter<AvailableServiceAdapter.AvailableServiceViewHolder> {

    private String calledFrom = "";

    AvailableServiceAdapter() {
    }

    AvailableServiceAdapter(String calledFrom) {

        this.calledFrom = calledFrom;
    }

    @NonNull
    @Override
    public AvailableServiceAdapter.AvailableServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_service_item,parent,false);
        return new AvailableServiceAdapter.AvailableServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableServiceAdapter.AvailableServiceViewHolder holder, int position) {

        //ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.available_service_constraint_layout.getLayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT);


    }

    @Override
    public int getItemCount() {
        return 15;
    }

     class AvailableServiceViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout available_service_constraint_layout;
        AvailableServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            available_service_constraint_layout = itemView.findViewById(R.id.available_service_constraint_layout);
            ViewGroup.LayoutParams params = available_service_constraint_layout.getLayoutParams();

            if (calledFrom.equals("home")){
                params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                params.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                available_service_constraint_layout.setLayoutParams(params);

            }

            if (calledFrom.equals("homeBottomDetails")){
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                available_service_constraint_layout.setLayoutParams(params);
            }
        }
    }
}

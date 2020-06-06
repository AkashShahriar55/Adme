package com.example.adme.Activities.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

public class AvailableServiceAdapter extends RecyclerView.Adapter<AvailableServiceAdapter.AvailableServiceViewHolder> {

    AvailableServiceAdapter() {

    }

    @NonNull
    @Override
    public AvailableServiceAdapter.AvailableServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_service_item,parent,false);
        return new AvailableServiceAdapter.AvailableServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableServiceAdapter.AvailableServiceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class AvailableServiceViewHolder extends RecyclerView.ViewHolder{
        public AvailableServiceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

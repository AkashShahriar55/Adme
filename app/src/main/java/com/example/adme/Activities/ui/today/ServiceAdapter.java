package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

public class ServiceAdapter  extends  RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>{
    private Context context;

    public ServiceAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(context);
        view = mInflator.inflate(R.layout.services_item_layout,parent,false);
        return new ServiceAdapter.ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder{

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

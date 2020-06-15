package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Helpers.Service;
import com.example.adme.R;

import java.util.List;
import java.util.Map;

public class ServiceAdapter  extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Map<String,String>> services;
    public ServiceAdapter(Context context,List<Map<String,String>> services) {
        this.context = context;
        this.services = services;
    }

    public void setServices(List<Map<String, String>> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(context);
        view = mInflator.inflate(R.layout.services_item_layout,parent,false);
        return new ServiceAdapter.ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ServiceViewHolder serviceHolder = (ServiceViewHolder) holder;
        serviceHolder.viewServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ViewServiceDetails.class);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder{
        Button viewServiceButton;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            viewServiceButton = itemView.findViewById(R.id.view_service_button);
        }
    }

}

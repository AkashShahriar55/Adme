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

public class ServiceAdapter  extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Service> services;
    public ServiceAdapter(Context context,List<Service> services) {
        this.context = context;
        this.services = services;
    }

//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        LayoutInflater mInflator = LayoutInflater.from(context);
//
//        if(services.size() <= 0){
//            view = mInflator.inflate(R.layout.layout_empty_recycleview,parent,false);
//            return new ServiceAdapter.NoServiceViewHolder(view);
//        }else{
//            view = mInflator.inflate(R.layout.services_item_layout,parent,false);
//            return new ServiceAdapter.ServiceViewHolder(view);
//        }
//
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if(holder instanceof ServiceViewHolder){
//            ServiceViewHolder serviceHolder = (ServiceViewHolder) holder;
//            serviceHolder.viewServiceButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context,ViewServiceDetails.class);
//                    context.startActivity(intent);
//                }
//            });
//        }
//
//        if(holder instanceof NoServiceViewHolder){
//            NoServiceViewHolder noServiceViewHolder = (NoServiceViewHolder) holder;
//            noServiceViewHolder.tv_empty_recycleview_title.setText("You haven't added any service yet");
//            noServiceViewHolder.tv_empty_recycleview_button.setText("Add a Service");
//            noServiceViewHolder.tv_empty_recycleview_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent addServiceActivityIntent = new Intent(context,AddServicesActivity.class);
//                    context.startActivity(addServiceActivityIntent);
//                }
//            });
//        }
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        if(services.size() <= 0){
//            return 1;
//        }else{
//            return services.size();
//        }
//    }
//
//    static class ServiceViewHolder extends RecyclerView.ViewHolder{
//        Button viewServiceButton;
//        public ServiceViewHolder(@NonNull View itemView) {
//            super(itemView);
//            viewServiceButton = itemView.findViewById(R.id.view_service_button);
//        }
//    }
//
//
//    static class NoServiceViewHolder extends RecyclerView.ViewHolder{
//        TextView tv_empty_recycleview_button;
//        TextView tv_empty_recycleview_title;
//        public NoServiceViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tv_empty_recycleview_title = itemView.findViewById(R.id.tv_empty_recycleview_title);
//            tv_empty_recycleview_button = itemView.findViewById(R.id.tv_empty_recycleview_button);
//        }
//    }
//}



    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        return 2;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder{
        Button viewServiceButton;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            viewServiceButton = itemView.findViewById(R.id.view_service_button);
        }
    }
}

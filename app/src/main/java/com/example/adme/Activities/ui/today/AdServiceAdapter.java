package com.example.adme.Activities.ui.today;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Service;
import com.example.adme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Map<String,String>> serviceList;
    private AddServiceAdapterListener listener;

    AdServiceAdapter(Context context, List<Map<String, String>> services,AddServiceAdapterListener listener) {
        this.context = context;
        this.serviceList = services;
        this.listener = listener;
    }

    public void setServiceList(List<Map<String, String>> serviceList) {
        this.serviceList = serviceList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        Log.d("akash-debug", "onCreateViewHolder: ");
        view = inflater.inflate(R.layout.ad_service_item,parent,false);
        return new AddServiceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String,String> service = serviceList.get(position);
        AddServiceViewHolder serviceHolder = (AddServiceViewHolder) holder;
        serviceHolder.tv_service_title.setText(service.get(FirebaseUtilClass.ENTRY_SERVICE_TITLE));
        serviceHolder.tv_service_description.setText(service.get(FirebaseUtilClass.ENTRY_SERVICE_DESCRIPTION));
        serviceHolder.tv_service_price.setText("$"+service.get(FirebaseUtilClass.ENTRY_SERVICE_PRICE));
        serviceHolder.btn_service_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteService(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class AddServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tv_service_title;
        TextView tv_service_description;
        TextView tv_service_price;
        TextView btn_service_delete;
        AddServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_service_title = itemView.findViewById(R.id.tv_service_title);
            tv_service_description = itemView.findViewById(R.id.tv_service_description);
            tv_service_price = itemView.findViewById(R.id.tv_service_price);
            btn_service_delete = itemView.findViewById(R.id.btn_service_delete);
        }
    }

    public interface AddServiceAdapterListener{
        void deleteService(int position);
    }

}

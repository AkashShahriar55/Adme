package com.cookietech.adme.Activities.ui.today;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.R;

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
        view = inflater.inflate(R.layout.select_service_item,parent,false);
        return new AddServiceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String,String> service = serviceList.get(position);
        AddServiceViewHolder serviceHolder = (AddServiceViewHolder) holder;
        serviceHolder.tv_service_title.setText(service.get(FirebaseUtilClass.ENTRY_SERVICE_TITLE));
        serviceHolder.tv_service_details.setText(service.get(FirebaseUtilClass.ENTRY_SERVICE_DESCRIPTION));
        serviceHolder.tv_service_price.setText("$"+service.get(FirebaseUtilClass.ENTRY_SERVICE_PRICE));
        serviceHolder.tv_service_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteService(position);
            }
        });
        serviceHolder.tv_service_button.setText("Delete");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            serviceHolder.tv_service_button.setTextColor(context.getResources().getColor(R.color.color_negative,null));
        }else{
            serviceHolder.tv_service_button.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class AddServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tv_service_title;
        TextView tv_service_details;
        TextView tv_service_price;
        TextView tv_service_button;
        AddServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_service_title = itemView.findViewById(R.id.tv_service_title);
            tv_service_details = itemView.findViewById(R.id.tv_service_details);
            tv_service_price = itemView.findViewById(R.id.tv_service_price);
            tv_service_button = itemView.findViewById(R.id.tv_service_button);

        }
    }

    public interface AddServiceAdapterListener{
        void deleteService(int position);
    }

}

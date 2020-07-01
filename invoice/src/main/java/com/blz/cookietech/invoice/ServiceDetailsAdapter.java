package com.blz.cookietech.invoice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ServiceDetailsAdapter extends RecyclerView.Adapter<ServiceDetailsAdapter.ServiceDetailsViewHolders> {

    private ArrayList<Services> servicesList;

    public ServiceDetailsAdapter(ArrayList<Services> servicesList) {
        this.servicesList = servicesList;
    }

    @NonNull
    @Override
    public ServiceDetailsViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_items,parent,false);
        return new ServiceDetailsViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceDetailsViewHolders holder, int position) {
        Services services = servicesList.get(position);
        holder.service_name.setText(services.getService_name());

        String quantity = services.getService_quantity() + " x " + "$" + services.getService_cost();
        holder.service_quantity.setText(quantity);

        String cost = "$"+ services.getService_cost() * services.getService_quantity();
        holder.service_cost.setText(cost);

    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    static class ServiceDetailsViewHolders extends RecyclerView.ViewHolder{
        TextView service_name,service_quantity,service_cost;
        ServiceDetailsViewHolders(@NonNull View itemView) {
            super(itemView);
            service_name = itemView.findViewById(R.id.service_name);
            service_quantity = itemView.findViewById(R.id.service_quantity);
            service_cost = itemView.findViewById(R.id.service_cost);

        }
    }
}

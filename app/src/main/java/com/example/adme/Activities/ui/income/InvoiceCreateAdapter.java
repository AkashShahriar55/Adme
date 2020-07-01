package com.example.adme.Activities.ui.income;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blz.cookietech.invoice.Services;
import com.example.adme.R;

import java.util.List;

public class InvoiceCreateAdapter extends RecyclerView.Adapter<InvoiceCreateAdapter.InvoiceCreateViewHolder> {
    private Context context;
    private List<Services> servicesList;
    private InvoiceCreateAdapter.SelectInvoiceCreateAdapterListener listener;

    public interface SelectInvoiceCreateAdapterListener {
        void onServicesSelected(Services selectServicesItem);
    }

    InvoiceCreateAdapter() {}

    public InvoiceCreateAdapter(Context context, List<Services> servicesList, SelectInvoiceCreateAdapterListener listener) {
        this.context = context;
        this.servicesList = servicesList;
        this.listener = listener;
        Log.d("TAG", "InvoiceCreateAdapter: "+servicesList.size());
    }

    @NonNull
    @Override
    public InvoiceCreateAdapter.InvoiceCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_service_item, parent,false);
        return new InvoiceCreateAdapter.InvoiceCreateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceCreateAdapter.InvoiceCreateViewHolder holder, int position) {
        final Services selectServiceItem = servicesList.get(position);
        holder.tv_service_title.setText(selectServiceItem.getService_name());
        holder.tv_service_details.setText(selectServiceItem.getService_quantity()+" x $"+selectServiceItem.getService_cost());
        holder.tv_service_price.setText(""+(selectServiceItem.getService_cost()*selectServiceItem.getService_quantity()));
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    class InvoiceCreateViewHolder extends RecyclerView.ViewHolder {
        TextView tv_service_title, tv_service_details, tv_service_price, tv_service_button;

        InvoiceCreateViewHolder(View view) {
            super(view);
            tv_service_title = view.findViewById(R.id.tv_service_title);
            tv_service_details = view.findViewById(R.id.tv_service_details);
            tv_service_price = view.findViewById(R.id.tv_service_price);
            tv_service_button = view.findViewById(R.id.tv_service_button);
            tv_service_button.setText("Edit/Delete");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onServicesSelected(servicesList.get(getAdapterPosition()));
                }
            });
        }

    }
}

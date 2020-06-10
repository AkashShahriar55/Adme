package com.example.adme.Activities.ui.servicehistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adme.Activities.Notification_class;
import com.example.adme.Activities.ui.today.NotificationItemInventoryAdapter;
import com.example.adme.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.InventoryViewHolder> {
    private List<ServiceHistoryItem> itemList;
    private Context context;

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new ServiceListAdapter.InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {

        holder.cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {

        CardView cardView_item;
        TextView textView_serviceName, textView_clientName, textView_clientLocation, textView_clientComment, textView_star, textView_amount;

        public InventoryViewHolder(@NonNull View itemView) {

            super(itemView);
            textView_amount = itemView.findViewById(R.id.amount);
            textView_clientComment = itemView.findViewById(R.id.client_comment);
            textView_clientLocation = itemView.findViewById(R.id.client_location);
            textView_clientName = itemView.findViewById(R.id.client_name);
            textView_serviceName = itemView.findViewById(R.id.service_name);
            textView_star = itemView.findViewById(R.id.stars);
            cardView_item = itemView.findViewById(R.id.service_item);
        }
    }
    public ServiceListAdapter(List<ServiceHistoryItem> moviesList, Context context) {
        this.itemList = moviesList;

        this.context=context;
    }
}

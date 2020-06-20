package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adme.Activities.ui.income.RatingAndHistoryActicity;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.Helpers.Notification;
import com.example.adme.R;

import java.util.Calendar;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationItemInventoryAdapter extends RecyclerView.Adapter<NotificationItemInventoryAdapter.InventoryViewHolder> {
    private List<Notification> itemList;
    private Context context;
    private NotificationItemListener listener;

    public interface NotificationItemListener {
        void onNotificationItemSelected(Notification notification);
    }

    public NotificationItemInventoryAdapter(List<Notification> itemList, Context context) {
        this.itemList = itemList;
        this.context=context;
    }

    public NotificationItemInventoryAdapter(List<Notification> itemList, Context context, NotificationItemListener listener) {
        this.itemList = itemList;
        this.context=context;
        this.listener=listener;
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView time, notification_text;
        ImageView mv_icon,img_details;
        ConstraintLayout cl_view;

        public InventoryViewHolder(View view) {
            super(view);
            notification_text = view.findViewById(R.id.notification_text);
            time = view.findViewById(R.id.notification_time);
            mv_icon = view.findViewById(R.id.mv_icon);
            img_details = view.findViewById(R.id.img_details);
            cl_view = view.findViewById(R.id.cl_view);
        }
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        final Notification notification = itemList.get(position);

        Drawable saveIcon;
        if(notification.getType().equals("appointment")){
            saveIcon = context.getResources().getDrawable(R.drawable.ic_business_center_black_24dp);
            holder.cl_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ServiceProviderQuotationActivity.class);
                    intent.putExtra("reference", notification.getReference());
                    context.startActivity(intent);
                }
            });
        } else if(notification.getType().equals("rating")){
            saveIcon = context.getResources().getDrawable(R.drawable.ic_star_black_24dp);
            holder.cl_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, RatingAndHistoryActicity.class);
                    intent.putExtra("reference", notification.getReference());
                    context.startActivity(intent);
                }
            });
        } else {
            saveIcon = context.getResources().getDrawable(R.drawable.ic_notification);
            holder.img_details.setVisibility(View.GONE);
        }
        holder.mv_icon.setImageDrawable(saveIcon);

        if(!notification.isSeen()){
            holder.cl_view.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent_blue));
        }
        holder.notification_text.setText(notification.getText());
        holder.time.setText(CookieTechUtilityClass.getTimeDifference(notification.getTime(), String.valueOf(Calendar.getInstance().getTimeInMillis())));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
//        return 10;
    }


}

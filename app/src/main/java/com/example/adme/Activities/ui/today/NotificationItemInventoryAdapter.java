package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adme.Activities.Notification_class;
import com.example.adme.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationItemInventoryAdapter extends RecyclerView.Adapter<NotificationItemInventoryAdapter.InventoryViewHolder> {
    private List<Notification_class> itemList;
    private Context context;
    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);

        return new InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView_profilePic;
        TextView time,notification_text;

        public InventoryViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.notification_time);
            notification_text = view.findViewById(R.id.notification_text);
            imageView_profilePic = view.findViewById(R.id.profile_image);
        }


    }
    public NotificationItemInventoryAdapter(List<Notification_class> moviesList, Context context) {
        this.itemList = moviesList;

        this.context=context;
    }
}

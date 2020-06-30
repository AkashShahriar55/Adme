package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Service;
import com.example.adme.Helpers.User;
import com.example.adme.R;

import java.util.List;
import java.util.Map;

public class ServiceAdapter  extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Map<String,String>> services;
    private User mCurrentUser;
    public ServiceAdapter(Context context, List<Map<String,String>> services, User mCurrentUser) {
        this.context = context;
        this.services = services;
        this.mCurrentUser = mCurrentUser;
    }

    public void setServices(List<Map<String, String>> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    public void setmCurrentUser(User mCurrentUser) {
        this.mCurrentUser = mCurrentUser;

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
        Map<String,String> service_snap = services.get(position);
        ServiceViewHolder serviceHolder = (ServiceViewHolder) holder;
        serviceHolder.tv_category.setText(service_snap.get(FirebaseUtilClass.ENTRY_SERVICE_CATEGORY));
        serviceHolder.tv_description.setText(service_snap.get(FirebaseUtilClass.ENTRY_MAIN_SERVICE_DESCRIPTION));
        serviceHolder.tv_reviews.setText(service_snap.get(FirebaseUtilClass.ENTRY_SERVICE_REVIEWS));
        serviceHolder.rb_rating.setRating(Float.parseFloat(service_snap.get(FirebaseUtilClass.ENTRY_SERVICE_RATING)));
        serviceHolder.ct_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ViewServiceDetails.class);
                intent.putExtra("service_id",service_snap.get(FirebaseUtilClass.ENTRY_SERVICE_REFERENCE));
                intent.putExtra(FirebaseUtilClass.CURRENT_USER_ID,mCurrentUser);
                intent.putExtra("service_index",position);
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
        TextView tv_category,tv_description,tv_reviews;
        RatingBar rb_rating;
        ConstraintLayout ct_parent;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            viewServiceButton = itemView.findViewById(R.id.view_service_button);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_reviews = itemView.findViewById(R.id.tv_reviews);
            rb_rating = itemView.findViewById(R.id.rb_rating);
            ct_parent = itemView.findViewById(R.id.ct_parent);
        }
    }

}

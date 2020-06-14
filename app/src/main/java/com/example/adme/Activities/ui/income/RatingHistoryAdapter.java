package com.example.adme.Activities.ui.income;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingHistoryAdapter extends RecyclerView.Adapter<RatingHistoryAdapter.RatingHistoryViewHolder> {

    public RatingHistoryAdapter() {
    }

    @NonNull
    @Override
    public RatingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_rating_item,parent,false);
        return new RatingHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingHistoryViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 5;
    }


    static class RatingHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name,tv_time,tv_star,tv_detail,tv_money,tv_invoice;
        RatingBar ratingBar;

        RatingHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_star = itemView.findViewById(R.id.tv_star);
            tv_detail = itemView.findViewById(R.id.tv_detail);
            tv_money = itemView.findViewById(R.id.tv_money);
            tv_invoice = itemView.findViewById(R.id.tv_invoice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }


}

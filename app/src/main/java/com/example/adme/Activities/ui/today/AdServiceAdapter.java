package com.example.adme.Activities.ui.today;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.ui.leaderboard.LeaderBoardAdapter;
import com.example.adme.R;

public class AdServiceAdapter extends RecyclerView.Adapter<AdServiceAdapter.AdServiceViewHolder> {

    AdServiceAdapter() {
    }

    @NonNull
    @Override
    public AdServiceAdapter.AdServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_service_item,parent,false);
        return new AdServiceAdapter.AdServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdServiceAdapter.AdServiceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class AdServiceViewHolder extends RecyclerView.ViewHolder {
        AdServiceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

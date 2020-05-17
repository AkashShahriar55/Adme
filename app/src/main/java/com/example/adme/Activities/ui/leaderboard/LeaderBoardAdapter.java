package com.example.adme.Activities.ui.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder> {

    public LeaderBoardAdapter() {
    }

    @NonNull
    @Override
    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item,parent,false);
        return new LeaderBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return 10;
    }


    static class LeaderBoardViewHolder extends RecyclerView.ViewHolder {

        TextView username,service_name,service_type,service_charge;
        CircleImageView profile_image;

        LeaderBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            service_name = itemView.findViewById(R.id.service_name);
            service_type = itemView.findViewById(R.id.service_type);
            service_charge = itemView.findViewById(R.id.service_charge);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }


}

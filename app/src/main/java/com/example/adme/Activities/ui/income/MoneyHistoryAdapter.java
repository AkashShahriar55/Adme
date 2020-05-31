package com.example.adme.Activities.ui.income;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

public class MoneyHistoryAdapter extends RecyclerView.Adapter<MoneyHistoryAdapter.MoneyHistoryViewHolder> {

    public MoneyHistoryAdapter() {

    }

    @NonNull
    @Override
    public MoneyHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_money_item,parent,false);
        return new MoneyHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoneyHistoryViewHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return 10;
    }


    static class MoneyHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tv_value_money,tv_time_money,tv_title_money;
        ImageView img_money_status;
        
        MoneyHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title_money = itemView.findViewById(R.id.tv_title_money);
            tv_time_money = itemView.findViewById(R.id.tv_time_money);
            tv_value_money = itemView.findViewById(R.id.tv_value_money);
            img_money_status = itemView.findViewById(R.id.img_money_status);
        }
    }


}

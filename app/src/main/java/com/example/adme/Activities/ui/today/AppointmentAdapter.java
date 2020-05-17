package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private Context context;

    public AppointmentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflator = LayoutInflater.from(context);
        view = mInflator.inflate(R.layout.current_delayed_appointments,parent,false);
        return new AppointmentAdapter.AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }


    static class AppointmentViewHolder extends RecyclerView.ViewHolder{

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

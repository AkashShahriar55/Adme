package com.example.adme.Activities.ui.today;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.R;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private Context context;
    private FragmentManager fragmentManager;
    private static final String TAG = "AppointmentAdapter";

    public AppointmentAdapter(Context context,FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
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
        holder.appointmentDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: ");
//                Fragment appointmentFragment = new QuotationDetails();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(R.id.nav_host_fragment,appointmentFragment,"appointmentFragment");
//                transaction.addToBackStack(null);
//                transaction.commit();

                Intent intent = new Intent(context, ServiceProviderQuotationActivity.class);
//                intent.putExtra("reference", notification.getReference());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 4;
    }


    static class AppointmentViewHolder extends RecyclerView.ViewHolder{
        Button appointmentDetailsButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            appointmentDetailsButton = itemView.findViewById(R.id.appointment_details_button);
        }
    }
}

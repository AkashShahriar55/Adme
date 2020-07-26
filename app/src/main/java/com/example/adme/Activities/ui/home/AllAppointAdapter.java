package com.example.adme.Activities.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.ui.today.ServiceProviderQuotationActivity;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Appointment;
import com.example.adme.Helpers.AppointmentRef;
import com.example.adme.Helpers.CookieTechUtilityClass;
import com.example.adme.R;
import com.google.gson.Gson;
import java.util.List;

public class AllAppointAdapter extends RecyclerView.Adapter<AllAppointAdapter.AllAppointmentViewHolder> {
    private Context context;
    private List<AppointmentRef> appointmentRefList;
    private List<Appointment> appointmentList;
    private FragmentManager fragmentManager;
    private static final String TAG = "AppointmentAdapter";

    public AllAppointAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentRefList = appointmentRefList;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AllAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflator = LayoutInflater.from(context);
        View view = mInflator.inflate(R.layout.appointment_item,parent,false);
        return new AllAppointAdapter.AllAppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllAppointmentViewHolder holder, int position) {
        final Appointment appointment = appointmentList.get(position);

        String servicetext = appointment.getServices();
        if (servicetext.contains(",,,")) {
            servicetext = servicetext.replace(",,,", "\n");
        }
        holder.tv_service_list.setText(servicetext);
        holder.tv_clint_name.setText(appointment.getService_provider_name());
        holder.tv_time.setText((CookieTechUtilityClass.getTimeDate(appointment.getClint_time(), "hh:mm aa, dd MMM yyyy")));
        String loc = appointment.getClint_location().getName().substring(0, appointment.getClint_location().getName().lastIndexOf(","));
        holder.tv_clint_address.setText(loc);
        holder.tv_clint_text.setText(appointment.getClint_text());

        if(appointment.getPrice_needed()!= null){
            holder.tv_money.setText("$" + appointment.getPrice_needed());
        } else {
            holder.tv_money.setText("$" + appointment.getPrice_requested());
        }

        if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_FINISHED)){
            holder.tv_state.setText("State : Finished");
        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_CANCELED)){
            holder.tv_state.setText("State : Canceled by client");
        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_CANCELED)){
            holder.tv_state.setText("State : Canceled by service provider");
        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_CLINT_SEND)){
            holder.tv_state.setText("State : Request sent to service provider");
        }else if(appointment.getState().equals(FirebaseUtilClass.APPOINTMENT_STATE_SERVICE_PROVIDER_SEND)){
            holder.tv_state.setText("State : Quotation sent to client");
        }else{
            holder.tv_state.setText("State : Active Appointment");
        }

        holder.ct_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                Intent intent = new Intent(context, ServiceProviderQuotationActivity.class);
//                intent.putExtra("reference", appointment.getAppointmentID());
                intent.putExtra("from", "AppointmentAdapter");
                intent.putExtra("appointment", new Gson().toJson(appointment));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }


    static class AllAppointmentViewHolder extends RecyclerView.ViewHolder{
        TextView tv_clint_name,tv_time,tv_clint_address,tv_clint_text,tv_service_list,tv_money,tv_state;
        ImageView img_loc,img_msg;
        ConstraintLayout ct_details;

        public AllAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_clint_name = itemView.findViewById(R.id.tv_clint_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_clint_address = itemView.findViewById(R.id.tv_clint_address);
            tv_clint_text = itemView.findViewById(R.id.tv_clint_text);
            tv_service_list = itemView.findViewById(R.id.tv_service_list);
            tv_money = itemView.findViewById(R.id.tv_money);
            tv_state = itemView.findViewById(R.id.tv_state);
            ct_details = itemView.findViewById(R.id.ct_details);
            img_loc = itemView.findViewById(R.id.imageView21);
            img_msg = itemView.findViewById(R.id.imageView22);

            tv_clint_address.setVisibility(View.GONE);
            tv_clint_text.setVisibility(View.GONE);
            img_loc.setVisibility(View.GONE);
            img_msg.setVisibility(View.GONE);
        }
    }
}

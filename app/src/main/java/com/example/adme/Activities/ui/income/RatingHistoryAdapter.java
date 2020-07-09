package com.example.adme.Activities.ui.income;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Activities.ui.invoice.CustomerDetails;
import com.example.adme.Activities.ui.invoice.Invoice;
import com.example.adme.Activities.ui.invoice.Services;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingHistoryAdapter extends RecyclerView.Adapter<RatingHistoryAdapter.RatingHistoryViewHolder> {
    Context context;

    public RatingHistoryAdapter(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public RatingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_rating_item,parent,false);
        return new RatingHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingHistoryViewHolder holder, int position) {

        holder.ct_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Services> services = new ArrayList<>();
                services.add(new Services("Wall painting",50));
                services.add(new Services("Room Painting",90,3));

                String due_date = "12/7/2020";
                String customer_name = "Amanullah Asraf";
                String customer_phone = "+8801521304517";
                String customer_email ="amanullahoasraf@gmail.com";
                String customer_address = "57/3, Gulshan, Dhaka";
                String service_provider = "Akash Shahriar";
                String service_category = "Paint Job";
                String service_id = "101";
                double vat =0;
                double discount =20;

                CustomerDetails detailsForServices = new CustomerDetails(
                        due_date,
                        customer_name,
                        customer_phone,
                        customer_email,
                        customer_address,
                        service_provider,
                        service_category,
                        service_id,vat,
                        discount
                );

                Intent intent = new Intent(context, Invoice.class);
                intent.putExtra("service_details", detailsForServices);
                intent.putExtra("service_list", services);
                intent.putExtra("mode", FirebaseUtilClass.ENTRY_NOT_EDITABLE);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }


    static class RatingHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name,tv_time,tv_star,tv_detail,tv_money,tv_invoice;
        RatingBar ratingBar;
        ConstraintLayout ct_next;

        RatingHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_star = itemView.findViewById(R.id.tv_star);
            tv_detail = itemView.findViewById(R.id.tv_detail);
            tv_money = itemView.findViewById(R.id.tv_money);
            tv_invoice = itemView.findViewById(R.id.tv_invoice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ct_next = itemView.findViewById(R.id.ct_next);
        }
    }


}

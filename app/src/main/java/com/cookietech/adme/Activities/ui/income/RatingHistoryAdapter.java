package com.cookietech.adme.Activities.ui.income;

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

import com.cookietech.adme.Activities.ui.invoice.Invoice;
import com.cookietech.adme.Architecture.FirebaseUtilClass;
import com.cookietech.adme.Helpers.CookieTechUtilityClass;
import com.cookietech.adme.Helpers.RatingItem;
import com.cookietech.adme.R;

import java.util.List;

public class RatingHistoryAdapter extends RecyclerView.Adapter<RatingHistoryAdapter.RatingHistoryViewHolder> {
    private Context context;
    private List<RatingItem> ratingItemList;

    public RatingHistoryAdapter(Context context) {
        this.context=context;
    }

    public RatingHistoryAdapter(Context context, List<RatingItem> ratingItemList) {
        this.context=context;
        this.ratingItemList=ratingItemList;
    }

    @NonNull
    @Override
    public RatingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_rating_item,parent,false);
        return new RatingHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingHistoryViewHolder holder, int position) {
        final RatingItem ratingItem = ratingItemList.get(position);

        holder.tv_name.setText(ratingItem.getClintName());
        holder.tv_time.setText(CookieTechUtilityClass.getTimeDate(ratingItem.getTime(), "hh:mm aa, dd MMM yyyy"));
        holder.ratingBar.setRating(ratingItem.getRating());
        holder.tv_detail.setText(ratingItem.getComment());
        holder.tv_money.setText(ratingItem.getTotalPrice());
        holder.ct_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Invoice.class);
                intent.putExtra("from", "NotificationItemInventoryAdapter");
                intent.putExtra("reference", ratingItem.getInvoiceID());
                intent.putExtra("mode", FirebaseUtilClass.ENTRY_NOT_EDITABLE);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingItemList.size();
//        return 5;
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

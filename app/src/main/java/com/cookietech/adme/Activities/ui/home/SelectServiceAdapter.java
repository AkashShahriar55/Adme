package com.cookietech.adme.Activities.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.adme.Helpers.SelectServiceItem;
import com.cookietech.adme.R;

import java.util.List;

public class SelectServiceAdapter extends RecyclerView.Adapter<SelectServiceAdapter.SelectServiceViewHolder> {
    private Context context;
    private List<SelectServiceItem> SelectServiceList;
    private SelectServiceAdapter.SelectServiceAdapterListener listener;

    public interface SelectServiceAdapterListener {
        void onSelectServiceSelected(SelectServiceItem selectServiceItem);
    }

    SelectServiceAdapter() {}

    public SelectServiceAdapter(Context context, List<SelectServiceItem> SelectServiceList, SelectServiceAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.SelectServiceList = SelectServiceList;
    }

    @NonNull
    @Override
    public SelectServiceAdapter.SelectServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_service_item, parent,false);
        return new SelectServiceAdapter.SelectServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectServiceAdapter.SelectServiceViewHolder holder, int position) {
        final SelectServiceItem selectServiceItem = SelectServiceList.get(position);
        holder.tv_service_title.setText(selectServiceItem.getService_title());
        holder.tv_service_details.setText(selectServiceItem.getService_details());
        holder.tv_service_price.setText(selectServiceItem.getService_price());
    }

    @Override
    public int getItemCount() {
        return SelectServiceList.size();
//        return 3;
    }

    class SelectServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tv_service_title, tv_service_details, tv_service_price, tv_service_button;

        SelectServiceViewHolder(View view) {
            super(view);
            tv_service_title = view.findViewById(R.id.tv_service_title);
            tv_service_details = view.findViewById(R.id.tv_service_details);
            tv_service_price = view.findViewById(R.id.tv_service_price);
            tv_service_button = view.findViewById(R.id.tv_service_button);

//            Typeface tf0 = Typeface.createFromAsset(context.getAssets(), "fonts/Sansation-Regular.ttf");
//            current.setTypeface(tf0);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSelectServiceSelected(SelectServiceList.get(getAdapterPosition()));
                    if(tv_service_button.getText().equals("Add Service")){
                        tv_service_button.setTextColor(Color.RED);
                        tv_service_button.setText("Remove Service");
                    } else {
                        tv_service_button.setTextColor(Color.parseColor("#3F5AA6"));
                        tv_service_button.setText("Add Service");
                    }
                }
            });

        }
    }
}

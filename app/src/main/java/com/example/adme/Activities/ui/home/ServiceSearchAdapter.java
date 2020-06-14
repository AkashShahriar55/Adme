package com.example.adme.Activities.ui.home;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.adme.Architecture.FirebaseUtilClass;
import com.example.adme.Helpers.Service;
import com.example.adme.R;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceSearchAdapter extends RecyclerView.Adapter<ServiceSearchAdapter.MyViewHolder> {
    private Context context;
    private List<Service> serviceProviderList;
    private ServiceSearchAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_username, tv_work_done, tv_distance, tv_details,tv_min_fee,tv_to,tv_max_fee;
        public CircleImageView profile_image;
        public RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            tv_username = view.findViewById(R.id.tv_username);
            tv_work_done = view.findViewById(R.id.tv_work_done);
            tv_distance = view.findViewById(R.id.tv_distance);
            tv_details = view.findViewById(R.id.tv_details);
            tv_min_fee = view.findViewById(R.id.tv_min_fee);
            tv_to = view.findViewById(R.id.tv_to);
            tv_max_fee = view.findViewById(R.id.tv_max_fee);
            profile_image = view.findViewById(R.id.profile_image);
            ratingBar = view.findViewById(R.id.ratingBar);

//            Typeface tf0 = Typeface.createFromAsset(context.getAssets(), "fonts/Sansation-Regular.ttf");
//            current.setTypeface(tf0);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onServiceProviderSelected(serviceProviderList.get(getAdapterPosition()));
                }
            });
        }
    }


    public ServiceSearchAdapter(Context context, List<Service> serviceProviderList, ServiceSearchAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.serviceProviderList = serviceProviderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View serviceProviderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_service_item, parent, false);
        return new MyViewHolder(serviceProviderView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Service serviceProvider = serviceProviderList.get(position);

        holder.tv_username.setText(serviceProvider.getUser_name());
        holder.ratingBar.setRating(Float.parseFloat(serviceProvider.getRating()));
        holder.tv_work_done.setText(serviceProvider.getReviews());
        holder.tv_distance.setText("1.2 miles away");
        holder.tv_details.setText(serviceProvider.getShort_dis());

        int smallest = 1000;
        int largest = 0;
        for(Map<String,String> service : serviceProvider.getServices()){
            int num = Integer.parseInt(service.get(FirebaseUtilClass.ENTRY_SERVICE_PRICE));
            if (num > largest) {
                largest = num;
            }
            if (num < smallest) {
                smallest = num;
            }
        }
        holder.tv_min_fee.setText(smallest+"");
        holder.tv_max_fee.setText(largest+"");

        Glide.with(context)
                .load(serviceProvider.getPic_url())
                //.thumbnail(/*sizeMultiplier=*/ 0.9f)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return serviceProviderList.size();
//        return 5;
    }

    public interface ServiceSearchAdapterListener {
        void onServiceProviderSelected(Service serviceProvider);
    }
}

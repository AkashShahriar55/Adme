package com.example.adme.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adme.Helpers.FirebaseUtilClass;
import com.example.adme.Helpers.MyPlaces;
import com.example.adme.Helpers.User;
import com.example.adme.R;

import java.util.List;

public class PlaceSearchAdapter extends RecyclerView.Adapter<PlaceSearchAdapter.PlaceViewHolder> {

    private Context context;
    private List<MyPlaces> myPlacesList;
    private PlaceSearchItemListener listener;

    public PlaceSearchAdapter(Context context, List<MyPlaces> myPlacesList, PlaceSearchItemListener listener) {
        this.context = context;
        this.myPlacesList = myPlacesList;
        this.listener = listener;
    }

    public void setMyPlacesList(List<MyPlaces> myPlacesList) {
        this.myPlacesList = myPlacesList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_search_location,parent,false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        MyPlaces place = myPlacesList.get(position);
        holder.nameTV.setText(place.getName());
        holder.detailsTV.setText(place.getFormattedAddress());

        holder.itemHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlaceSelected(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myPlacesList.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder{
        TextView nameTV,detailsTV;
        ConstraintLayout itemHolder;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.place_name);
            detailsTV = itemView.findViewById(R.id.place_details);
            itemHolder = itemView.findViewById(R.id.place_search_item);

        }
    }

    public interface PlaceSearchItemListener{
        void onPlaceSelected(MyPlaces place);
    }
}

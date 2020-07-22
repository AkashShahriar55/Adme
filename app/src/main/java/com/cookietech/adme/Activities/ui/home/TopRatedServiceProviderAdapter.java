package com.cookietech.adme.Activities.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.adme.R;

public class TopRatedServiceProviderAdapter extends RecyclerView.Adapter<TopRatedServiceProviderAdapter.TopRatedServiceProviderViewHolder> {

    TopRatedServiceProviderAdapter(){

    }

    @NonNull
    @Override
    public TopRatedServiceProviderAdapter.TopRatedServiceProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_rated_service_provider_item,parent,false);
        return new TopRatedServiceProviderAdapter.TopRatedServiceProviderViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopRatedServiceProviderAdapter.TopRatedServiceProviderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class TopRatedServiceProviderViewHolder extends RecyclerView.ViewHolder{
        public TopRatedServiceProviderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

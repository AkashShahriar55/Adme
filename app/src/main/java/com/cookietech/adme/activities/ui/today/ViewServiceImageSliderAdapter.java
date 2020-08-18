package com.cookietech.adme.activities.ui.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cookietech.adme.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ViewServiceImageSliderAdapter extends  SliderViewAdapter<ViewServiceImageSliderAdapter.ImageSliderViewHolder>  {

    private Context context;
    private List<String> feature_image_url_list;


    public ViewServiceImageSliderAdapter(Context context, List<String> feature_image_url_list) {
        this.context = context;
        this.feature_image_url_list = feature_image_url_list;

    }

    public void setFeature_image_url_list(List<String> feature_image_url_list) {
        this.feature_image_url_list = feature_image_url_list;
        Toast.makeText(context,""+feature_image_url_list.size(),Toast.LENGTH_LONG).show();
        notifyDataSetChanged();
    }

    @Override
    public ImageSliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_service_image_slider, null);
        return new ImageSliderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ImageSliderViewHolder viewHolder, int position) {
        final String feature_image_url = feature_image_url_list.get(position);

        Glide.with(viewHolder.itemView)
                .load(feature_image_url)
                .centerCrop()
                .into(viewHolder.sliderImage);
    }

    @Override
    public int getCount() {

        return feature_image_url_list.size();
//        return 3;
    }

    class ImageSliderViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView sliderImage;
        public ImageSliderViewHolder(View itemView) {
            super(itemView);
            sliderImage = itemView.findViewById(R.id.view_service_image_slider_imageview);
        }
    }
}

package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.adme.Helpers.MyReader;
import com.example.adme.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class ViewServiceImageSliderAdapter extends  SliderViewAdapter<ViewServiceImageSliderAdapter.ImageSliderViewHolder>  {

    private Context context;

    public ViewServiceImageSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ImageSliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_service_image_slider, null);
        return new ImageSliderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ImageSliderViewHolder viewHolder, int position) {
        Glide.with(viewHolder.itemView)
                .load(MyReader.readImageFromAssets(context,"plumber_test.jpg"))
                .fitCenter()
                .into(viewHolder.sliderImage);
    }

    @Override
    public int getCount() {
        return 3;
    }

    class ImageSliderViewHolder extends SliderViewAdapter.ViewHolder
    {
        ImageView sliderImage;
        public ImageSliderViewHolder(View itemView) {
            super(itemView);

            sliderImage = itemView.findViewById(R.id.view_service_image_slider_imageview);
        }
    }
}

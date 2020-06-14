package com.example.adme.Activities.ui.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.adme.Activities.ui.home.ServiceProviderDetailsActivity;
import com.example.adme.Helpers.MyReader;
import com.example.adme.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ViewServiceImageSliderAdapter extends  SliderViewAdapter<ViewServiceImageSliderAdapter.ImageSliderViewHolder>  {

    private Context context;
    private String feature_image_url="";
    private List<String> feature_image_url_list;

    public ViewServiceImageSliderAdapter(Context context) {
        this.context = context;
    }

    public ViewServiceImageSliderAdapter(Context context, String picUrl) {
        this.context = context;
        this.feature_image_url = picUrl;
    }

    public ViewServiceImageSliderAdapter(Context context, List<String> feature_image_url_list) {
        this.context = context;
        this.feature_image_url_list = feature_image_url_list;
    }

    @Override
    public ImageSliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_service_image_slider, null);
        return new ImageSliderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ImageSliderViewHolder viewHolder, int position) {
        final String feature_image_url = feature_image_url_list.get(position);
        
        if(feature_image_url.equals("")){
            Glide.with(viewHolder.itemView)
                    .load(MyReader.readImageFromAssets(context,"plumber_test.jpg"))
                    .fitCenter()
                    .into(viewHolder.sliderImage);
        } else {
            Glide.with(viewHolder.itemView)
                    .load(feature_image_url)
                    .fitCenter()
                    .into(viewHolder.sliderImage);
        }
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

package com.feed.plugin.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.feed.plugin.R;
import com.feed.plugin.adapter.items.GPUImgItem;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImageView;

import java.util.ArrayList;

public class GpuimageSlideAdapter extends PagerAdapter{
    private ArrayList<GPUImgItem> mImages;
    private LayoutInflater inflater;
    private Context context;

    public GpuimageSlideAdapter(Context context){
        this.context = context;
    }
    public void setImages(ArrayList<GPUImgItem> images)
    {
        mImages = images;
    }
    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GPUImgItem item = mImages.get(position);

        View v = inflater.inflate(R.layout.slider_gpuimage, container, false);
        GPUImageView imageView = (GPUImageView)v.findViewById(R.id.slide_gpuimageview);

        imageView.setImage(BitmapFactory.decodeFile(item.getImagePath()));
        if(item.getFilter() != null)
        {
            imageView.setFilter(item.getFilter());
            imageView.requestRender();
        }


        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}

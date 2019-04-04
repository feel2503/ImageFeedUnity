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

import java.util.ArrayList;

public class ImageViewSlideAdapter extends PagerAdapter{
    private ArrayList<String> mImages;
    private LayoutInflater inflater;
    private Context context;

    private int selectPos = 0;


    public ImageViewSlideAdapter(Context context){
        this.context = context;
    }
    public void setImages(ArrayList<String> images)
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
        String path = mImages.get(position);

        View v = inflater.inflate(R.layout.slider_imageview, container, false);
        ImageView imageView = (ImageView)v.findViewById(R.id.slide_imageview);
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));

        container.addView(v);
        return v;


    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }

}

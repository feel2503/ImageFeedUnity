package com.feed.plugin;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.feed.plugin.adapter.GpuimageSlideAdapter;
import com.feed.plugin.adapter.ImageViewSlideAdapter;
import com.feed.plugin.adapter.items.GPUImgItem;

import java.util.ArrayList;

public class ImageViewActivity extends AppCompatActivity{
    private ArrayList<String> mImagList;
    private ViewPager mImagePager;
    private ImageViewSlideAdapter mImageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image_view);

        mImagList = getIntent().getStringArrayListExtra("ImageList");

        mImagePager = (ViewPager)findViewById(R.id.viewpager_image_view) ;
        mImageAdapter = new ImageViewSlideAdapter(getApplicationContext());

        mImageAdapter.setImages(mImagList);
        mImagePager.setAdapter(mImageAdapter);
    }
}

package com.feed.plugin.adapter.items;

import android.graphics.Bitmap;

import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.util.FilterUtils;

public class ThumbnailItem{
    public String filterName;
    public Bitmap image;
    public GPUImageFilter filter;
    public FilterUtils.FilterType filteType;

    public boolean isSelected = false;

    public ThumbnailItem() {
        image = null;
        filter = new GPUImageFilter();
    }


}

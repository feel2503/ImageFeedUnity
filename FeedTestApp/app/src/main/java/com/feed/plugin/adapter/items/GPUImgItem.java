package com.feed.plugin.adapter.items;

import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;

public class GPUImgItem{
    private String imagePath;
    private GPUImageFilter filter;
    private int filterValue;

    public GPUImgItem()
    {

    }

    public String getImagePath(){
        return imagePath;
    }

    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public GPUImageFilter getFilter(){
        return filter;
    }

    public void setFilter(GPUImageFilter filter){
        this.filter = filter;
    }

    public int getFilterValue(){
        return filterValue;
    }

    public void setFilterValue(int filterValue){
        this.filterValue = filterValue;
    }
}

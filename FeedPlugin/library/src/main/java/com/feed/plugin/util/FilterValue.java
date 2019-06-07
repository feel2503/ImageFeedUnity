package com.feed.plugin.util;

import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;

public class FilterValue implements Cloneable{
    private GPUImageFilter filter;
    private int value;

    public GPUImageFilter getFilter(){
        return filter;
    }

    public void setFilter(GPUImageFilter filter){
        this.filter = filter;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int value){
        this.value = value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}

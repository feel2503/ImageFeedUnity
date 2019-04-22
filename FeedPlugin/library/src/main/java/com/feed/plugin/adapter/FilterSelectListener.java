package com.feed.plugin.adapter;

import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;

public interface FilterSelectListener{
    void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect);
}

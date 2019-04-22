package com.feed.plugin.fragment;

import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;

public interface FiltersListSelectListener{
    void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect);
}

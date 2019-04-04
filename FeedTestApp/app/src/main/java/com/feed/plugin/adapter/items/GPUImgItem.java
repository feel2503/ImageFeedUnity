package com.feed.plugin.adapter.items;

import android.view.View;

import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImage3x3ConvolutionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImage3x3TextureSamplingFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageAddBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBilateralBlurFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBrightnessFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBulgeDistortionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageColorBalanceFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageContrastFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageCrosshatchFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDissolveBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageEmbossFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageExposureFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageGammaFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageGlassSphereFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHazeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHueFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLevelsFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLuminanceThresholdFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageMonochromeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageOpacityFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImagePixelationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImagePosterizeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageRGBFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSaturationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSepiaToneFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSharpenFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSobelEdgeDetectionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSolarizeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSphereRefractionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSwirlFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageThresholdEdgeDetectionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageVibranceFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageVignetteFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageWhiteBalanceFilter;

public class GPUImgItem{
    private String imagePath;
    private GPUImageFilter filter;
    private int filterValue;
    private View gpuImageView;

    public View getView(){
        return gpuImageView;
    }

    public void setView(View gpuImageView){
        this.gpuImageView = gpuImageView;
    }

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
        if(gpuImageView != null && gpuImageView instanceof GPUImageView)
        {
            ((GPUImageView) gpuImageView).setFilter(filter);
        }
    }

    public int getFilterValue(){
        return filterValue;
    }

    public void requestRender()
    {
        if(gpuImageView != null && gpuImageView instanceof GPUImageView)
            ((GPUImageView) gpuImageView).requestRender();
    }

    private float range(int percentage, float start, float end)
    {
        float result = (end - start) * percentage / 100.0f + start;
        return result;
    }

    private int range(int percentage, int start, int end)
    {
        int result = (end - start) * percentage / 100 + start;
        return result;
    }


    public void setFilterValue(int percentage){
        this.filterValue = percentage;

        if(filter instanceof GPUImageSharpenFilter)
        {
            ((GPUImageSharpenFilter)filter).setSharpness(range(percentage, -4.0f, 4.0f));
        }
        else if(filter instanceof GPUImagePixelationFilter)
        {
            ((GPUImagePixelationFilter)filter).setPixel(range(percentage, 1.0f, 100.0f));
        }
        else if(filter instanceof GPUImageHueFilter)
        {
            ((GPUImageHueFilter)filter).setHue(range(percentage, 0.0f, 360.0f));
        }
        else if(filter instanceof GPUImageContrastFilter)
        {
            ((GPUImageContrastFilter)filter).setContrast(range(percentage, 0.0f, 2.0f));
        }
        else if(filter instanceof GPUImageGammaFilter)
        {
            ((GPUImageGammaFilter)filter).setGamma(range(percentage, 0.0f, 3.0f));
        }
        else if(filter instanceof GPUImageBrightnessFilter)
        {
            ((GPUImageBrightnessFilter)filter).setBrightness(range(percentage, -1.0f, 1.0f));
        }
        else if(filter instanceof GPUImageSepiaToneFilter)
        {
            ((GPUImageSepiaToneFilter)filter).setIntensity(range(percentage, 0.0f, 2.0f));
        }
        else if(filter instanceof GPUImageSobelEdgeDetectionFilter)
        {
            ((GPUImageSobelEdgeDetectionFilter)filter).setLineSize(range(percentage, 0.0f, 5.0f));
        }
        else if(filter instanceof GPUImageThresholdEdgeDetectionFilter)
        {
            ((GPUImageThresholdEdgeDetectionFilter)filter).setLineSize(range(percentage, 0.0f, 5.0f));
            ((GPUImageThresholdEdgeDetectionFilter)filter).setThreshold(0.9f);
        }
        else if(filter instanceof GPUImage3x3ConvolutionFilter)
        {
            ((GPUImage3x3ConvolutionFilter)filter).setConvolutionKernel(new float[]{-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f});
        }
        else if(filter instanceof GPUImageEmbossFilter)
        {
            ((GPUImageEmbossFilter)filter).setIntensity(range(percentage, 0.0f, 4.0f));
        }
        else if(filter instanceof GPUImagePosterizeFilter)
        {
            // In theorie to 256, but only first 50 are interesting
            ((GPUImagePosterizeFilter)filter).setColorLevels(range(percentage, 1, 50));
        }
        else if(filter instanceof GPUImage3x3TextureSamplingFilter)
        {
            ((GPUImage3x3TextureSamplingFilter)filter).setLineSize(range(percentage, 0.0f, 5.0f));
        }
        else if(filter instanceof GPUImageSaturationFilter)
        {
            ((GPUImageSaturationFilter)filter).setSaturation(range(percentage, 0.0f, 2.0f));
        }
        else if(filter instanceof GPUImageExposureFilter)
        {
            ((GPUImageExposureFilter)filter).setExposure(range(percentage, -10.0f, 10.0f));
        }
        else if(filter instanceof GPUImageHighlightShadowFilter)
        {
            ((GPUImageHighlightShadowFilter)filter).setShadows(range(percentage, 0.0f, 1.0f));
            ((GPUImageHighlightShadowFilter)filter).setHighlights(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageMonochromeFilter)
        {
            ((GPUImageMonochromeFilter)filter).setIntensity(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageOpacityFilter)
        {
            ((GPUImageOpacityFilter)filter).setOpacity(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageRGBFilter)
        {
            ((GPUImageRGBFilter)filter).setRed(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageWhiteBalanceFilter)
        {
            ((GPUImageWhiteBalanceFilter)filter).setTemperature(range(percentage, 2000.0f, 8000.0f));
        }
        else if(filter instanceof GPUImageVignetteFilter)
        {
            ((GPUImageVignetteFilter)filter).setVignetteStart(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageLuminanceThresholdFilter)
        {
            ((GPUImageLuminanceThresholdFilter)filter).setThreshold(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageDissolveBlendFilter)
        {
            ((GPUImageDissolveBlendFilter)filter).setMix(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageGaussianBlurFilter)
        {
            ((GPUImageGaussianBlurFilter)filter).setBlurSize(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageCrosshatchFilter)
        {
            ((GPUImageCrosshatchFilter)filter).setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
            ((GPUImageCrosshatchFilter)filter).setLineWidth(range(percentage, 0.0f, 0.006f));
        }
        else if(filter instanceof GPUImageBulgeDistortionFilter)
        {
            ((GPUImageBulgeDistortionFilter)filter).setRadius(range(percentage, 0.0f, 1.0f));
            ((GPUImageBulgeDistortionFilter)filter).setScale(range(percentage, -1.0f, 1.0f));
        }
        else if(filter instanceof GPUImageGlassSphereFilter)
        {
            ((GPUImageGlassSphereFilter)filter).setRadius(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageHazeFilter)
        {
            ((GPUImageHazeFilter)filter).setDistance(range(percentage, -0.3f, 0.3f));
            ((GPUImageHazeFilter)filter).setSlope(range(percentage, -0.3f, 0.3f));
        }
        else if(filter instanceof GPUImageSphereRefractionFilter)
        {
            ((GPUImageSphereRefractionFilter)filter).setRadius(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageSwirlFilter)
        {
            ((GPUImageSwirlFilter)filter).setAngle(range(percentage, 0.0f, 2.0f));
        }
        else if(filter instanceof GPUImageColorBalanceFilter)
        {
            ((GPUImageColorBalanceFilter)filter).setMidtones(new float[]{
                            range(percentage, 0.0f, 1.0f),
                            range(percentage / 2, 0.0f, 1.0f),
                            range(percentage / 3, 0.0f, 1.0f)});
        }
        else if(filter instanceof GPUImageLevelsFilter)
        {
            ((GPUImageLevelsFilter)filter).setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f);
        }
        else if(filter instanceof GPUImageBilateralBlurFilter)
        {
            ((GPUImageBilateralBlurFilter)filter).setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f));
        }
        else if(filter instanceof GPUImageSolarizeFilter)
        {
            ((GPUImageSolarizeFilter)filter).setThreshold(range(percentage, 0.0f, 1.0f));
        }
        else if(filter instanceof GPUImageVibranceFilter)
        {
            ((GPUImageVibranceFilter)filter).setVibrance(range(percentage, -1.2f, 1.2f));
        }


        requestRender();
    }

}

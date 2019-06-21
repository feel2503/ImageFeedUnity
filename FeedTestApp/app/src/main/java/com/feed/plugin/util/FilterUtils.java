package com.feed.plugin.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.feed.plugin.R;
import com.feed.plugin.android.gpuimage.filter.GPUImage3x3ConvolutionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageAddBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageAlphaBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBilateralBlurFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBoxBlurFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBrightnessFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBulgeDistortionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageCGAColorspaceFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageChromaKeyBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageColorBalanceFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageColorBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageColorBurnBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageColorDodgeBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageColorInvertFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageContrastFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageCrosshatchFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDarkenBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDifferenceBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDilationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDirectionalSobelEdgeDetectionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDissolveBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDivideBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageEmbossFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageExclusionBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageExposureFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFalseColorFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilterGroup;
import com.feed.plugin.android.gpuimage.filter.GPUImageGammaFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageGlassSphereFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageGrayscaleFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHalftoneFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHardLightBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHazeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHueBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHueFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageKuwaharaFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLaplacianFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLevelsFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLightenBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLinearBurnBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLookupFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLuminosityBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageMonochromeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageMultiplyBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageNonMaximumSuppressionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageNormalBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageOpacityFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageOverlayBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImagePixelationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImagePosterizeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageRGBFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSaturationBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSaturationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageScreenBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSepiaToneFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSharpenFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSmoothToonFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSobelEdgeDetectionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSoftLightBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSolarizeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSourceOverBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSphereRefractionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSubtractBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSwirlFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageToneCurveFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageToonFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageTwoInputFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageVibranceFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageVignetteFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageWeakPixelInclusionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageWhiteBalanceFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageZoomBlurFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IF1977Filter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFAmaroFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFBrannanFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFEarlybirdFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFHefeFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFHudsonFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFInkwellFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFLomoFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFLordKelvinFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFNashvilleFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFRiseFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFSierraFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFSutroFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFToasterFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFValenciaFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFWaldenFilter;
import com.feed.plugin.android.gpuimage.filter.extfilter.IFXprollFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class FilterUtils{

    // 아래 필터 구분 필요
    // CONTRAST 대비
    // BRIGHTNESS 밝기
    // 선명도
    // 채도
    // 배경 흐리게
    public static enum FilterType {
        CONTRAST, GRAYSCALE, SHARPEN, SEPIA, SOBEL_EDGE_DETECTION, THRESHOLD_EDGE_DETECTION, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, PIXELATION,
        SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, BLEND_COLOR_BURN, BLEND_COLOR_DODGE, BLEND_DARKEN,
        BLEND_DIFFERENCE, BLEND_DISSOLVE, BLEND_EXCLUSION, BLEND_SOURCE_OVER, BLEND_HARD_LIGHT, BLEND_LIGHTEN, BLEND_ADD, BLEND_DIVIDE, BLEND_MULTIPLY, BLEND_OVERLAY, BLEND_SCREEN, BLEND_ALPHA,
        BLEND_COLOR, BLEND_HUE, BLEND_SATURATION, BLEND_LUMINOSITY, BLEND_LINEAR_BURN, BLEND_SOFT_LIGHT, BLEND_SUBTRACT, BLEND_CHROMA_KEY, BLEND_NORMAL, LOOKUP_AMATORKA,
        GAUSSIAN_BLUR, CROSSHATCH, BOX_BLUR, CGA_COLORSPACE, DILATION, KUWAHARA, RGB_DILATION, SKETCH, TOON, SMOOTH_TOON, BULGE_DISTORTION, GLASS_SPHERE, HAZE, LAPLACIAN, NON_MAXIMUM_SUPPRESSION,
        SPHERE_REFRACTION, SWIRL, WEAK_PIXEL_INCLUSION, FALSE_COLOR, COLOR_BALANCE, LEVELS_FILTER_MIN, BILATERAL_BLUR, ZOOM_BLUR, HALFTONE, SOLARIZE, VIBRANCE
//        CONTRAST, BRIGHTNESS, GRAYSCALE, SHARPEN, SEPIA, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, INVERT,
//        HUE, PIXELATION, SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, LOOKUP_AMATORKA,
//        I_1977, I_AMARO, I_BRANNAN, I_EARLYBIRD, I_HEFE, I_HUDSON, I_INKWELL, I_LOMO, I_LORDKELVIN, I_NASHVILLE, I_RISE, I_SIERRA, I_SUTRO,
//        I_TOASTER, I_VALENCIA, I_WALDEN, I_XPROII
    }

    public static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
        switch (type) {
            case CONTRAST:
                return new GPUImageContrastFilter(2.0f);
            case GAMMA:
                return new GPUImageGammaFilter(2.0f);
            case INVERT:
                return new GPUImageColorInvertFilter();
            case PIXELATION:
                return new GPUImagePixelationFilter();
            case HUE:
                return new GPUImageHueFilter(90.0f);
            case BRIGHTNESS:
                return new GPUImageBrightnessFilter(0.0f);
            case GRAYSCALE:
                return new GPUImageGrayscaleFilter();
            case SEPIA:
                return new GPUImageSepiaToneFilter();
            case SHARPEN:
                GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
                sharpness.setSharpness(2.0f);
                return sharpness;
            case SOBEL_EDGE_DETECTION:
                return new GPUImageSobelEdgeDetectionFilter();
            case THREE_X_THREE_CONVOLUTION:
                GPUImage3x3ConvolutionFilter convolution = new GPUImage3x3ConvolutionFilter();
                convolution.setConvolutionKernel(new float[] {
                        -1.0f, 0.0f, 1.0f,
                        -2.0f, 0.0f, 2.0f,
                        -1.0f, 0.0f, 1.0f
                });
                return convolution;
            case EMBOSS:
                return new GPUImageEmbossFilter();
            case POSTERIZE:
                return new GPUImagePosterizeFilter();
            case FILTER_GROUP:
                List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
                filters.add(new GPUImageContrastFilter());
                filters.add(new GPUImageDirectionalSobelEdgeDetectionFilter());
                filters.add(new GPUImageGrayscaleFilter());
                return new GPUImageFilterGroup(filters);
            case SATURATION:
                return new GPUImageSaturationFilter(1.0f);
            case EXPOSURE:
                return new GPUImageExposureFilter(0.0f);
            case HIGHLIGHT_SHADOW:
                return new GPUImageHighlightShadowFilter(0.0f, 1.0f);
            case MONOCHROME:
                return new GPUImageMonochromeFilter(1.0f, new float[]{0.6f, 0.45f, 0.3f, 1.0f});
            case OPACITY:
                return new GPUImageOpacityFilter(1.0f);
            case RGB:
                return new GPUImageRGBFilter(1.0f, 1.0f, 1.0f);
            case WHITE_BALANCE:
                return new GPUImageWhiteBalanceFilter(5000.0f, 0.0f);
            case VIGNETTE:
                PointF centerPoint = new PointF();
                centerPoint.x = 0.5f;
                centerPoint.y = 0.5f;
                return new GPUImageVignetteFilter(centerPoint, new float[] {0.0f, 0.0f, 0.0f}, 0.3f, 0.75f);
//            case TONE_CURVE:
//                GPUImageToneCurveFilter toneCurveFilter = new GPUImageToneCurveFilter();
//                toneCurveFilter.setFromCurveFileInputStream(
//                        context.getResources().openRawResource(R.raw.tone_cuver_sample));
//                return toneCurveFilter;
            case BLEND_DIFFERENCE:
                return createBlendFilter(context, GPUImageDifferenceBlendFilter.class);
            case BLEND_SOURCE_OVER:
                return createBlendFilter(context, GPUImageSourceOverBlendFilter.class);
            case BLEND_COLOR_BURN:
                return createBlendFilter(context, GPUImageColorBurnBlendFilter.class);
            case BLEND_COLOR_DODGE:
                return createBlendFilter(context, GPUImageColorDodgeBlendFilter.class);
            case BLEND_DARKEN:
                return createBlendFilter(context, GPUImageDarkenBlendFilter.class);
            case BLEND_DISSOLVE:
                return createBlendFilter(context, GPUImageDissolveBlendFilter.class);
            case BLEND_EXCLUSION:
                return createBlendFilter(context, GPUImageExclusionBlendFilter.class);


            case BLEND_HARD_LIGHT:
                return createBlendFilter(context, GPUImageHardLightBlendFilter.class);
            case BLEND_LIGHTEN:
                return createBlendFilter(context, GPUImageLightenBlendFilter.class);
            case BLEND_ADD:
                return createBlendFilter(context, GPUImageAddBlendFilter.class);
            case BLEND_DIVIDE:
                return createBlendFilter(context, GPUImageDivideBlendFilter.class);
            case BLEND_MULTIPLY:
                return createBlendFilter(context, GPUImageMultiplyBlendFilter.class);
            case BLEND_OVERLAY:
                return createBlendFilter(context, GPUImageOverlayBlendFilter.class);
            case BLEND_SCREEN:
                return createBlendFilter(context, GPUImageScreenBlendFilter.class);
            case BLEND_ALPHA:
                return createBlendFilter(context, GPUImageAlphaBlendFilter.class);
            case BLEND_COLOR:
                return createBlendFilter(context, GPUImageColorBlendFilter.class);
            case BLEND_HUE:
                return createBlendFilter(context, GPUImageHueBlendFilter.class);
            case BLEND_SATURATION:
                return createBlendFilter(context, GPUImageSaturationBlendFilter.class);
            case BLEND_LUMINOSITY:
                return createBlendFilter(context, GPUImageLuminosityBlendFilter.class);
            case BLEND_LINEAR_BURN:
                return createBlendFilter(context, GPUImageLinearBurnBlendFilter.class);
            case BLEND_SOFT_LIGHT:
                return createBlendFilter(context, GPUImageSoftLightBlendFilter.class);
            case BLEND_SUBTRACT:
                return createBlendFilter(context, GPUImageSubtractBlendFilter.class);
            case BLEND_CHROMA_KEY:
                return createBlendFilter(context, GPUImageChromaKeyBlendFilter.class);
            case BLEND_NORMAL:
                return createBlendFilter(context, GPUImageNormalBlendFilter.class);

            case LOOKUP_AMATORKA:
                GPUImageLookupFilter amatorka = new GPUImageLookupFilter();
                amatorka.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_amatorka));
                return amatorka;
            case GAUSSIAN_BLUR:
                return new GPUImageGaussianBlurFilter();
            case CROSSHATCH:
                return new GPUImageCrosshatchFilter();
            case BOX_BLUR:
                return new GPUImageBoxBlurFilter();
            case CGA_COLORSPACE:
                return new GPUImageCGAColorspaceFilter();
            case DILATION:
                return new GPUImageDilationFilter();
            case KUWAHARA:
                return new GPUImageKuwaharaFilter();
            case TOON:
                return new GPUImageToonFilter();
            case SMOOTH_TOON:
                return new GPUImageSmoothToonFilter();
            case BULGE_DISTORTION:
                return new GPUImageBulgeDistortionFilter();
            case GLASS_SPHERE:
                return new GPUImageGlassSphereFilter();
            case HAZE:
                return new GPUImageHazeFilter();
            case LAPLACIAN:
                return new GPUImageLaplacianFilter();
            case NON_MAXIMUM_SUPPRESSION:
                return new GPUImageNonMaximumSuppressionFilter();
            case SPHERE_REFRACTION:
                return new GPUImageSphereRefractionFilter();
            case SWIRL:
                return new GPUImageSwirlFilter();
            case WEAK_PIXEL_INCLUSION:
                return new GPUImageWeakPixelInclusionFilter();
            case FALSE_COLOR:
                return new GPUImageFalseColorFilter();
            case COLOR_BALANCE:
                return new GPUImageColorBalanceFilter();
            case LEVELS_FILTER_MIN:
                return new GPUImageLevelsFilter();
            case HALFTONE:
                return new GPUImageHalftoneFilter();
            case BILATERAL_BLUR:
                return new GPUImageBilateralBlurFilter();
            case ZOOM_BLUR:
                return new GPUImageZoomBlurFilter();
            case SOLARIZE:
                return new GPUImageSolarizeFilter();
            case VIBRANCE:
                return new GPUImageVibranceFilter();


            default:
                throw new IllegalStateException("No filter of that type!");
        }

    }

    private static GPUImageFilter createBlendFilter(Context context, Class<? extends GPUImageTwoInputFilter> filterClass) {
        try {
            GPUImageTwoInputFilter filter = filterClass.newInstance();
            filter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<FilterTypeItem> getFilterTypeList(Context context)
    {
        ArrayList<FilterTypeItem> filterTypes = new ArrayList<>();
        filterTypes.add(new FilterTypeItem("Invert", FilterType.INVERT));
        filterTypes.add(new FilterTypeItem("Pixelation", FilterType.PIXELATION));
        filterTypes.add(new FilterTypeItem("Hue", FilterType.HUE));
        filterTypes.add(new FilterTypeItem("Gamma", FilterType.GAMMA));
        filterTypes.add(new FilterTypeItem("Sepia", FilterType.SEPIA));
        filterTypes.add(new FilterTypeItem("Grayscale", FilterType.GRAYSCALE));
        filterTypes.add(new FilterTypeItem("Sobel", FilterType.SOBEL_EDGE_DETECTION));
        filterTypes.add(new FilterTypeItem("Emboss", FilterType.EMBOSS));
        filterTypes.add(new FilterTypeItem("Posterize", FilterType.POSTERIZE));
        filterTypes.add(new FilterTypeItem("Grouped filters", FilterType.FILTER_GROUP));
        filterTypes.add(new FilterTypeItem("Saturation", FilterType.SATURATION));
        filterTypes.add(new FilterTypeItem("Exposure", FilterType.EXPOSURE));
        filterTypes.add(new FilterTypeItem("Highlight Shadow", FilterType.HIGHLIGHT_SHADOW));
        filterTypes.add(new FilterTypeItem("Monochrome", FilterType.MONOCHROME));
        filterTypes.add(new FilterTypeItem("Opacity", FilterType.OPACITY));
        filterTypes.add(new FilterTypeItem("RGB", FilterType.RGB));
        filterTypes.add(new FilterTypeItem("White Balance", FilterType.WHITE_BALANCE));
        filterTypes.add(new FilterTypeItem("Lookup (Amatorka)", FilterType.LOOKUP_AMATORKA));
        filterTypes.add(new FilterTypeItem("Gaussian Blur", FilterType.GAUSSIAN_BLUR));
        filterTypes.add(new FilterTypeItem("Crosshatch", FilterType.CROSSHATCH));
        filterTypes.add(new FilterTypeItem("Box Blur", FilterType.BOX_BLUR));
        filterTypes.add(new FilterTypeItem("CGA Color Space", FilterType.CGA_COLORSPACE));
        filterTypes.add(new FilterTypeItem("Dilation", FilterType.DILATION));
        filterTypes.add(new FilterTypeItem("Kuwahara", FilterType.KUWAHARA));
        filterTypes.add(new FilterTypeItem("Toon", FilterType.TOON));
        filterTypes.add(new FilterTypeItem("Smooth Toon", FilterType.SMOOTH_TOON));
        filterTypes.add(new FilterTypeItem("Halftone", FilterType.HALFTONE));
        filterTypes.add(new FilterTypeItem("Bulge Distortion", FilterType.BULGE_DISTORTION));
        filterTypes.add(new FilterTypeItem("Glass Sphere", FilterType.GLASS_SPHERE));
        filterTypes.add(new FilterTypeItem("Haze", FilterType.HAZE));
        filterTypes.add(new FilterTypeItem("Laplacian", FilterType.LAPLACIAN));
        filterTypes.add(new FilterTypeItem("Swirl", FilterType.SWIRL));
        filterTypes.add(new FilterTypeItem("False Color", FilterType.FALSE_COLOR));
        filterTypes.add(new FilterTypeItem("Color Balance", FilterType.COLOR_BALANCE));
        filterTypes.add(new FilterTypeItem("Levels Min (Mid Adjust)", FilterType.LEVELS_FILTER_MIN));

        return filterTypes;
    }

    public static String getFilterName(FilterType type)
    {
        return "temp";
    }


    public static class FilterTypeItem
    {
        FilterType type;
        String name;

        public FilterTypeItem(final String name, final FilterType filter)
        {
            this.name = name;
            this.type = filter;
        }


        public FilterType getType(){
            return type;
        }

        public void setType(FilterType type){
            this.type = type;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }
    }
}

package com.feed.plugin.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.feed.plugin.R;
import com.feed.plugin.android.gpuimage.filter.GPUImage3x3ConvolutionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageBrightnessFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageColorInvertFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageContrastFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDifferenceBlendFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageDirectionalSobelEdgeDetectionFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageEmbossFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageExposureFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilterGroup;
import com.feed.plugin.android.gpuimage.filter.GPUImageGammaFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageGrayscaleFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageHueFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageLookupFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageMonochromeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageOpacityFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImagePixelationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImagePosterizeFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageRGBFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSaturationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSepiaToneFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSharpenFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageToneCurveFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageVignetteFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageWhiteBalanceFilter;
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
        CONTRAST, BRIGHTNESS, GRAYSCALE, SHARPEN, SEPIA, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, INVERT,
        HUE, PIXELATION, SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, LOOKUP_AMATORKA,
        I_1977, I_AMARO, I_BRANNAN, I_EARLYBIRD, I_HEFE, I_HUDSON, I_INKWELL, I_LOMO, I_LORDKELVIN, I_NASHVILLE, I_RISE, I_SIERRA, I_SUTRO,
        I_TOASTER, I_VALENCIA, I_WALDEN, I_XPROII
    }

//    TONE_CURVE, BLEND_COLOR_BURN, BLEND_COLOR_DODGE, BLEND_DARKEN, BLEND_DIFFERENCE,
//    BLEND_DISSOLVE, BLEND_EXCLUSION, BLEND_SOURCE_OVER, BLEND_HARD_LIGHT, BLEND_LIGHTEN, BLEND_ADD, BLEND_DIVIDE, BLEND_MULTIPLY, BLEND_OVERLAY, BLEND_SCREEN, BLEND_ALPHA,
//    BLEND_COLOR, BLEND_HUE, BLEND_SATURATION, BLEND_LUMINOSITY, BLEND_LINEAR_BURN, BLEND_SOFT_LIGHT, BLEND_SUBTRACT, BLEND_CHROMA_KEY, BLEND_NORMAL,

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
                return new GPUImageBrightnessFilter(1.5f);
            case GRAYSCALE:
                return new GPUImageGrayscaleFilter();
            case SEPIA:
                return new GPUImageSepiaToneFilter();
            case SHARPEN:
                GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
                sharpness.setSharpness(2.0f);
                return sharpness;
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
            case LOOKUP_AMATORKA:
                GPUImageLookupFilter amatorka = new GPUImageLookupFilter();
                amatorka.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_amatorka));
                return amatorka;

            case I_1977:
                return new IF1977Filter(context);
            case I_AMARO:
                return new IFAmaroFilter(context);
            case I_BRANNAN:
                return new IFBrannanFilter(context);
            case I_EARLYBIRD:
                return new IFEarlybirdFilter(context);
            case I_HEFE:
                return new IFHefeFilter(context);
            case I_HUDSON:
                return new IFHudsonFilter(context);
            case I_INKWELL:
                return new IFInkwellFilter(context);
            case I_LOMO:
                return new IFLomoFilter(context);
            case I_LORDKELVIN:
                return new IFLordKelvinFilter(context);
            case I_NASHVILLE:
                return new IFNashvilleFilter(context);
            case I_RISE:
                return new IFRiseFilter(context);
            case I_SIERRA:
                return new IFSierraFilter(context);
            case I_SUTRO:
                return new IFSutroFilter(context);
            case I_TOASTER:
                return new IFToasterFilter(context);
            case I_VALENCIA:
                return new IFValenciaFilter(context);
            case I_WALDEN:
                return new IFWaldenFilter(context);
            case I_XPROII:
                return new IFXprollFilter(context);
//            case TONE_CURVE:
//                GPUImageToneCurveFilter toneCurveFilter = new GPUImageToneCurveFilter();
//                toneCurveFilter.setFromCurveFileInputStream(
//                        context.getResources().openRawResource(R.raw.tone_cuver_sample));
//                return toneCurveFilter;
//            case BLEND_DIFFERENCE:
//                return createBlendFilter(context, GPUImageDifferenceBlendFilter.class);
//            case BLEND_SOURCE_OVER:
//                return createBlendFilter(context, GPUImageSourceOverBlendFilter.class);
//            case BLEND_COLOR_BURN:
//                return createBlendFilter(context, GPUImageColorBurnBlendFilter.class);
//            case BLEND_COLOR_DODGE:
//                return createBlendFilter(context, GPUImageColorDodgeBlendFilter.class);
//            case BLEND_DARKEN:
//                return createBlendFilter(context, GPUImageDarkenBlendFilter.class);
//            case BLEND_DISSOLVE:
//                return createBlendFilter(context, GPUImageDissolveBlendFilter.class);
//            case BLEND_EXCLUSION:
//                return createBlendFilter(context, GPUImageExclusionBlendFilter.class);
//            case BLEND_HARD_LIGHT:
//                return createBlendFilter(context, GPUImageHardLightBlendFilter.class);
//            case BLEND_LIGHTEN:
//                return createBlendFilter(context, GPUImageLightenBlendFilter.class);
//            case BLEND_ADD:
//                return createBlendFilter(context, GPUImageAddBlendFilter.class);
//            case BLEND_DIVIDE:
//                return createBlendFilter(context, GPUImageDivideBlendFilter.class);
//            case BLEND_MULTIPLY:
//                return createBlendFilter(context, GPUImageMultiplyBlendFilter.class);
//            case BLEND_OVERLAY:
//                return createBlendFilter(context, GPUImageOverlayBlendFilter.class);
//            case BLEND_SCREEN:
//                return createBlendFilter(context, GPUImageScreenBlendFilter.class);
//            case BLEND_ALPHA:
//                return createBlendFilter(context, GPUImageAlphaBlendFilter.class);
//            case BLEND_COLOR:
//                return createBlendFilter(context, GPUImageColorBlendFilter.class);
//            case BLEND_HUE:
//                return createBlendFilter(context, GPUImageHueBlendFilter.class);
//            case BLEND_SATURATION:
//                return createBlendFilter(context, GPUImageSaturationBlendFilter.class);
//            case BLEND_LUMINOSITY:
//                return createBlendFilter(context, GPUImageLuminosityBlendFilter.class);
//            case BLEND_LINEAR_BURN:
//                return createBlendFilter(context, GPUImageLinearBurnBlendFilter.class);
//            case BLEND_SOFT_LIGHT:
//                return createBlendFilter(context, GPUImageSoftLightBlendFilter.class);
//            case BLEND_SUBTRACT:
//                return createBlendFilter(context, GPUImageSubtractBlendFilter.class);
//            case BLEND_CHROMA_KEY:
//                return createBlendFilter(context, GPUImageChromaKeyBlendFilter.class);
//            case BLEND_NORMAL:
//                return createBlendFilter(context, GPUImageNormalBlendFilter.class);

            default:
                throw new IllegalStateException("No filter of that type!");
        }

    }

    public static ArrayList<FilterType> getFilterTypeList()
    {
        ArrayList<FilterType> filterTypes = new ArrayList<>();
        filterTypes.add(FilterType.CONTRAST);
        filterTypes.add(FilterType.GRAYSCALE);
        filterTypes.add(FilterType.SHARPEN);
        filterTypes.add(FilterType.SEPIA);
        filterTypes.add(FilterType.THREE_X_THREE_CONVOLUTION);
        filterTypes.add(FilterType.FILTER_GROUP);
        filterTypes.add(FilterType.EMBOSS);
        filterTypes.add(FilterType.POSTERIZE);
        filterTypes.add(FilterType.GAMMA);
        filterTypes.add(FilterType.INVERT);

        return filterTypes;
    }

    public static String getFilterName(FilterType type)
    {
        return "temp";
    }
}

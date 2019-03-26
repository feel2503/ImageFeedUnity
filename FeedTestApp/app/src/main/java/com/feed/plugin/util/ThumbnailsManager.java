package com.feed.plugin.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.feed.plugin.R;
import com.feed.plugin.adapter.items.ThumbnailItem;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailsManager{
    private static List<ThumbnailItem> filterThumbs = new ArrayList<ThumbnailItem>(10);
    private static List<ThumbnailItem> processedThumbs = new ArrayList<ThumbnailItem>(10);

    private ThumbnailsManager() {
    }

    public static void addThumb(ThumbnailItem thumbnailItem) {
        filterThumbs.add(thumbnailItem);
    }

    public static List<ThumbnailItem> processThumbs(Context context) {
        try{
            for (ThumbnailItem thumb : filterThumbs) {

                // scaling down the image
                float size = context.getResources().getDimension(R.dimen.thumbnail_size);
                thumb.image = Bitmap.createScaledBitmap(thumb.image, (int) size, (int) size, false);

                //thumb.gpuImageView.setImage(thumb.image);
                //thumb.gpuImageView.requestRender();
                //thumb.image =thumb.gpuImageView.capture((int) size, (int) size);
                //thumb.image = thumb.filter.processFilter(thumb.image);
                // cropping circle

                // TODO - think about circular thumbnails
                // thumb.image = GeneralUtils.generateCircularBitmap(thumb.image);
                processedThumbs.add(thumb);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return processedThumbs;
    }

    public static void clearThumbs() {
        filterThumbs = new ArrayList<>();
        processedThumbs = new ArrayList<>();
    }
}

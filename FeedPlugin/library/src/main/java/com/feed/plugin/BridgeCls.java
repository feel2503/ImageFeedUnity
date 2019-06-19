package com.feed.plugin;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BridgeCls{
    private static BridgeCls mInstance;
    private Context mContext;
    public static String EXTRA_EDITIMG_PATH = "android.feed.EXTRA_EDITIMG_PATH";
    public static String EXTRA_FEEDIMG_PATH = "android.feed.EXTRA_FEEDIMG_PATH";

    public static BridgeCls instance()
    {
        if(mInstance == null)
            mInstance = new BridgeCls();

        return mInstance;
    }

    private void setContext(Context context)
    {
        mContext = context;
    }

    private void showToast(String msg)
    {
        Toast.makeText(this.mContext, msg, Toast.LENGTH_LONG).show();

    }
    public void startGalleryActivity(Context context)
    {
        Intent intent = new Intent(context, ImgSelectActivity.class);
        context.startActivity(intent);
    }

    public void startEditActivity(Context context, String imgUrl)
    {
        Intent intent = new Intent(context, ImgFilterActivity.class);
        intent.putExtra(EXTRA_EDITIMG_PATH, imgUrl);
        context.startActivity(intent);
    }

    public void startFeedActivity(Context context, String imgUrl)
    {
        Intent intent = new Intent(context, FeedUploadActivity.class);
        intent.putExtra(EXTRA_FEEDIMG_PATH, imgUrl);
        context.startActivity(intent);
    }

    public void startFacePhotoActivity(Context context)
    {
        Intent intent = new Intent(context, FacePhotoActivity.class);
        context.startActivity(intent);
    }

    public void startProfileActivity(Context context)
    {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
    }

}

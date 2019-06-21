package com.feed.plugin;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

public class BridgeCls{
    private static BridgeCls mInstance;
    private Context mContext;
    public static String EXTRA_ACTIVITY_MODE = "android.feed.EXTRA_ACTIVITY_MODE";

    public static String EXTRA_EDITIMG_LIST = "android.feed.EXTRA_EDITIMG_LIST";
    public static String EXTRA_FEEDIMG_PATH = "android.feed.EXTRA_FEEDIMG_PATH";


    public static String ACTIVITY_MODE_DEFAULT = "Action_Default";
    public static String ACTIVITY_MODE_PROFILE = "Action_Profile";
    public static String ACTIVITY_MODE_FILTER = "Action_Filter";
    public static String ACTIVITY_MODE_FEED = "Action_Feed";

    public static String mStrFacePath;
    public static String mStrProfilePath;
    public static String mStrFilterPath;

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
        ArrayList<String> imgList;
        if(imgUrl == null)
        {
            return;
        }
        else
        {
            imgList = new ArrayList<>();
            imgList.add(imgUrl);
        }


        Intent intent = new Intent(context, ImgFilterActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_MODE, ACTIVITY_MODE_FILTER);
        intent.putStringArrayListExtra(EXTRA_EDITIMG_LIST, imgList);
        context.startActivity(intent);
    }

    public void startFeedActivity(Context context, String imgUrl)
    {
        ArrayList<String> imgList;
        if(imgUrl == null)
        {
            return;
        }
        else
        {
            imgList = new ArrayList<>();
            imgList.add(imgUrl);
        }

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

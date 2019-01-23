package com.feed.plugin;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BridgeCls {
    private static BridgeCls mInstance;
    private Context mContext;

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
}

package com.feed.plugin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.feed.plugin.widget.hashtag.HashTagSuggestAdapter;
import com.feed.plugin.widget.hashtag.HashTagSuggestWithAPIAdapter;
import com.feed.plugin.widget.hashtag.HashTagTextView;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FeedUploadActivity extends AppCompatActivity {

    private ImageView mImgView;
    private ArrayList<String> mImagList;

    private  HashTagTextView textView;

    protected ProgressDialog mProgress = null;

    private static final String[] COUNTRIES = new String[]{
            "#Belgium", "#France", "#Italy", "#Germany", "#Spain"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_upload);

        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));

        mProgress = new ProgressDialog(this);
        mImagList = getIntent().getStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST);

        mImgView = (ImageView)findViewById(R.id.img_feedImglist);

        if(mImagList != null && mImagList.size() > 0)
        {
            String imgPath = mImagList.get(0);
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            mImgView.setImageBitmap(bitmap);
        }

        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);
        findViewById(R.id.text_share).setOnClickListener(mOnClickListener);
        mImgView.setOnClickListener(mOnClickListener);

        // hashtag
        textView = (HashTagTextView) findViewById(R.id.input_form);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "RecklessTRIAL-Regular.otf"));

//        HashTagSuggestAdapter adapter = new HashTagSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
//        adapter.setCursorPositionListener(new HashTagSuggestAdapter.CursorPositionListener() {
//            @Override
//            public int currentCursorPosition() {
//                return textView.getSelectionStart();
//            }
//        });

        List<String> object = new ArrayList<>();
        HashTagSuggestWithAPIAdapter adapter = new HashTagSuggestWithAPIAdapter(this, R.layout.hashtag_suggest_cell);
        adapter.setCursorPositionListener(new HashTagSuggestWithAPIAdapter.CursorPositionListener() {
            @Override
            public int currentCursorPosition() {
                return textView.getSelectionStart();
            }
        });


        textView.setAdapter(adapter);

    }

    private boolean saveToInternalStorage(Bitmap bitmapImage, String path)
    {
        File mypath = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void feedRequest(String disCovery)
    {
        // send message
        String message = textView.getText().toString();
        UnityPlayer.UnitySendMessage("FeedModule","SetMessage",message);

        String imgResult = mImagList.get(0);
        for(int i = 1; i < mImagList.size(); i++)
        {
            imgResult = imgResult + "|" + mImagList.get(i);
        }
        UnityPlayer.UnitySendMessage("FeedModule","AddImagePath",imgResult);

        UnityPlayer.UnitySendMessage("FeedModule","AddDiscoveryImagePath", disCovery);

        UnityPlayer.UnitySendMessage("FeedModule","Feed",imgResult);


        showProgress(FeedUploadActivity.this, false);

        setResult(RESULT_OK);
        finish();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.text_share)
            {
                showProgress(FeedUploadActivity.this, true);

                AsyncDiscovery async = new AsyncDiscovery();
                async.execute(mImagList.get(0));
            }
            else if(v.getId() == R.id.btn_back)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
            else if(v.getId() == R.id.img_feedImglist)
            {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ImageViewActivity.class);
                intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, mImagList);
                startActivity(intent);
            }
        }
    };

    private class AsyncDiscovery extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params){
            String path = params[0];
            boolean needCrop = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int x = 0;
            int y = 0;

            if(width == height)
            {
                needCrop = false;
            }
            else if(width > height)
            {
                x = (width - height)/2;
                y = 0;
                width = height;

                needCrop = true;
            }
            else if(width < height)
            {
                y = (height - width)/2;
                x = 0;
                height = width;

                needCrop = true;
            }

            if(needCrop)
                bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);

            int idx = path.lastIndexOf("/");
            String savePath = path.substring(0, idx+1) + "filter_discovery.jpg";

            saveToInternalStorage(bitmap, savePath);
            return savePath;
        }

        @Override
        protected void onPostExecute(String disPath){
            super.onPostExecute(disPath);

            feedRequest(disPath);
        }
    }


    public void showProgress(final Activity act, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgress.setMessage(getString(R.string.saving));
                try
                {
                    if (bShow)
                    {
                        mProgress.show();
                    }
                    else
                    {
                        mProgress.dismiss();
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
    }
}

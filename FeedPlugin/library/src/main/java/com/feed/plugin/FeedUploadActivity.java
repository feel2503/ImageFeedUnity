package com.feed.plugin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.feed.plugin.widget.hashtag.HashTagSuggestAdapter;
import com.feed.plugin.widget.hashtag.HashTagTextView;

import java.util.ArrayList;

public class FeedUploadActivity extends AppCompatActivity {

    private ImageView mImgView;
    private ArrayList<String> mImagList;

    private static final String[] COUNTRIES = new String[]{
            "#Belgium", "#France", "#Italy", "#Germany", "#Spain"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_upload);

        mImagList = getIntent().getStringArrayListExtra("ImageList");

        mImgView = (ImageView)findViewById(R.id.img_feedImglist);

        if(mImagList != null && mImagList.size() > 0)
        {
            String imgPath = mImagList.get(0);
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            mImgView.setImageBitmap(bitmap);
        }

        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);
        mImgView.setOnClickListener(mOnClickListener);

        // hashtag
        final HashTagTextView textView = (HashTagTextView) findViewById(R.id.input_form);

        HashTagSuggestAdapter adapter = new HashTagSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        adapter.setCursorPositionListener(new HashTagSuggestAdapter.CursorPositionListener() {
            @Override
            public int currentCursorPosition() {
                return textView.getSelectionStart();
            }
        });

        textView.setAdapter(adapter);

    }



    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_next)
            {

            }
            else if(v.getId() == R.id.btn_back)
            {
                finish();
            }
            else if(v.getId() == R.id.img_feedImglist)
            {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ImageViewActivity.class);
                intent.putStringArrayListExtra("ImageList", mImagList);
                startActivity(intent);
            }
        }
    };
}

package com.feed.plugin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ImgEditActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private BottomNavigationView mBtnavigation;
    private ImageView mImgView;

    private ArrayList<String> mImagList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_edit);

        mImagList = getIntent().getStringArrayListExtra("ImageList");

        mTextMessage = (TextView)findViewById(R.id.text_editmenu);

        mBtnavigation = (BottomNavigationView) findViewById(R.id.btnavigation);
        mBtnavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mImgView = (ImageView)findViewById(R.id.img_editimg);

        String imgPath = mImagList.get(0);
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        mImgView.setImageBitmap(bitmap);

        findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_filter:
                    mTextMessage.setText(R.string.filter);
                    return true;
                case R.id.navigation_edit:
                    mTextMessage.setText(R.string.edit);
                    return true;
            }
            return false;
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_next)
            {

                if(mImagList == null || mImagList.size() < 1)
                {
                    Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                intent.putStringArrayListExtra("ImageList", mImagList);
                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_back)
            {
                finish();
            }
        }
    };
}

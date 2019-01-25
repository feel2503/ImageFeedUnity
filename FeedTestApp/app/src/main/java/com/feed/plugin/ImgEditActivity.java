package com.feed.plugin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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
}

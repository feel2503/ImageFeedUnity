package com.feed.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.unity3d.player.UnityPlayerActivity;

public class GalleryActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        findViewById(R.id.btn_next).setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_next)
            {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ImgEditActivity.class);
                startActivity(intent);
            }
        }
    };
}

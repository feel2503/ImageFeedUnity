package com.ext.phill.feedtestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.feed.plugin.BridgeCls;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text_hello).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                BridgeCls inst = BridgeCls.instance();
                inst.startGalleryActivity(getApplicationContext());

            }
        });
    }
}

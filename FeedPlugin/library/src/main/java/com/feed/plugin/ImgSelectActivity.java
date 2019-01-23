package com.feed.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.feed.plugin.adapter.TabPagerAdapter;
import com.unity3d.player.UnityPlayerActivity;

//public class ImgSelectActivity extends UnityPlayerActivity{
public class ImgSelectActivity extends AppCompatActivity{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgselect);

        //findViewById(R.id.btn_next).setOnClickListener(mClickListener);
    }

    private void initTab()
    {
        mTabLayout = (TabLayout)findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.gallelry));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.stylebook));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.camera));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = findViewById(R.id.viewPager);

        //Creating adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        //TabPagerAdapter pagerAdapter = new TabPagerAdapter(getFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //Set TabSelectedListener
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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

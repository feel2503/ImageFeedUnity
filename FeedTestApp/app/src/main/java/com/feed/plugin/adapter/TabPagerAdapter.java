package com.feed.plugin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.feed.plugin.fragment.CameraFragment;
import com.feed.plugin.fragment.GalleryFragment;
import com.feed.plugin.fragment.StyleBookFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter{
    private int mTabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        mTabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                GalleryFragment mainTabFragment1 = new GalleryFragment();
                return mainTabFragment1;
            case 1:
                StyleBookFragment mainTabFragment2 = new StyleBookFragment();
                return mainTabFragment2;
            case 2:
                CameraFragment mainTabFragment3 = new CameraFragment();
                return mainTabFragment3;

            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return mTabCount;
    }
}

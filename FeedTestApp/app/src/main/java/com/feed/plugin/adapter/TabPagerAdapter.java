package com.feed.plugin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.feed.plugin.fragment.CameraFragment;
import com.feed.plugin.fragment.GalleryFragment;
import com.feed.plugin.fragment.ImgSelFragment;
import com.feed.plugin.fragment.StyleBookFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter{
    private int mTabCount;

    private GalleryFragment mGalleryFragment;
    private StyleBookFragment mStyleBookFragment;
    private CameraFragment mCameraFragment;


    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        mTabCount = tabCount;
    }

    @Override
    public ImgSelFragment getItem(int position){
        switch (position){
            case 0:
                return GalleryFragment.getInstance();
            case 1:
                CameraFragment mainTabFragment3 = new CameraFragment();
                return mainTabFragment3;


//            case 1:
//                return StyleBookFragment.getInstance();
//            case 2:
//                CameraFragment mainTabFragment3 = new CameraFragment();
//                return mainTabFragment3;

            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return mTabCount;
    }
}

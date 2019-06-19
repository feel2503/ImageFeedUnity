package com.feed.plugin.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.feed.plugin.fragment.CameraFragment;
import com.feed.plugin.fragment.CameraViewFragment;
import com.feed.plugin.fragment.GalleryFragment;
import com.feed.plugin.fragment.ImgSelFragment;
import com.feed.plugin.fragment.ProfileCookiGalleryFragment;
import com.feed.plugin.fragment.ProfileGalleryFragment;
import com.feed.plugin.fragment.StyleBookFragment;

public class ProfilePagerAdapter extends FragmentStatePagerAdapter{
    private int mTabCount;

    private GalleryFragment mGalleryFragment;
    private StyleBookFragment mStyleBookFragment;
    private CameraFragment mCameraFragment;



    public ProfilePagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        mTabCount = tabCount;
    }

    @Override
    public ImgSelFragment getItem(int position){
        switch (position){
            case 0:
                //return ProfileGalleryFragment.getInstance();
                return ProfileCookiGalleryFragment.getInstance();
            case 1:
                CameraViewFragment mainTabFragment3 = new CameraViewFragment();
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

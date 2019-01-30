package com.feed.plugin.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feed.plugin.R;

public class StyleBookFragment extends ImgSelFragment{

    private static StyleBookFragment instance;

    public static StyleBookFragment getInstance()
    {
        if(instance == null)
            instance = new StyleBookFragment();

        return instance;
    }

    public StyleBookFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_stylebook, container, false);
    }
}

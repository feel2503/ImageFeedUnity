package com.feed.plugin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.feed.plugin.R;
import com.feed.plugin.widget.camera.CameraPreview;

public class CameraFragment extends ImgSelFragment  {

    private TextureView mCameraTextureView;
    private CameraPreview mPreview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView =  inflater.inflate(R.layout.fragment_camera, container, false);

        mCameraTextureView = (TextureView) rootView.findViewById(R.id.texture_camera_preview);
        mPreview = new CameraPreview(getActivity(), mCameraTextureView);

        rootView.findViewById(R.id.button_main_capture).setOnClickListener(mOnClickListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreview.onResume();
    }

    @Override
    public void onPause() {
        mPreview.onPause();
        super.onPause();
    }

    private void takePicture()
    {
        mPreview.takePicture();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.button_main_capture)
            {
                takePicture();
            }
        }
    };
}

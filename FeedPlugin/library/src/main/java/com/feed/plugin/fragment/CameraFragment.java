package com.feed.plugin.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.feed.plugin.R;
import com.feed.plugin.widget.camera.CameraPreview;

public class CameraFragment extends ImgSelFragment  {

    private final String CAMERA_FRONT = "0";
    private final String CAMERA_BACK = "1";

    private TextureView mCameraTextureView;
    private CameraPreview mPreview;
    private ToggleButton mToggleFlash;
    private Button mBtnCameraRotate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView =  inflater.inflate(R.layout.fragment_camera, container, false);

        mCameraTextureView = (TextureView) rootView.findViewById(R.id.texture_camera_preview);
        mPreview = new CameraPreview(getActivity(), mCameraTextureView);

        mToggleFlash = rootView.findViewById(R.id.btn_camera_flash_mode);
        mToggleFlash.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mBtnCameraRotate = rootView.findViewById(R.id.btn_camera_rotate);
        mBtnCameraRotate.setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.button_main_capture).setOnClickListener(mOnClickListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreview.onResume();
        mPreview.openCamera();
    }

    @Override
    public void onPause() {
        mPreview.onPause();
        super.onPause();
    }

    private void takePicture()
    {
        Activity activity = getActivity();
        mPreview.takePicture(activity);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.button_main_capture)
            {
                takePicture();
            }
            else if(v.getId() == R.id.btn_camera_rotate)
            {
                mPreview.onPause();
                if(mPreview.getmCameraID().equalsIgnoreCase(CAMERA_FRONT))
                    mPreview.openCamera(CAMERA_BACK);
                else
                    mPreview.openCamera(CAMERA_FRONT);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton v, boolean isChecked){
            if(v.getId() == R.id.btn_camera_flash_mode)
            {
                if(isChecked)
                {
                    mPreview.flashMode(true);
                }
                else
                {
                    mPreview.flashMode(false);
                }
            }
        }
    };
}

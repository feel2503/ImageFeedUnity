package com.feed.plugin.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.feed.plugin.ImgSelectActivity;
import com.feed.plugin.ProfileActivity;
import com.feed.plugin.R;
import com.feed.plugin.widget.camera.CameraView;
import com.feed.plugin.widget.cropimgview.util.CropUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class CameraViewFragment extends ImgSelFragment
{
    private static final String TAG = "CameraViewFragment";

    private Handler mBackgroundHandler;

    private CameraView mCameraView;

    private ToggleButton mToggleFlash;
    private Button mBtnCameraRotate;
    private Button mBtnCapture;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView =  inflater.inflate(R.layout.fragment_camera_view, container, false);

        mCameraView = (CameraView) rootView.findViewById(R.id.camera_preview);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }

        mToggleFlash = rootView.findViewById(R.id.btn_camera_flash_mode);
        mToggleFlash.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mBtnCameraRotate = rootView.findViewById(R.id.btn_camera_rotate);
        mBtnCameraRotate.setOnClickListener(mOnClickListener);

        mBtnCapture = rootView.findViewById(R.id.button_main_capture);
        mBtnCapture.setOnClickListener(mOnClickListener);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mCameraView != null)
            mCameraView.start();

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            mCameraView.start();
//        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.CAMERA)) {
//            ConfirmationDialogFragment
//                    .newInstance(R.string.camera_permission_confirmation,
//                            new String[]{Manifest.permission.CAMERA},
//                            REQUEST_CAMERA_PERMISSION,
//                            R.string.camera_permission_not_granted)
//                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA_PERMISSION);
//        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //mCameraView.stop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mCameraView.stop();

        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.button_main_capture)
            {
                if (mCameraView != null) {
                    mCameraView.takePicture();
                }
            }
            else if(v.getId() == R.id.btn_camera_rotate)
            {
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
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
                    if (mCameraView != null) {
                        mCameraView.setFlash(CameraView.FLASH_ON);
                    }
                }
                else
                {
                    if (mCameraView != null) {
                        mCameraView.setFlash(CameraView.FLASH_OFF);
                    }
                }
            }
        }
    };


    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);
            Toast.makeText(cameraView.getContext(), "onPictureTaken", Toast.LENGTH_SHORT).show();
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    // This demo app saves the taken picture to a constant file.
                    //File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"picture.jpg");
                    String filePath = CropUtils.getDirPath() + "/"+"picture.jpg";
                    final File file = new File(filePath);

                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }


                    ArrayList<String> arrImg = new ArrayList<String>();
                    arrImg.add(filePath);

                    //((ImgSelectActivity)getActivity()).startResultActivity(arrImg);
                    if(getActivity() instanceof ImgSelectActivity)
                    {
                        ((ImgSelectActivity)getActivity()).startResultActivity(arrImg);
                    }
                    else if(getActivity() instanceof ProfileActivity)
                    {
                        ((ProfileActivity)getActivity()).startResultActivity(arrImg);
                    }

                }
            });
        }

    };
}

package com.feed.plugin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.feed.plugin.widget.camera.CameraView;
import com.feed.plugin.widget.cropimgview.util.CropUtils;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProfilePhotoActivity extends AppCompatActivity{
    private int REQUEST_IMAGE_FILTER = 0x1002;
    private Handler mBackgroundHandler;
    private CameraView mCameraView;

    private ToggleButton mToggleFlash;
    private Button mBtnCameraRotate;
    private Button mBtnCapture;


    private int REQUEST_GALLERY_SELECT = 0x1011;

    private final int REQEUST_PERFMSSION_CODE = 0x1001;
    private int REQUEST_SETTING_FOR_PERMISSION = 0x1003;
    private String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA, // 카메라
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};  // 외부 저장소


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile_photo);

        boolean isPermission = checkVerify();
        if(isPermission)
        {
            init();
        }
    }

    private void init()
    {
        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));
        textTitle.setText(getString(R.string.camera));

        mCameraView = (CameraView) findViewById(R.id.camera_preview);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }

        mToggleFlash = findViewById(R.id.btn_camera_flash_mode);
        mToggleFlash.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mBtnCameraRotate = findViewById(R.id.btn_camera_rotate);
        mBtnCameraRotate.setOnClickListener(mOnClickListener);

        mBtnCapture = findViewById(R.id.button_main_capture);
        mBtnCapture.setOnClickListener(mOnClickListener);


        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);
    }

    public boolean checkVerify()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
            {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    // ...
                }
                requestPermissions(REQUIRED_PERMISSIONS,  REQEUST_PERFMSSION_CODE);
                return false;
            }
            else
            {
                return true;
            }
        }
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(mCameraView != null)
            mCameraView.start();

    }

    @Override
    public void onPause(){
        super.onPause();
        if(mCameraView != null)
            mCameraView.stop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        UnityPlayer.UnitySendMessage("FeedModule", "SetFacePhotoPath", "BACK");
    }

    public void showRequestAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePhotoActivity.this);
        builder.setMessage(getString(R.string.requestpermission));
        builder.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_SETTING_FOR_PERMISSION);
                    //startActivity(intent);
                }catch (ActivityNotFoundException ae)
                {
                    ae.printStackTrace();

                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQEUST_PERFMSSION_CODE && grantResults.length == REQUIRED_PERMISSIONS.length)
        {
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {
                init();
            }
            else {
                showRequestAgainDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GALLERY_SELECT)
        {
            if(resultCode == RESULT_OK)
            {
                finish();
            }
        }
        else if(requestCode == REQUEST_IMAGE_FILTER)
        {
            if(resultCode == RESULT_OK)
            {
                finish();
            }
        }
        else if(requestCode == REQUEST_SETTING_FOR_PERMISSION)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )
                {
                    init();
                }
                else
                {
                    finish();
                }
            }
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
            else if(v.getId() == R.id.btn_back)
            {
                UnityPlayer.UnitySendMessage("FeedModule", "SetFacePhotoPath", "BACK");
                finish();
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

        }

        @Override
        public void onCameraClosed(CameraView cameraView) {

        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            //Toast.makeText(cameraView.getContext(), "onPictureTaken", Toast.LENGTH_SHORT).show();
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    // This demo app saves the taken picture to a constant file.
                    //File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"picture.jpg");
                    String filePath = CropUtils.getDirPath(getApplicationContext()) + "/"+"profile_picture.jpg";
                    final File file = new File(filePath);
                    if(file.exists())
                        file.delete();

                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
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

                    Intent intent = new Intent();
                    //intent.setClass(getApplicationContext(), ImgEditActivity.class);
                    intent.setClass(getApplicationContext(), ImgFilterActivity.class);
                    intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, arrImg);
                    intent.putExtra(BridgeCls.EXTRA_ACTIVITY_MODE, BridgeCls.ACTIVITY_MODE_PROFILE);
                    startActivityForResult(intent, REQUEST_IMAGE_FILTER);
                }
            });
        }

    };
}

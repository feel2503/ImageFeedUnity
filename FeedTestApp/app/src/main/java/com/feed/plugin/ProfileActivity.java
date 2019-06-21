package com.feed.plugin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.feed.plugin.adapter.ProfilePagerAdapter;
import com.feed.plugin.adapter.TabPagerAdapter;
import com.feed.plugin.widget.SwipeViewPager;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity{
    private int REQEUST_PERFMSSION_CODE = 0x1001;
    private int REQUEST_IMAGE_FILTER = 0x1002;

    private String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA, // 카메라
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};  // 외부 저장소

    private TabLayout mTabLayout;
    private ProfilePagerAdapter mTabpagerAdapter;
    private SwipeViewPager mViewPager;
    private TextView mTextTitle;
    protected ProgressDialog mProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);

        mProgress = new ProgressDialog(this);

        boolean isPermission = checkVerify();
        if(isPermission)
        {
            initTab();
        }

        findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);

        mTextTitle = (TextView)findViewById(R.id.text_title);
    }

    private void initTab()
    {
        mTabLayout = (TabLayout)findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.gallelry));
//        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.stylebook));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.camera));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (SwipeViewPager)findViewById(R.id.viewPager);
        setPagingEnabled(false);
        //Creating adapter
        mTabpagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        //TabPagerAdapter pagerAdapter = new TabPagerAdapter(getFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(mTabpagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //Set TabSelectedListener
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
                if(position == 0)
                {
                    mTextTitle.setText(R.string.gallelry);
                }
//                else if(position == 1)
//                {
//                    mTextTitle.setText(R.string.stylebook);
//                }
                else
                {
                    mTextTitle.setText(R.string.camera);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_FILTER)
        {
            if(resultCode == RESULT_OK)
            {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setPagingEnabled(boolean enabled)
    {
        mViewPager.setPagingEnabled(enabled);
    }

    public void startResultActivity(ArrayList<String> imgList) {
        Intent intent = new Intent();
        //intent.setClass(getApplicationContext(), ImgEditActivity.class);
        intent.setClass(getApplicationContext(), ImgFilterActivity.class);
        intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, imgList);
        intent.putExtra(BridgeCls.EXTRA_ACTIVITY_MODE, BridgeCls.ACTIVITY_MODE_PROFILE);
        startActivityForResult(intent, REQUEST_IMAGE_FILTER);
        imgList.clear();
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
                initTab();
            }
            else {
                showRequestAgainDialog();
            }
        }
    }

    public void showRequestAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage(getString(R.string.requestpermission));
        builder.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
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


    public void showProgress(final Activity act, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgress.setMessage(getString(R.string.saving));
                try
                {
                    if (bShow)
                    {
                        mProgress.show();
                    }
                    else
                    {
                        mProgress.dismiss();
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_next)
            {
                mTabpagerAdapter.getItem(mViewPager.getCurrentItem());
                mTabpagerAdapter.getItem(mViewPager.getCurrentItem()).getCropImgList();

//                ArrayList<String> imgList = mTabpagerAdapter.getItem(mViewPager.getCurrentItem()).getCropImgList();
//
//                if(imgList == null || imgList.size() < 1)
//                {
//                    Toast.makeText(getApplicationContext(), "이미지를 선택해 주세요.", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                Intent intent = new Intent();
//                intent.setClass(getApplicationContext(), ImgEditActivity.class);
//                intent.putStringArrayListExtra("ImageList", imgList);
//                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_back)
            {
                finish();
            }
        }
    };
}

package com.feed.plugin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.feed.plugin.adapter.TabPagerAdapter;

import java.util.ArrayList;

public class ImgSelectActivity extends AppCompatActivity{

    private final int REQEUST_PERFMSSION_CODE = 0x1001;

    private String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA, // 카메라
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};  // 외부 저장소

    private TabLayout mTabLayout;
    private TabPagerAdapter mTabpagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgselect);

        boolean isPermission = checkVerify();
        if(isPermission)
        {
            initTab();
        }

        findViewById(R.id.btn_next).setOnClickListener(mClickListener);
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
        mTabpagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        //TabPagerAdapter pagerAdapter = new TabPagerAdapter(getFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(mTabpagerAdapter);
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
                mTabpagerAdapter.getItem(mViewPager.getCurrentItem());
                ArrayList<String> imgList = mTabpagerAdapter.getItem(mViewPager.getCurrentItem()).getImgList();

                if(imgList == null || imgList.size() < 1)
                {
                    Toast.makeText(getApplicationContext(), "이미지를 선택해 주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ImgEditActivity.class);
                intent.putStringArrayListExtra("ImageList", imgList);
                startActivity(intent);
            }
        }
    };


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
        AlertDialog.Builder builder = new AlertDialog.Builder(ImgSelectActivity.this);
        builder.setMessage("이 권한은 꼭 필요한 권한이므로, 설정에서 활성화 부탁드립니다.");
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create().show();
    }
}

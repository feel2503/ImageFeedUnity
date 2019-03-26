package com.feed.plugin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.fragment.EditImageFragment;
import com.feed.plugin.fragment.FiltersListFragment;
import com.feed.plugin.widget.SwipeViewPager;

import java.util.ArrayList;
import java.util.List;

public class ImgEditActivity extends AppCompatActivity {

    private GPUImageView mImgView;

    private ArrayList<String> mImagList;

    private SwipeViewPager mViewPager;
    private FiltersListFragment filtersListFragment;
    private EditImageFragment editImageFragment;

    private TabLayout mTabLayout;

    private Bitmap originalImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_img_edit);

        mImagList = getIntent().getStringArrayListExtra("ImageList");
        mImgView = (GPUImageView)findViewById(R.id.gpuimage);

        loadImage();

        findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);

        mViewPager = (SwipeViewPager)findViewById(R.id.edit_viewpager);
        mViewPager.setPagingEnabled(false);

        mTabLayout = (TabLayout)findViewById(R.id.edit_tabs);

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setImageList(mImagList);
        filtersListFragment.setListener(mFilterListListener);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(mEditImageListenr);

        adapter.addFragment(filtersListFragment, "Filters");
        adapter.addFragment(editImageFragment, "Edit");

        viewPager.setAdapter(adapter);
    }

    private void loadImage()
    {
        String imgPath = mImagList.get(0);
        originalImage = BitmapFactory.decodeFile(imgPath);
        mImgView.setImage(originalImage);
    }

    private void createFilterList()
    {

    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_next)
            {

                if(mImagList == null || mImagList.size() < 1)
                {
                    Toast.makeText(getApplicationContext(), "이미지를 선택해 주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                intent.putStringArrayListExtra("ImageList", mImagList);
                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_back)
            {
                finish();
            }
        }
    };

    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private FiltersListFragment.FiltersListFragmentListener mFilterListListener = new FiltersListFragment.FiltersListFragmentListener()
    {

        @Override
        public void onFilterSelected(GPUImageFilter filter){

//            // applying the selected filter
//            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
//            // preview filtered image
//            mImgView.setImageBitmap(filter.processFilter(filteredImage));
//
//            finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
        }
    };

    private EditImageFragment.EditImageFragmentListener mEditImageListenr = new EditImageFragment.EditImageFragmentListener()
    {

        @Override
        public void onBrightnessChanged(int brightness){
//            brightnessFinal = brightness;
//            Filter myFilter = new Filter();
//            myFilter.addSubFilter(new BrightnessSubFilter(brightness));
//            mImgView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        }

        @Override
        public void onSaturationChanged(float saturation){
//            saturationFinal = saturation;
//            Filter myFilter = new Filter();
//            myFilter.addSubFilter(new SaturationSubfilter(saturation));
//            mImgView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        }

        @Override
        public void onContrastChanged(float contrast){
//            contrastFinal = contrast;
//            Filter myFilter = new Filter();
//            myFilter.addSubFilter(new ContrastSubFilter(contrast));
//            mImgView.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
        }

        @Override
        public void onColorOverlayChanged(){

        }

        @Override
        public void onToneCurveChanged(){

        }

        @Override
        public void onVignetteChanged(int alpha){

        }

        @Override
        public void onEditStarted(){

        }

        @Override
        public void onEditCompleted(){

        }
    };
}

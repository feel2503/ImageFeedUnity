package com.feed.plugin;

import android.app.Activity;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feed.plugin.adapter.GpuimageSlideAdapter;
import com.feed.plugin.adapter.items.GPUImgItem;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.fragment.EditImageFragment;
import com.feed.plugin.fragment.FiltersListFragment;
import com.feed.plugin.fragment.FiltersListSelectListener;
import com.feed.plugin.widget.SwipeViewPager;
import com.feed.plugin.widget.thumbseekbar.ThumbTextSeekBar;

import java.util.ArrayList;
import java.util.List;

public class ImgEditActivity extends AppCompatActivity {

    private int REQUEST_NOTICE = 0x1001;
    //private GPUImageView mImgView;
    private ViewPager mGPUImagePager;
    private GpuimageSlideAdapter mGPUImageAdapter;

    private ArrayList<String> mImagList;
    private ArrayList<GPUImgItem> mGPUImgList;
    private GPUImgItem mCurrentGPUImage;
    private ThumbnailItem mCurrentThumbnailItem;

    private SwipeViewPager mViewPager;
    private FiltersListFragment filtersListFragment;
    private EditImageFragment editImageFragment;

    private TabLayout mTabLayout;

    private Bitmap originalImage;

    private RelativeLayout mRelFilterValue;
    private RelativeLayout mRelFilterSelect;

    private ThumbTextSeekBar mSeekbarValue;
//    private SeekBar mSeekbar;
//    private TextView mTextValue;

    private TextView mTextCancel;
    private TextView mTextDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_img_edit);

        mImagList = getIntent().getStringArrayListExtra("ImageList");
        //mImgView = (GPUImageView)findViewById(R.id.gpuimage);
        mGPUImagePager = (ViewPager)findViewById(R.id.viewpager_gpuimage) ;
        mGPUImageAdapter = new GpuimageSlideAdapter(getApplicationContext());
        initGPUImageList();
        mGPUImagePager.setAdapter(mGPUImageAdapter);
        mGPUImagePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int i, float v, int i1){ }
            @Override
            public void onPageSelected(int i){
                mCurrentGPUImage = mGPUImgList.get(i);
                //mGPUImageAdapter.setSelectPos(i);
                filtersListFragment.updateThumbnail(mImagList.get(i));
            }
            @Override
            public void onPageScrollStateChanged(int i){ }
        });

        findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);

        mViewPager = (SwipeViewPager)findViewById(R.id.edit_viewpager);
        mViewPager.setPagingEnabled(false);
        setupViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){ }
            @Override
            public void onPageSelected(int position){
                ArrayList<ThumbnailItem> tItems;
                if(position == 0)
                {
                    tItems = filtersListFragment.getThumbnailItemList();
                }
                else
                {
                    tItems = editImageFragment.getEditItemList();
                }
                for(ThumbnailItem item : tItems)
                {
                    if(item.isSelected)
                    {
                        mCurrentGPUImage.setFilter(item.filter);
                        mCurrentGPUImage.requestRender();
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state){}
        });

        mTabLayout = (TabLayout)findViewById(R.id.edit_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorHeight(0);

        mRelFilterSelect = (RelativeLayout)findViewById(R.id.relative_filter_select);
        mRelFilterValue = (RelativeLayout)findViewById(R.id.relative_filter_value);

        mRelFilterValue.setVisibility(View.GONE);

//        mSeekbar = (SeekBar)findViewById(R.id.seekbar_filter_value);
//        mTextValue = (TextView)findViewById(R.id.text_seekbar_value);

        mSeekbarValue = (ThumbTextSeekBar)findViewById(R.id.seekbar_filter_value);
        mSeekbarValue.setOnSeekBarChangeListener(mOnSeekbarChangeListener);

        mTextCancel = (TextView)findViewById(R.id.text_cancel);
        mTextCancel.setOnClickListener(mOnClickListener);
        mTextDone = (TextView)findViewById(R.id.text_done);
        mTextDone.setOnClickListener(mOnClickListener);
    }

    private void initGPUImageList()
    {
        mGPUImgList = new ArrayList<GPUImgItem>();
        for(String imgPath : mImagList)
        {
            GPUImgItem item = new GPUImgItem();
            item.setImagePath(imgPath);
            mGPUImgList.add(item);
        }
        mCurrentGPUImage = mGPUImgList.get(0);
        mGPUImageAdapter.setImages(mGPUImgList);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        //filtersListFragment.setImageList(mImagList);
        filtersListFragment.setImagePath(mImagList.get(0));
        filtersListFragment.setListener(mFilterListListener);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(mFilterListListener);


        adapter.addFragment(filtersListFragment, "Filters");
        adapter.addFragment(editImageFragment, "Edit");

        viewPager.setAdapter(adapter);
    }

    private void createFilterList()
    {

    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), NoticeDialog.class);
        startActivityForResult(intent, REQUEST_NOTICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_NOTICE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                // remove filter
                finish();
            }
            else
            {

            }
        }
    }


    private SeekBar.OnSeekBarChangeListener mOnSeekbarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            //Log.d("AAAA", "getprogress : "+seekBar.getProgress() + " progress : "+progress);
            mSeekbarValue.setThumbText(""+progress);
            mCurrentGPUImage.setFilterValue(progress);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){ }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){ }
    };


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_next)
            {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                intent.putStringArrayListExtra("ImageList", mImagList);
                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_back)
            {
                Intent intent = new Intent(getApplicationContext(), NoticeDialog.class);
                startActivityForResult(intent, REQUEST_NOTICE);
                //finish();
            }
            else if(v.getId() == R.id.text_cancel)
            {
                mRelFilterValue.setVisibility(View.GONE);
                mRelFilterSelect.setVisibility(View.VISIBLE);
                mCurrentThumbnailItem.isSetted = false;
            }
            else if(v.getId() == R.id.text_done)
            {
                mRelFilterValue.setVisibility(View.GONE);
                mRelFilterSelect.setVisibility(View.VISIBLE);

                mCurrentThumbnailItem.isSetted = true;
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


    private FiltersListSelectListener mFilterListListener = new FiltersListSelectListener()
    {
        @Override
        public void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect){
            if(isSecondSelect)
            {
                mRelFilterValue.setVisibility(View.VISIBLE);
                mRelFilterSelect.setVisibility(View.GONE);
            }
            else
            {
                mRelFilterValue.setVisibility(View.GONE);
                mRelFilterSelect.setVisibility(View.VISIBLE);

                if(mCurrentGPUImage != null)
                {
                    mCurrentGPUImage.setFilter(filter);
                    mCurrentGPUImage.requestRender();

                    ArrayList<ThumbnailItem> tItems = filtersListFragment.getThumbnailItemList();;
                    for(ThumbnailItem item : tItems)
                    {
                        if(item.filter == filter)
                        {
                            mCurrentThumbnailItem = item;
                        }
                    }
                }
            }
        }
    };


}

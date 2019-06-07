package com.feed.plugin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.feed.plugin.adapter.GpuimageSlideAdapter;
import com.feed.plugin.adapter.items.GPUImgItem;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.fragment.EditImageFragment;
import com.feed.plugin.fragment.FiltersListFragment;
import com.feed.plugin.fragment.FiltersListSelectListener;
import com.feed.plugin.util.FilterValue;
import com.feed.plugin.widget.SwipeViewPager;
import com.feed.plugin.widget.thumbseekbar.ThumbTextSeekBar;

import java.util.ArrayList;
import java.util.List;

public class ImgEditActivity extends AppCompatActivity{

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

    private ArrayList<FilterValue> mArrEditFilter = new ArrayList<>();
    private FilterValue mCurrentFilter = new FilterValue();
    private FilterValue mTempEditFilter = new FilterValue();
    private int mCurrentFilterState = 0;

    private boolean isCallByUnity = false;
    protected ProgressDialog mProgress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_img_edit);

        mProgress = new ProgressDialog(this);

        String editImgPath = getIntent().getStringExtra(BridgeCls.EXTRA_EDITIMG_PATH);
        if(editImgPath != null)
        {
            isCallByUnity = true;
            mImagList = new ArrayList<>();
            mImagList.add(editImgPath);
        }
        else
        {
            mImagList = getIntent().getStringArrayListExtra("ImageList");
        }

        //mImgView = (GPUImageView)findViewById(R.id.gpuimage);
        mGPUImagePager = (ViewPager)findViewById(R.id.viewpager_gpuimage) ;
        mGPUImageAdapter = new GpuimageSlideAdapter(getApplicationContext());
        initGPUImageList();
        mGPUImagePager.setAdapter(mGPUImageAdapter);
        mGPUImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
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
                mCurrentFilterState = position;
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

                        mCurrentThumbnailItem = item;

                        if(position == 1)
                        {
                            mTempEditFilter.setFilter(item.filter);
                        }
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
        EditViewPagerAdapter adapter = new EditViewPagerAdapter(getSupportFragmentManager());

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
            if(mCurrentFilterState == 0)
            {
                mCurrentFilter.setValue(progress);
            }
            else
            {
                //mArrEditFilter.get(mArrEditFilter.size()-1).setValue(progress);
                mTempEditFilter.setValue(progress);
            }

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
                //showProgress(ImgEditActivity.this, true);
//                AsyncSetFilter async = new AsyncSetFilter();
//                async.execute();

                for(GPUImgItem gpuimg : mGPUImgList)
                {
                    gpuimg.setFilter(mCurrentFilter.getFilter());
                    gpuimg.setFilterValue(mCurrentFilter.getValue());
                    gpuimg.requestRender();
                    for(FilterValue editFilter : mArrEditFilter)
                    {
                        gpuimg.setFilter(editFilter.getFilter());
                        gpuimg.setFilterValue(editFilter.getValue());
                        gpuimg.requestRender();
                    }

                    String fullpath = gpuimg.getImagePath();
                    int idx = fullpath.lastIndexOf("/");
                    String path = fullpath.substring(0, idx);
                    String name = "filter_" + fullpath.substring(idx+1, fullpath.length());
                    //String name = fullpath.substring(idx+1, fullpath.length());
                    gpuimg.saveGpuImage(path, name, pictureSavedListener);
                    gpuimg.getImagePath();
                }

                if(isCallByUnity)
                {
                    //UnityPlayer.UnitySendMessage();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                    intent.putStringArrayListExtra("ImageList", mImagList);
                    startActivity(intent);
                }

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

                if(mCurrentFilterState == 1)
                {
                    try{
                        FilterValue filterValue = (FilterValue)mTempEditFilter.clone();
                        mArrEditFilter.add(filterValue);
                    }catch(CloneNotSupportedException ce)
                    {
                        ce.printStackTrace();
                    }
                    mTempEditFilter.setFilter(null);
                }
            }
        }
    };

    private FiltersListSelectListener mEditFilterListListener = new FiltersListSelectListener(){
        @Override
        public void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect){
            if(isSecondSelect)
            {
                mRelFilterValue.setVisibility(View.VISIBLE);
                mRelFilterSelect.setVisibility(View.GONE);
            }
            else
            {
                if(mArrEditFilter.size() > 0)
                {
                    for(FilterValue filtvalue : mArrEditFilter)
                    {
                        GPUImageFilter filt = filtvalue.getFilter();
                        Class csass = filt.getClass();
                        if(filtvalue.getFilter().getClass().getName().equalsIgnoreCase(filter.getClass().getName()) )
                        {
                            mTempEditFilter = filtvalue;
                            break;
                        }
                    }
                }
                if(mTempEditFilter.getFilter() == null)
                {
                    FilterValue fv = new FilterValue();
                    fv.setFilter(filter);
                    mArrEditFilter.add(fv);
                    mTempEditFilter.setFilter(filter);
                }
            }
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
    };

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
                if(mCurrentFilterState == 0)
                {
                    mCurrentFilter.setFilter(filter);
                }
                else
                {
                    FilterValue tValue = null;
                    if(mArrEditFilter.size() > 0)
                    {
                        for(FilterValue filtvalue : mArrEditFilter)
                        {
                            GPUImageFilter filt = filtvalue.getFilter();
                            Class csass = filt.getClass();
                            String name = csass.getName();
                            if(filtvalue.getFilter().getClass().getName().equalsIgnoreCase(filter.getClass().getName()) )
                            {
//                                tValue = filtvalue;
//                                mArrEditFilter.remove(filtvalue);

                                mTempEditFilter = filtvalue;
                                break;
                            }
                        }
                    }
                    if(mTempEditFilter.getFilter() == null)
                    {
                        mTempEditFilter.setFilter(filter);
                    }
                    //mArrEditFilter.add(tValue);
                }
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

    private GPUImageView.OnPictureSavedListener pictureSavedListener = new GPUImageView.OnPictureSavedListener()
    {
        @Override
        public void onPictureSaved(Uri uri){
            String path = uri.getPath();
            if(path != null)
            {

            }
        }
    };

    private class AsyncSetFilter extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids){
            boolean result = true;

            for(GPUImgItem gpuimg : mGPUImgList)
            {
                gpuimg.setFilter(mCurrentFilter.getFilter());
                gpuimg.setFilterValue(mCurrentFilter.getValue());
                gpuimg.requestRender();
                for(FilterValue editFilter : mArrEditFilter)
                {
                    gpuimg.setFilter(editFilter.getFilter());
                    gpuimg.setFilterValue(editFilter.getValue());
                    gpuimg.requestRender();
                }

                String fullpath = gpuimg.getImagePath();
                int idx = fullpath.lastIndexOf("/");
                String path = fullpath.substring(0, idx);
                String name = fullpath.substring(idx+1, fullpath.length());
                gpuimg.saveGpuImage(path, name, pictureSavedListener);
                gpuimg.getImagePath();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);

            //showProgress(ImgEditActivity.this, false);

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), FeedUploadActivity.class);
            intent.putStringArrayListExtra("ImageList", mImagList);
            startActivity(intent);
        }
    };


    public void showProgress(final Activity act, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgress.setMessage("Saving...");
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

    class EditViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public EditViewPagerAdapter(FragmentManager manager) {
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

}

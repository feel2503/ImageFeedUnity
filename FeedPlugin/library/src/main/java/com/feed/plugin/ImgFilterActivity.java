package com.feed.plugin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageBrightnessFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageContrastFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilterGroup;
import com.feed.plugin.android.gpuimage.filter.GPUImageSaturationFilter;
import com.feed.plugin.fragment.EditImageFragment;
import com.feed.plugin.fragment.FiltersListFragment;
import com.feed.plugin.fragment.FiltersListSelectListener;
import com.feed.plugin.widget.SwipeViewPager;
import com.feed.plugin.widget.thumbseekbar.ThumbTextSeekBar;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImgFilterActivity extends AppCompatActivity{

    private int REQUEST_NOTICE = 0x1001;
    private int FILTER_STATE_TRANS_FILTER = 0;
    private int FILTER_STATE_EDIT_FILTER = 1;
    private int mCurrentFilterState = FILTER_STATE_TRANS_FILTER;

    private boolean isCallByUnity = false;
    private ArrayList<String> mImagList;
    private ArrayList<String> mFilterImgList;

    // gpuimageview
    private ViewPager mGPUImagePager;
    private GpuimageSlideAdapter mGPUImageAdapter;
    private ArrayList<GPUImgItem> mGPUImgList;
    private GPUImgItem mCurrentGPUImage;

    // filterlist view
    private SwipeViewPager mViewPager;
    private EditViewPagerAdapter mEditPagerAdapter;
    private FiltersListFragment mFiltersListFragment;
    private EditImageFragment mEditImageFragment;

    private TabLayout mTabLayout;

    private RelativeLayout mRelFilterValue;
    private RelativeLayout mRelFilterSelect;

    private ThumbTextSeekBar mSeekbarValue;

    private TextView mTextCancel;
    private TextView mTextDone;

    private ArrayList<GPUImgItem> mArrEditFilter = new ArrayList<>();
    private GPUImgItem mCurrentTransFilter;
    private GPUImgItem mCurrentEditFilter = new GPUImgItem();

    private int mSavedImgCount = 0;

    protected ProgressDialog mProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_img_filter);

        mProgress = new ProgressDialog(this);

        findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);

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

        // gpuimageviewer init
        mGPUImagePager = (ViewPager)findViewById(R.id.viewpager_gpuimage) ;
        mGPUImageAdapter = new GpuimageSlideAdapter(getApplicationContext());
        initGPUImageList();
        mGPUImagePager.setAdapter(mGPUImageAdapter);
        mGPUImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){ }

            @Override
            public void onPageSelected(int position)
            {
                mCurrentGPUImage = mGPUImgList.get(position);
                mFiltersListFragment.updateThumbnail(mImagList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state){ }
        });

        // filterlist init
        setupViewPager();

        mTabLayout = (TabLayout)findViewById(R.id.edit_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorHeight(0);

        mRelFilterSelect = (RelativeLayout)findViewById(R.id.relative_filter_select);
        mRelFilterValue = (RelativeLayout)findViewById(R.id.relative_filter_value);

        mRelFilterValue.setVisibility(View.GONE);

        mSeekbarValue = (ThumbTextSeekBar)findViewById(R.id.seekbar_filter_value);
        mSeekbarValue.setOnSeekBarChangeListener(mOnSeekbarChangeListener);

        mTextCancel = (TextView)findViewById(R.id.text_cancel);
        mTextCancel.setOnClickListener(mOnClickListener);
        mTextDone = (TextView)findViewById(R.id.text_done);
        mTextDone.setOnClickListener(mOnClickListener);



    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), NoticeDialog.class);
        startActivityForResult(intent, REQUEST_NOTICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_NOTICE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                // remove filter
                deleteImgFiles();
                finish();
            }
            else
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void setupViewPager() {
        mViewPager = (SwipeViewPager)findViewById(R.id.edit_viewpager);
        mViewPager.setPagingEnabled(false);

        mEditPagerAdapter = new EditViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        mFiltersListFragment = new FiltersListFragment();
        //filtersListFragment.setImageList(mImagList);
        mFiltersListFragment.setImagePath(mImagList.get(0));
        mFiltersListFragment.setListener(mFilterListListener);

        // adding edit image fragment
        mEditImageFragment = new EditImageFragment();
        mEditImageFragment.setListener(mEditListListener);

        mEditPagerAdapter.addFragment(mFiltersListFragment, getString(R.string.filter));
        mEditPagerAdapter.addFragment(mEditImageFragment, getString(R.string.edit));

        mViewPager.setAdapter(mEditPagerAdapter);

        mViewPager.addOnPageChangeListener(mOnViewChangeListener);
    }

    private void deleteImgFiles()
    {
        if(mImagList != null)
        {
            for(String filePath : mImagList)
            {
                File file = new File(filePath);
                file.delete();
            }
        }
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekbarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();

            if(mCurrentFilterState == FILTER_STATE_TRANS_FILTER)
            {
                //mCurrentTransFilter.setValue(progress);
                mCurrentTransFilter.setFilterValue(progress);
                groupFilter.addFilter(mCurrentTransFilter.getFilter());
                if(mArrEditFilter != null && mArrEditFilter.size() > 0)
                {
                    for(GPUImgItem efilter : mArrEditFilter){
                        groupFilter.addFilter(efilter.getFilter());
                    }
                }
                mCurrentGPUImage.setFilter(groupFilter);
                mCurrentGPUImage.requestRender();
            }
            else
            {
                //mCurrentEditFilter.setValue(progress);
                mCurrentEditFilter.setFilterValue(progress);

                if(mCurrentTransFilter != null)
                    groupFilter.addFilter(mCurrentTransFilter.getFilter());

                if(mArrEditFilter != null && mArrEditFilter.size() > 0)
                {
                    for(GPUImgItem efilter : mArrEditFilter){
                        groupFilter.addFilter(efilter.getFilter());
                    }
                }
                groupFilter.addFilter(mCurrentEditFilter.getFilter());

                mCurrentGPUImage.setFilter(groupFilter);
                mCurrentGPUImage.requestRender();

                mCurrentGPUImage.setFilterValue(progress);

            }
            mSeekbarValue.setThumbText(""+progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar){
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar){
        }
    };

    private ViewPager.OnPageChangeListener mOnViewChangeListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
            mCurrentFilterState = position;
        }

        @Override
        public void onPageSelected(int position){
            ArrayList<ThumbnailItem> tItems;
            if(position == 0)
            {
                tItems = mFiltersListFragment.getThumbnailItemList();
            }
            else
            {
                tItems = mEditImageFragment.getEditItemList();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state){

        }
    };


    GPUImage.ResponseListener<Bitmap> mReponseListener = new GPUImage.ResponseListener<Bitmap>()
    {
        @Override
        public void response(Bitmap item){
            String fullpath = mGPUImgList.get(mSavedImgCount).getImagePath();
            int idx = fullpath.lastIndexOf("/");
            String path = fullpath.substring(0, idx);
            String name = "filter_" + fullpath.substring(idx + 1, fullpath.length());
            mSavedImgCount++;

            //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, name);
            file.getParentFile().mkdirs();

            FileOutputStream out = null;
            //Bitmap bitmap = mGPUImageView.getGPUImage().getBitmapWithFilterApplied();
            try {
                out = new FileOutputStream(file);
                item.compress(Bitmap.CompressFormat.PNG, 100, out);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            showProgress(ImgFilterActivity.this, false);

            if(mFilterImgList == null)
                mFilterImgList = new ArrayList<String>();

            String imgUrl = path+"/"+name;
            mFilterImgList.add(imgUrl);
            if(mSavedImgCount == mGPUImgList.size())
            {
                mSavedImgCount= 0;

                if(isCallByUnity)
                {
                    UnityPlayer.UnitySendMessage("Manager","AndroidToUnity",imgUrl);
                    //UnityPlayer.UnitySendMessage("게임 오브젝트 이름","함수 이름","String 인자");

                    finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                    intent.putStringArrayListExtra("ImageList", mFilterImgList);
                    startActivity(intent);
                }
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.btn_next)
            {
                showProgress(ImgFilterActivity.this, true);

                AsyncSetFilter async = new AsyncSetFilter();
                async.execute();

//                ArrayList<GPUImageFilter> filters = new ArrayList<GPUImageFilter>();
//
//                GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();
//                if(mCurrentTransFilter != null)
//                {
//                    //filters.add(mCurrentTransFilter.getFilter());
//
//                    groupFilter.addFilter(mCurrentTransFilter.getFilter());
//                }
//                if(mArrEditFilter != null && mArrEditFilter.size() > 0)
//                {
//                    for(GPUImgItem efilter : mArrEditFilter)
//                    {
//                        groupFilter.addFilter(efilter.getFilter());
//
//                        //filters.add(efilter.getFilter());
//                    }
//                }
//
//                filters.add(groupFilter);
//
//                if(groupFilter.getFilters().size() < 1)
//                {
//                    Intent intent = new Intent();
//                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
//                    intent.putStringArrayListExtra("ImageList", mImagList);
//                    startActivity(intent);
//                }
//                else
//                {
//                    for(GPUImgItem gpuimg : mGPUImgList)
//                    {
//                        Bitmap source = BitmapFactory.decodeFile(gpuimg.getImagePath());
//                        GPUImage.getBitmapForMultipleFilters(source, filters, mReponseListener);
//                    }
//                }

            }
            else if(v.getId() == R.id.btn_back)
            {
                Intent intent = new Intent(getApplicationContext(), NoticeDialog.class);
                startActivityForResult(intent, REQUEST_NOTICE);
            }
            else if(v.getId() == R.id.text_cancel)
            {
                mRelFilterValue.setVisibility(View.GONE);
                mRelFilterSelect.setVisibility(View.VISIBLE);
            }
            else if(v.getId() == R.id.text_done)
            {
                if(mCurrentFilterState == FILTER_STATE_EDIT_FILTER)
                {
                    GPUImgItem item = new GPUImgItem();
                    item.setFilter(mCurrentEditFilter.getFilter());
                    mArrEditFilter.add(item);
                    //mArrEditFilter.add(mCurrentEditFilter);

                    ArrayList<ThumbnailItem> tItems = mEditImageFragment.getEditItemList();
                    for(ThumbnailItem titem : tItems)
                    {
                        if(titem.filter == mCurrentEditFilter.getFilter())
                            titem.isSetted = true;
                    }
                }
                else
                {
                    ArrayList<ThumbnailItem> tItems = mFiltersListFragment.getThumbnailItemList();;
                    for(ThumbnailItem item : tItems)
                    {
                        if(item.filter == mCurrentTransFilter.getFilter())
                            item.isSetted = true;
                        else
                            item.isSetted = false;
                    }
                }
                mRelFilterValue.setVisibility(View.GONE);
                mRelFilterSelect.setVisibility(View.VISIBLE);

            }
        }
    };

    private FiltersListSelectListener mFilterListListener = new FiltersListSelectListener()
    {
        @Override
        public void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect)
        {
            if(isSecondSelect)
            {
                mRelFilterValue.setVisibility(View.VISIBLE);
                mRelFilterSelect.setVisibility(View.GONE);
                mSeekbarValue.initProgressValue(false);
            }
            else
            {
                if(mCurrentTransFilter == null)
                    mCurrentTransFilter = new GPUImgItem();

                mCurrentTransFilter.setFilter(filter);
                if(mCurrentGPUImage != null)
                {
                    GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();
                    groupFilter.addFilter(filter);
                    if(mArrEditFilter != null && mArrEditFilter.size() > 0)
                    {
                        for(GPUImgItem efilter : mArrEditFilter)
                        {
                            groupFilter.addFilter(efilter.getFilter());
                        }
                    }

                    mCurrentGPUImage.setFilter(groupFilter);
                    mCurrentGPUImage.requestRender();


                }
            }
        }
    };

    private FiltersListSelectListener mEditListListener = new FiltersListSelectListener()
    {
        @Override
        public void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect)
        {
            if(isSecondSelect)
            {
                mRelFilterValue.setVisibility(View.VISIBLE);
                mRelFilterSelect.setVisibility(View.GONE);

                if(filter instanceof GPUImageBrightnessFilter || filter instanceof GPUImageContrastFilter || filter instanceof GPUImageSaturationFilter)
                {
                    mSeekbarValue.initProgressValue(true);
                }
                else
                {
                    mSeekbarValue.initProgressValue(false);
                }
            }
            else
            {
                if(mCurrentEditFilter == null)
                    mCurrentEditFilter = new GPUImgItem();

                mCurrentEditFilter.setFilter(filter);

                if(mCurrentGPUImage != null)
                {
                    GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();
                    if(mCurrentTransFilter != null)
                    {
                        groupFilter.addFilter(mCurrentTransFilter.getFilter());
                    }

                    if(mArrEditFilter != null && mArrEditFilter.size() > 0)
                    {
                        for(GPUImgItem efilter : mArrEditFilter)
                        {
                            groupFilter.addFilter(efilter.getFilter());
                        }
                    }
                    groupFilter.addFilter(filter);

                    mCurrentGPUImage.setFilter(groupFilter);
                    mCurrentGPUImage.requestRender();
                }
            }

        }
    };

    private GPUImage.OnPictureSavedListener mGPUImageSavedListener = new GPUImage.OnPictureSavedListener()
    {
        @Override
        public void onPictureSaved(Uri uri){

//            GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();
//            if(mCurrentTransFilter != null)
//                groupFilter.addFilter(mCurrentTransFilter.getFilter());
//            if(mArrEditFilter != null && mArrEditFilter.size() > 0)
//            {
//                for(GPUImgItem efilter : mArrEditFilter)
//                {
//                    groupFilter.addFilter(efilter.getFilter());
//                }
//            }
//
//            GPUImage simg1 = new GPUImage(getApplicationContext());
//            Bitmap bitmap1 = BitmapFactory.decodeFile(mGPUImgList.get(1).getImagePath());
//            simg1.setFilter(groupFilter);
//            simg1.setImage(bitmap1);
//            String fullpath1 = mGPUImgList.get(1).getImagePath();
//            int idx1 = fullpath1.lastIndexOf("/");
//            String name1 = "filter_" + fullpath1.substring(idx1+1, fullpath1.length());
//            simg1.saveToPictures("GPUImage", name1, mGPUImageSavedListener);

        }
    };

    private GPUImageView.OnPictureSavedListener mPictureSavedListener = new GPUImageView.OnPictureSavedListener()
    {
        @Override
        public void onPictureSaved(Uri uri){
//            if(mGPUImgList.size() > mSavedImgCount)
//            {
//                GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();
//                if(mCurrentTransFilter == null)
//                    groupFilter.addFilter(mCurrentTransFilter.getFilter());
//                if(mArrEditFilter != null && mArrEditFilter.size() > 0)
//                {
//                    for(GPUImgItem efilter : mArrEditFilter)
//                    {
//                        groupFilter.addFilter(efilter.getFilter());
//                    }
//                }
//
//                GPUImgItem gpuimg = mGPUImgList.get(mSavedImgCount);
//                gpuimg.setFilter(groupFilter);
//
//                String fullpath = gpuimg.getImagePath();
//                int idx = fullpath.lastIndexOf("/");
//                String path = fullpath.substring(0, idx);
//                String name = "filter_" + fullpath.substring(idx+1, fullpath.length());
//                //String name = fullpath.substring(idx+1, fullpath.length());
//                gpuimg.saveGpuImage(path, name, mPictureSavedListener);
//                mSavedImgCount ++;
//            }
//            else
//            {
//                if(isCallByUnity)
//                {
//                    //UnityPlayer.UnitySendMessage();
//                }
//                else
//                {
//                    Intent intent = new Intent();
//                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
//                    intent.putStringArrayListExtra("ImageList", mImagList);
//                    startActivity(intent);
//                }
//            }


            String path = uri.getPath();
            if(path != null)
            {

            }
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

    private class AsyncSetFilter extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids){
            ArrayList<GPUImageFilter> filters = new ArrayList<GPUImageFilter>();

            GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();
            if(mCurrentTransFilter != null)
            {
                //filters.add(mCurrentTransFilter.getFilter());

                groupFilter.addFilter(mCurrentTransFilter.getFilter());
            }
            if(mArrEditFilter != null && mArrEditFilter.size() > 0)
            {
                for(GPUImgItem efilter : mArrEditFilter)
                {
                    groupFilter.addFilter(efilter.getFilter());

                    //filters.add(efilter.getFilter());
                }
            }

            filters.add(groupFilter);

            if(groupFilter.getFilters().size() < 1)
            {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                intent.putStringArrayListExtra("ImageList", mImagList);
                startActivity(intent);
            }
            else
            {
                for(GPUImgItem gpuimg : mGPUImgList)
                {
                    Bitmap source = BitmapFactory.decodeFile(gpuimg.getImagePath());
                    GPUImage.getBitmapForMultipleFilters(source, filters, mReponseListener);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            super.onPostExecute(aBoolean);
        }
    };

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

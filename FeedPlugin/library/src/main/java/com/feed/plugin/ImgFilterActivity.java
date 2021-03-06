package com.feed.plugin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.feed.plugin.adapter.GpuimageSlideAdapter;
import com.feed.plugin.adapter.items.GPUImgItem;
import com.feed.plugin.adapter.items.ImgFilterSetItem;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageBrightnessFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageContrastFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilterGroup;
import com.feed.plugin.android.gpuimage.filter.GPUImageHueFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImagePixelationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSaturationFilter;
import com.feed.plugin.fragment.EditImageFragment;
import com.feed.plugin.fragment.FiltersListFragment;
import com.feed.plugin.fragment.FiltersListSelectListener;
import com.feed.plugin.widget.CycleView;
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
    private int REQUEST_FEED_UPLOAD = 0x1002;

    private int FILTER_STATE_TRANS_FILTER = 0;
    private int FILTER_STATE_EDIT_FILTER = 1;
    private int mCurrentFilterState = FILTER_STATE_TRANS_FILTER;

    //private boolean isCallByUnity = false;
    private ArrayList<String> mImagList;
    private ArrayList<String> mFilterImgList;
    private ArrayList<ImgFilterSetItem> mArrImgSetFilterList;

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
    private LinearLayout mLinearPagerPos;

    private ThumbTextSeekBar mSeekbarValue;

    private TextView mTextCancel;
    private TextView mTextDone;
    private TextView mTextFilterName;

    private ArrayList<GPUImgItem> mArrEditFilter = new ArrayList<>();
    private GPUImgItem mCurrentTransFilter;
    private GPUImgItem mCurrentEditFilter = new GPUImgItem();

    private int mSavedImgCount = 0;

    protected ProgressDialog mProgress = null;

    private String mStrActivitMode;


    private int dotsCount;
    private ImageView[] dots;
    private CycleView mCycleView;

    Handler mHandler = new Handler()
    {
        @Override
        public void dispatchMessage(Message msg){
            super.dispatchMessage(msg);
            // gpuimageviewer init
            mGPUImagePager = (ViewPager)findViewById(R.id.viewpager_gpuimage) ;
            mGPUImageAdapter = new GpuimageSlideAdapter(getApplicationContext());
            initGPUImageList();
            mGPUImagePager.setAdapter(mGPUImageAdapter);
            mGPUImagePager.addOnPageChangeListener(mOnViewChangeListener);


            mFiltersListFragment.initSelected();
            mEditImageFragment.initSelected();
            mViewPager.setCurrentItem(0);

            mCurrentTransFilter = null;
            mCurrentEditFilter = new GPUImgItem();

//            mGPUImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){ }
//
//                @Override
//                public void onPageSelected(int position)
//                {
//                    mCurrentGPUImage = mGPUImgList.get(position);
//                    mFiltersListFragment.updateThumbnail(mImagList.get(position));
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state){ }
//            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_img_filter);

        mProgress = new ProgressDialog(this);

        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));
        textTitle.setText(getString(R.string.edit));

        findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);


        //String editImgPath = getIntent().getStringExtra(BridgeCls.EXTRA_EDITIMG_LIST);

        Intent intent = getIntent();

        mImagList = intent.getStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST);
        mStrActivitMode = intent.getStringExtra(BridgeCls.EXTRA_ACTIVITY_MODE);


        // gpuimageviewer init
        mGPUImagePager = (ViewPager)findViewById(R.id.viewpager_gpuimage) ;
        mGPUImageAdapter = new GpuimageSlideAdapter(getApplicationContext());
        initGPUImageList();
        mGPUImagePager.setAdapter(mGPUImageAdapter);
        mGPUImagePager.addOnPageChangeListener(mPageChangeListener);

//        mGPUImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){ }
//
//            @Override
//            public void onPageSelected(int position)
//            {
//                mCurrentGPUImage = mGPUImgList.get(position);
//                mFiltersListFragment.updateThumbnail(mImagList.get(position));
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state){ }
//        });

        // filterlist init
        setupViewPager();

        mTabLayout = (TabLayout)findViewById(R.id.edit_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorHeight(0);
        setCustomFont();

        mRelFilterSelect = (RelativeLayout)findViewById(R.id.relative_filter_select);
        mRelFilterValue = (RelativeLayout)findViewById(R.id.relative_filter_value);
        mLinearPagerPos = findViewById(R.id.linear_pager_pos);


        mRelFilterValue.setVisibility(View.GONE);

        mSeekbarValue = (ThumbTextSeekBar)findViewById(R.id.seekbar_filter_value);
        mSeekbarValue.setOnSeekBarChangeListener(mOnSeekbarChangeListener);

        mTextCancel = (TextView)findViewById(R.id.text_cancel);
        mTextCancel.setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));
        mTextCancel.setOnClickListener(mOnClickListener);
        mTextDone = (TextView)findViewById(R.id.text_done);
        mTextDone.setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));
        mTextDone.setOnClickListener(mOnClickListener);

        mTextFilterName = findViewById(R.id.text_filter_name);
        mTextFilterName.setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));

        initPageMark();

        mCycleView = (CycleView)findViewById(R.id.cycle_view);
        if(mStrActivitMode != null && mStrActivitMode.equalsIgnoreCase(BridgeCls.ACTIVITY_MODE_PROFILE))
        {
            mCycleView.setPathColor(0xffffffff);
        }
        else
        {
            mCycleView.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        if(mStrActivitMode != null && mStrActivitMode.equalsIgnoreCase(BridgeCls.ACTIVITY_MODE_FILTER))
        {
            super.onBackPressed();
            return;
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), NoticeDialog.class);
            startActivityForResult(intent, REQUEST_NOTICE);
        }

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
        else if(requestCode == REQUEST_FEED_UPLOAD)
        {
            if(resultCode == RESULT_OK)
            {
                setResult(RESULT_OK);
                finish();
            }
            else if(resultCode == RESULT_CANCELED)
            {
                mHandler.sendEmptyMessageDelayed(1, 200);
                //mCurrentGPUImage.requestRender();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initPageMark()
    {
        dotsCount = mImagList.size();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mLinearPagerPos.getLayoutParams();
//            lp.setMargins(10, 0, 0, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 0, 0);
            dots[i].setLayoutParams(params);

            dots[i].setImageResource(R.drawable.pagepos_oval);
            mLinearPagerPos.addView(dots[i]);
        }

        dots[0].setImageResource(R.drawable.pagepos_sel_oval);


    }

    private void pagePositionCheck(int position)
    {

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageResource(R.drawable.pagepos_oval);
        }
        dots[position].setImageResource(R.drawable.pagepos_sel_oval);

        // Config.PAGE_SELECT_MIN_LEFT 95
//
//        int setLeft = 0;
//
//        setLeft = Config.PAGE_SELECT_MIN_LEFT
//
//                + (position * Config.PAGE_SELECT_LEFT_PLUS);
//
//
//
//        bottomPoint.setPadding(setLeft, 0, 0, 0);

    }







    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) mTabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));
                    ((TextView) tabViewChild).setTextColor(0xffffffff);
                }
            }
        }
    }

    private void initGPUImageList()
    {
        mGPUImgList = new ArrayList<GPUImgItem>();
        mArrImgSetFilterList = new ArrayList<ImgFilterSetItem>();
        for(String imgPath : mImagList)
        {
            GPUImgItem item = new GPUImgItem();
            item.setImagePath(imgPath);
            mGPUImgList.add(item);

            ImgFilterSetItem ifitem = new ImgFilterSetItem();
            ifitem.imagePath = imgPath;
            mArrImgSetFilterList.add(ifitem);

        }
        mCurrentGPUImage = mGPUImgList.get(0);
        mGPUImageAdapter.setImages(mGPUImgList);
    }

    private void setupViewPager()
    {
        mViewPager = (SwipeViewPager)findViewById(R.id.edit_viewpager);
        mViewPager.setPagingEnabled(false);
        mViewPager.addOnPageChangeListener(mOnViewChangeListener);

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

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

        }

        @Override
        public void onPageSelected(int position){
            for(ImgFilterSetItem item : mArrImgSetFilterList)
            {
                if(item.imagePath.equalsIgnoreCase(mCurrentGPUImage.getImagePath()))
                {
                    GPUImgItem transFilter = new GPUImgItem();
                    if(mCurrentTransFilter != null )
                    {
                        transFilter.setFilter(mCurrentTransFilter.getFilter());
                        transFilter.setFilterValue(mCurrentTransFilter.getFilterValue());
                        item.mTransFilter = transFilter;
                    }
                    ArrayList<GPUImgItem> arrEditFilter = new ArrayList<>();
                    if(mArrEditFilter != null && mArrEditFilter.size() > 0)
                    {
                        for(GPUImgItem editItem : mArrEditFilter)
                        {
                            GPUImgItem gitem = new GPUImgItem();
                            gitem.setFilter(editItem.getFilter());
                            //gitem.setFilterValue(editItem.getFilterValue());
                            arrEditFilter.add(gitem);

                        }
                        item.mArrEditFilter = arrEditFilter;
                    }
                    break;
                }
            }

            mCurrentGPUImage = mGPUImgList.get(position);
            mFiltersListFragment.updateThumbnail(mImagList.get(position));
            pagePositionCheck(position);

            for(ImgFilterSetItem item : mArrImgSetFilterList)
            {
                if(item.imagePath.equalsIgnoreCase(mCurrentGPUImage.getImagePath()))
                {
                    if(item.mTransFilter != null)
                    {
                        mCurrentTransFilter = item.mTransFilter;
                    }
                    if(item.mArrEditFilter != null && item.mArrEditFilter.size() > 0)
                    {
                        mArrEditFilter = item.mArrEditFilter;
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state){

        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekbarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            GPUImageFilterGroup groupFilter = new GPUImageFilterGroup();

            if(mCurrentFilterState == FILTER_STATE_TRANS_FILTER)
            {
                if(mCurrentTransFilter == null)
                    return;

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



            if(mFilterImgList == null)
                mFilterImgList = new ArrayList<String>();

            String imgUrl = path+"/"+name;
            mFilterImgList.add(imgUrl);
            if(mSavedImgCount == mGPUImgList.size())
            {
                mSavedImgCount= 0;

                if(mStrActivitMode == null || mStrActivitMode.length() < 1)
                {
                    showProgress(ImgFilterActivity.this, false);

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                    intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, mFilterImgList);
                    startActivityForResult(intent, REQUEST_FEED_UPLOAD);
                }
                else if(mStrActivitMode.equalsIgnoreCase(BridgeCls.ACTIVITY_MODE_PROFILE))
                {
                    showProgress(ImgFilterActivity.this, false);

                    UnityPlayer.UnitySendMessage("FeedModule","SetProfilePath",imgUrl);

                    BridgeCls.mStrProfilePath = imgUrl;
                    setResult(RESULT_OK);
                    finish();
                }
                else if(mStrActivitMode.equalsIgnoreCase(BridgeCls.ACTIVITY_MODE_FILTER))
                {
                    showProgress(ImgFilterActivity.this, false);

                    UnityPlayer.UnitySendMessage("FeedModule","SetFilterImagePath",imgUrl);
                    BridgeCls.mStrFilterPath = imgUrl;
                    setResult(RESULT_OK);
                    finish();
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

                for(ImgFilterSetItem item : mArrImgSetFilterList)
                {
                    if(item.imagePath.equalsIgnoreCase(mCurrentGPUImage.getImagePath()))
                    {
                        GPUImgItem transFilter = new GPUImgItem();
                        if(mCurrentTransFilter != null )
                        {
                            transFilter.setFilter(mCurrentTransFilter.getFilter());
                            //transFilter.setFilterValue(mCurrentTransFilter.getFilterValue());
                            item.mTransFilter = transFilter;
                        }
                        ArrayList<GPUImgItem> arrEditFilter = new ArrayList<>();
                        if(mArrEditFilter != null && mArrEditFilter.size() > 0)
                        {
                            for(GPUImgItem editItem : mArrEditFilter)
                            {
                                GPUImgItem gitem = new GPUImgItem();
                                gitem.setFilter(editItem.getFilter());
                                //gitem.setFilterValue(editItem.getFilterValue());
                                arrEditFilter.add(gitem);
                            }
                            item.mArrEditFilter = arrEditFilter;
                        }
                        break;
                    }
                }

                if(mStrActivitMode == null && mImagList.size() > 1)
                {
                    AsyncMulSetFilter async = new AsyncMulSetFilter();
                    async.execute(mSavedImgCount);
                }
                else
                {
                    AsyncSetFilter async = new AsyncSetFilter();
                    async.execute();
                }

//                AsyncSetFilter async = new AsyncSetFilter();
//                async.execute();
            }
            else if(v.getId() == R.id.btn_back)
            {
                if(mStrActivitMode != null && mStrActivitMode.equalsIgnoreCase(BridgeCls.ACTIVITY_MODE_FILTER))
                {
                    finish();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), NoticeDialog.class);
                startActivityForResult(intent, REQUEST_NOTICE);
            }
            else if(v.getId() == R.id.text_cancel)
            {
                mRelFilterValue.setVisibility(View.GONE);
                mRelFilterSelect.setVisibility(View.VISIBLE);
                mLinearPagerPos.setVisibility(View.VISIBLE);
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
                mLinearPagerPos.setVisibility(View.VISIBLE);

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
                mLinearPagerPos.setVisibility(View.GONE);

                mTextFilterName.setText(mFiltersListFragment.getSelectFilterName());

                if(filter instanceof GPUImagePixelationFilter)
                    mSeekbarValue.initProgressValue(1);
//                else if(filter instanceof GPUImageHueFilter)
//                    mSeekbarValue.initProgressValue(25);
                else
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

    private class AsyncSetFilter extends AsyncTask<Void, Void, Boolean>
    {
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
                if(mStrActivitMode == null || mStrActivitMode.length() < 1)
                {
                    showProgress(ImgFilterActivity.this, false);

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                    intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, mImagList);
                    startActivityForResult(intent, REQUEST_FEED_UPLOAD);
                }
                else if(mStrActivitMode.equalsIgnoreCase(BridgeCls.ACTIVITY_MODE_PROFILE))
                {
                    showProgress(ImgFilterActivity.this, false);

                    UnityPlayer.UnitySendMessage("FeedModule","SetProfilePath",mImagList.get(0));

                    BridgeCls.mStrProfilePath = mImagList.get(0);
                    setResult(RESULT_OK);
                    finish();
                }
                else if(mStrActivitMode.equalsIgnoreCase(BridgeCls.ACTIVITY_MODE_FILTER))
                {
                    showProgress(ImgFilterActivity.this, false);

                    UnityPlayer.UnitySendMessage("FeedModule","SetFilterImagePath",mImagList.get(0));

                    BridgeCls.mStrFilterPath = mImagList.get(0);
                    setResult(RESULT_OK);
                    finish();
                }

//                Intent intent = new Intent();
//                intent.setClass(getApplicationContext(), FeedUploadActivity.class);
//                intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, mImagList);
//                startActivityForResult(intent, REQUEST_FEED_UPLOAD);
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

    private class AsyncMulSetFilter extends AsyncTask<Integer, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Integer... params)
        {
            int pos = params[0];
            GPUImgItem gpuimg = mGPUImgList.get(pos);
            ArrayList<GPUImageFilter> arrFilters = new ArrayList<GPUImageFilter>();
            GPUImageFilterGroup grFilter = new GPUImageFilterGroup();

            for(ImgFilterSetItem filItem : mArrImgSetFilterList)
            {
                if(filItem.imagePath.equalsIgnoreCase(gpuimg.getImagePath()))
                {

                    if(filItem.mTransFilter != null)
                    {
                        grFilter.addFilter(filItem.mTransFilter.getFilter());
                    }
                    if(filItem.mArrEditFilter != null && filItem.mArrEditFilter.size() > 0)
                    {
                        for(GPUImgItem efilter : filItem.mArrEditFilter)
                        {
                            grFilter.addFilter(efilter.getFilter());
                        }
                    }
                    arrFilters.add(grFilter);
                    break;
                }
            }


            if(grFilter.getFilters().size() < 1)
            {
                mSavedImgCount++;

                if(mFilterImgList == null)
                    mFilterImgList = new ArrayList<String>();

                mFilterImgList.add(gpuimg.getImagePath());
                return true;
            }
            else
            {
                Bitmap source = BitmapFactory.decodeFile(gpuimg.getImagePath());
                GPUImage.getBitmapForMultipleFilters(source, arrFilters, mReponseMulListener);

                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);

            if(result)
            {
                if(mSavedImgCount == mGPUImgList.size())
                {
                    mSavedImgCount= 0;
                    showProgress(ImgFilterActivity.this, false);

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                    intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, mFilterImgList);
                    startActivityForResult(intent, REQUEST_FEED_UPLOAD);
                }
                else
                {
                    AsyncMulSetFilter async = new AsyncMulSetFilter();
                    async.execute(mSavedImgCount);
                }
            }

        }
    }

    GPUImage.ResponseListener<Bitmap> mReponseMulListener = new GPUImage.ResponseListener<Bitmap>()
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

            if(mFilterImgList == null)
                mFilterImgList = new ArrayList<String>();

            String imgUrl = path+"/"+name;
            mFilterImgList.add(imgUrl);
            if(mSavedImgCount == mGPUImgList.size())
            {
                mSavedImgCount= 0;

                if(mStrActivitMode == null || mStrActivitMode.length() < 1)
                {
                    showProgress(ImgFilterActivity.this, false);

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), FeedUploadActivity.class);
                    intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, mFilterImgList);
                    startActivityForResult(intent, REQUEST_FEED_UPLOAD);
                }
            }
            else
            {
                AsyncMulSetFilter async = new AsyncMulSetFilter();
                async.execute(mSavedImgCount);
            }
        }
    };
}

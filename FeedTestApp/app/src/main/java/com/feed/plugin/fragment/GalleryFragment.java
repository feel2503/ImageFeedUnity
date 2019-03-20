package com.feed.plugin.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.feed.plugin.ImgSelectActivity;
import com.feed.plugin.R;
import com.feed.plugin.fragment.gallery.GalleryAdapter;
import com.feed.plugin.fragment.gallery.GalleryManager;
import com.feed.plugin.fragment.gallery.GridDividerDecoration;
import com.feed.plugin.fragment.gallery.OnItemClickListener;
import com.feed.plugin.fragment.gallery.PhotoVO;
import com.feed.plugin.widget.crop.CropperView;
import com.feed.plugin.widget.cropimgview.CropImageView;
import com.feed.plugin.widget.cropimgview.callback.LoadCallback;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends ImgSelFragment{

    private static GalleryFragment instance;

    private RelativeLayout mRelImgViewer;
    private LinearLayout.LayoutParams mImgViewLayout;
    private GalleryManager mGalleryManager;
    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;

    private boolean imgViewState = true;




    private ImgSelectActivity mParentActivity;

    private CropImageView mCropView;
    private RectF mFrameRect = null;

    private Button mBtnViewState;
    private Button mBtnSelState;
    private Button mBtnCrop16_9;
    private Button mBtnCrop3_4;
    private Button mBtnCrop1_1;

    public static GalleryFragment getInstance()
    {
        if(instance == null)
        {
            Bundle args = new Bundle();
            instance = new GalleryFragment();
            instance.setArguments(args);
        }
        return instance;
    }

    public GalleryFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mParentActivity = (ImgSelectActivity)getActivity();
        bindView(rootView);

        initRecyclerGallery();

        //mCropView.setDebug(true);

        return rootView;
    }

    private void bindView(View rootView)
    {
        mRelImgViewer = (RelativeLayout)rootView.findViewById(R.id.relative_gall_imgviewer);

        mCropView = (CropImageView) rootView.findViewById(R.id.cropImageView);

        recyclerGallery = (RecyclerView) rootView.findViewById(R.id.recycler_gallery);
        mBtnViewState = rootView.findViewById(R.id.btn_change_gall_imgview_state);
        mBtnViewState.setOnClickListener(mOnClickListenr);
        mBtnSelState = rootView.findViewById(R.id.btn_img_sel_mode);
        mBtnSelState.setOnClickListener(mOnClickListenr);
        mBtnCrop16_9 = rootView.findViewById(R.id.btn_crop_16_9);
        mBtnCrop16_9.setOnClickListener(mOnClickListenr);
        mBtnCrop3_4 = rootView.findViewById(R.id.btn_crop_3_4);
        mBtnCrop3_4.setOnClickListener(mOnClickListenr);
        mBtnCrop1_1 = rootView.findViewById(R.id.btn_crop_1_1);
        mBtnCrop1_1.setOnClickListener(mOnClickListenr);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mImgViewLayout == null)
            mImgViewLayout =  (LinearLayout.LayoutParams)mRelImgViewer.getLayoutParams();
    }

    /**
     * 갤러리 아미지 데이터 초기화
     */
    private List<PhotoVO> initGalleryPathList() {

        mGalleryManager = new GalleryManager(getContext());
        //return mGalleryManager.getDatePhotoPathList(2015, 9, 19);
        return mGalleryManager.getAllPhotoPathList();
    }

    /**
     * 갤러리 리사이클러뷰 초기화
     */
    private void initRecyclerGallery() {

        galleryAdapter = new GalleryAdapter(getActivity(), initGalleryPathList(), R.layout.item_photo);
        galleryAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerGallery.setAdapter(galleryAdapter);
        recyclerGallery.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerGallery.setItemAnimator(new DefaultItemAnimator());
        recyclerGallery.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));
    }

    private void loadNewImage(String filePath) {
        mFrameRect = null;
        filePath = "file://" + filePath;
        Uri sourceUrk = Uri.parse(filePath);
        mCropView.load(sourceUrk)
                .initialFrameRect(mFrameRect)
                .useThumbnail(true)
                .execute(mLoadCallback);


//        mBitmap = BitmapFactory.decodeFile(filePath);
//        originalBitmap = mBitmap;
//        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
//        float scale1280 = (float)maxP / 1280;
//        if (mImageView.getWidth() != 0) {
//            mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
//        } else {
//            ViewTreeObserver vto = mImageView.getViewTreeObserver();
//            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
//                    mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
//                    return true;
//                }
//            });
//        }
//        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int)(mBitmap.getWidth()/scale1280),
//                (int)(mBitmap.getHeight()/scale1280), true);
//        mImageView.setImageBitmap(mBitmap);
    }
    /**
     * 리사이클러뷰 아이템 선택시 호출 되는 리스너
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position) {

            PhotoVO photoVO = galleryAdapter.getmPhotoList().get(position);

            if(photoVO.isSelected()){
                photoVO.setSelected(false);
            }else{
                photoVO.setSelected(true);
            }

            String imgPath = photoVO.getImgPath();
            loadNewImage(imgPath);

            if(mArrImgList == null)
                mArrImgList = new ArrayList<>();

            if(mArrImgList.size() < 1)
                mArrImgList.add(imgPath);
            else
                mArrImgList.set(0, imgPath);    // 일단 하나만

            galleryAdapter.getmPhotoList().set(position,photoVO);
            galleryAdapter.notifyDataSetChanged();

        }
    };

    private void changeImgViewSize(boolean viewState)
    {

    }


    private View.OnClickListener mOnClickListenr = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.btn_change_gall_imgview_state)
            {
                if(imgViewState)
                {
                    mImgViewLayout.height = (int)getResources().getDimension(R.dimen.view_min);
                    mRelImgViewer.setLayoutParams(mImgViewLayout);
                    //mRelImgViewer.setVisibility(View.GONE);
                    imgViewState = false;
                }
                else
                {
                    mImgViewLayout.height = (int)getResources().getDimension(R.dimen.view_max);
                    mRelImgViewer.setLayoutParams(mImgViewLayout);

                    //mRelImgViewer.setVisibility(View.VISIBLE);
                    imgViewState = true;
                }
            }
            else if(v.getId() == R.id.btn_crop_16_9)
            {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
            }
            else if(v.getId() == R.id.btn_crop_3_4)
            {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
            }
            else if(v.getId() == R.id.btn_crop_1_1)
            {
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);
            }
            else if(v.getId() == R.id.btn_img_sel_mode)
            {

            }
        }
    };


    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess() {
        }

        @Override public void onError(Throwable e) {
        }
    };
}

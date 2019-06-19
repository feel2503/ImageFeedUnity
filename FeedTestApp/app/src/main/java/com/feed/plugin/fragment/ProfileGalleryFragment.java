package com.feed.plugin.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.feed.plugin.ImgSelectActivity;
import com.feed.plugin.ProfileActivity;
import com.feed.plugin.R;
import com.feed.plugin.fragment.gallery.GalleryAdapter;
import com.feed.plugin.fragment.gallery.GalleryManager;
import com.feed.plugin.fragment.gallery.GridDividerDecoration;
import com.feed.plugin.fragment.gallery.OnItemClickListener;
import com.feed.plugin.fragment.gallery.PhotoVO;
import com.feed.plugin.widget.crop.CropperView;
import com.feed.plugin.widget.cropimgview.CropImageView;
import com.feed.plugin.widget.cropimgview.callback.CropCallback;
import com.feed.plugin.widget.cropimgview.callback.LoadCallback;
import com.feed.plugin.widget.cropimgview.callback.SaveCallback;
import com.feed.plugin.widget.cropimgview.util.CropUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileGalleryFragment extends ImgSelFragment{

    private static ProfileGalleryFragment instance;

    private LinearLayout mRelImgViewer;

    private RelativeLayout.LayoutParams mImgViewLayout;
    private GalleryManager mGalleryManager;
    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;

    private boolean imgViewState = true;

    private ProfileActivity mParentActivity;

    private CropperView mImageView;
    private Bitmap originalBitmap;
    private Bitmap mBitmap;

    private RectF mFrameRect = null;
    private Uri mSourceUri = null;
    protected ArrayList<String> mArrImgList;
    protected ArrayList<String> mCropImgList;

    private String mCurrentLoadImg;

    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private int mSavePos = 0;

    public static ProfileGalleryFragment getInstance()
    {
        if(instance == null)
        {
            Bundle args = new Bundle();
            instance = new ProfileGalleryFragment();
            instance.setArguments(args);
        }
        return instance;
    }

    public ProfileGalleryFragment()
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
        View rootView = inflater.inflate(R.layout.fragment_profile_gallery, container, false);
        mParentActivity = (ProfileActivity)getActivity();
        recyclerGallery = (RecyclerView) rootView.findViewById(R.id.recycler_gallery);

        mImageView = (CropperView)rootView.findViewById(R.id.img_selimg);
        mImageView.setGridCallback(new CropperView.GridCallback() {
            @Override
            public boolean onGestureStarted() {
                mParentActivity.setPagingEnabled(false);
                return true;
            }

            @Override
            public boolean onGestureCompleted() {
                mParentActivity.setPagingEnabled(true);
                return false;
            }
        });

        initRecyclerGallery();

        return rootView;
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

        //galleryAdapter = new GalleryAdapter(getActivity(), initGalleryPathList(), R.layout.item_photo);
        galleryAdapter = new GalleryAdapter(getActivity(), initGalleryPathList(), R.layout.gallery_item_photo);
        galleryAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerGallery.setAdapter(galleryAdapter);
        recyclerGallery.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerGallery.setItemAnimator(new DefaultItemAnimator());
        recyclerGallery.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));


        // init select image
        mArrImgList = new ArrayList<>();
        List<PhotoVO> itemList = galleryAdapter.getmPhotoList();
        if(itemList.size() > 0)
        {
            mArrImgList.add(itemList.get(0).getImgPath());
            itemList.get(0).setSelected(true);
            itemList.get(0).setSelectCount(mArrImgList.size());
            loadNewImage(itemList.get(0).getImgPath());
        }



    }

    private void loadNewImage(String filePath) {

        mBitmap = BitmapFactory.decodeFile(filePath);
        originalBitmap = mBitmap;
        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float)maxP / 1280;
        if (mImageView.getWidth() != 0) {
            mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
        } else {
            ViewTreeObserver vto = mImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
                    return true;
                }
            });
        }
        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int)(mBitmap.getWidth()/scale1280),
                (int)(mBitmap.getHeight()/scale1280), true);
        mImageView.setImageBitmap(mBitmap);
    }

    public void getCropImgList()
    {
        ((ImgSelectActivity)getActivity()).showProgress(getActivity(), true);

        if(mCropImgList == null)
            mCropImgList = new ArrayList<>();

        String imgPath = mArrImgList.get(mSavePos);
        imgPath = "file://" + imgPath;
        Uri uri = Uri.parse(imgPath);

//        //mCropView.crop(mSourceUri).execute(mCropCallback);
//        mCropView.crop(uri).execute(mCropCallback);
//        mSavePos += 1;


    }

    public Uri createSaveUri() {
        return createNewUri(getContext(), mCompressFormat);
    }

    public static Uri createNewUri(Context context, Bitmap.CompressFormat format){
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = CropUtils.getDirPath(context);
        String fileName = "crop" + title + "." + CropUtils.getMimeType(format);
        String path = dirPath + "/" + fileName;

//        File file = new File(path);
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, title);
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + CropUtils.getMimeType(format));
//        values.put(MediaStore.Images.Media.DATA, path);
//        long time = currentTimeMillis / 1000;
//        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
//        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
//        if(file.exists()){
//            values.put(MediaStore.Images.Media.SIZE, file.length());
//        }
//        ContentResolver resolver = context.getContentResolver();
//        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        return uri;

        path = "file://" + path;
        Uri newUri = Uri.parse(path);
        return newUri;

    }
    /**
     * 리사이클러뷰 아이템 선택시 호출 되는 리스너
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position)
        {
            PhotoVO photoVO = galleryAdapter.getmPhotoList().get(position);

            if(mArrImgList == null)
                mArrImgList = new ArrayList<>();

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






}

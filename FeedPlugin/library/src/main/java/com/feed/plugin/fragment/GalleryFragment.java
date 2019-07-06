package com.feed.plugin.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.feed.plugin.ImgSelectActivity;
import com.feed.plugin.R;
import com.feed.plugin.fragment.gallery.GalleryAdapter;
import com.feed.plugin.fragment.gallery.GalleryManager;
import com.feed.plugin.fragment.gallery.GridDividerDecoration;
import com.feed.plugin.fragment.gallery.OnItemClickListener;
import com.feed.plugin.fragment.gallery.PhotoVO;
import com.feed.plugin.widget.cookicrop.ImageUtils;
import com.feed.plugin.widget.crop.BitmapUtils;
import com.feed.plugin.widget.cropimgview.CropImageView;
import com.feed.plugin.widget.cropimgview.callback.CropCallback;
import com.feed.plugin.widget.cropimgview.callback.LoadCallback;
import com.feed.plugin.widget.cropimgview.callback.SaveCallback;
import com.feed.plugin.widget.cropimgview.util.CropUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryFragment extends ImgSelFragment{

    private static GalleryFragment instance;

    private LinearLayout mRelImgViewer;

    private RelativeLayout.LayoutParams mImgViewLayout;
    private GalleryManager mGalleryManager;
    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;

    private boolean imgViewState = true;

    private ImgSelectActivity mParentActivity;

    private CropImageView mCropView;

    private Uri mSourceUri = null;
    //protected ArrayList<String> mArrImgList;
    protected ArrayList<CropImgItem> mArrImgList;
    protected ArrayList<String> mCropImgList;

    private Button mBtnViewState;
    private ToggleButton mToggleSelState;
    private Button mBtnCrop16_9;
    private Button mBtnCrop3_4;
    private Button mBtnCrop1_1;

    private String mCurrentLoadImg;

    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private int mSavePos = 0;

    private int mCropType = -1;

    Handler mHandler = new Handler()
    {
        @Override
        public void dispatchMessage(Message msg){
            super.dispatchMessage(msg);
            loadNewImage(mArrImgList.get(0).mImgString);
        }
    };

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
        RelativeLayout relCropView = (RelativeLayout)rootView.findViewById(R.id.relative_gall_imgviewer);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams params = relCropView.getLayoutParams();
        params.height = width;
        relCropView.setLayoutParams(params);

        mRelImgViewer = (LinearLayout)rootView.findViewById(R.id.gallery_layout);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mRelImgViewer.getLayoutParams();
        int margintop = width - (int)(displayMetrics.density * 40);
        lp.setMargins(0, margintop, 0, 0);
        mRelImgViewer.setLayoutParams(lp);

        mCropView = (CropImageView) rootView.findViewById(R.id.cropImageView);

        mCropView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    mParentActivity.setPagingEnabled(false);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    mParentActivity.setPagingEnabled(true);
                }
                return false;
            }
        });
        mCropView.setCropEnabled(false);

        recyclerGallery = (RecyclerView) rootView.findViewById(R.id.recycler_gallery);
        mBtnViewState = rootView.findViewById(R.id.btn_change_gall_imgview_state);
        mBtnViewState.setOnClickListener(mOnClickListenr);
        mToggleSelState = rootView.findViewById(R.id.btn_img_sel_mode);
        mToggleSelState.setOnClickListener(mOnClickListenr);
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
            mImgViewLayout =  (RelativeLayout.LayoutParams)mRelImgViewer.getLayoutParams();
    }


    private List<PhotoVO> initGalleryPathList() {

        mGalleryManager = new GalleryManager(getContext());
        //return mGalleryManager.getDatePhotoPathList(2015, 9, 19);
        return mGalleryManager.getAllPhotoPathList();
    }


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
            //mArrImgList.add(itemList.get(0).getImgPath());
            mArrImgList.add(new CropImgItem(itemList.get(0).getImgPath(), null, null));
            itemList.get(0).setSelected(true);
            itemList.get(0).setSelectCount(mArrImgList.size());
            //loadNewImage(itemList.get(0).getImgPath());
            mHandler.sendEmptyMessageDelayed(1, 200);
        }



    }

    private void loadNewImage(String filePath) {
        RectF frameRect = mCropView.getFrameRect();
        RectF imgRect = mCropView.getImageRect();
        for(CropImgItem item : mArrImgList)
        {
            if(frameRect == null || imgRect == null)
                continue;

            if(item.mImgString.equalsIgnoreCase(mCurrentLoadImg))
            {
                int left = (int)(frameRect.left - imgRect.left);
                int top = (int)(frameRect.top - imgRect.top);
                int right = (int)(frameRect.right - frameRect.left) + left;
                int bottom = (int)(frameRect.bottom - frameRect.top) + top;

                item.mCropRect = new RectF(left, top, right, bottom);
                //item.mImgRect = new RectF(imgRect);

                //item.mImgRect = new RectF(imgRect);
                break;
            }
        }

        RectF preFrameRect = null;
        for(CropImgItem item : mArrImgList)
        {
            if(item.mImgString.equalsIgnoreCase(filePath))
            {
                //preFrameRect = item.mCropRect;
                if(item.mCropRect != null)
                {
                    preFrameRect = new RectF(item.mCropRect);
                }
                break;
            }
        }

        mCurrentLoadImg = filePath;
        filePath = "file://" + filePath;
        Uri sourceUri = Uri.parse(filePath);
        //mSourceUri = Uri.parse(filePath);
        //mCropView.load(mSourceUri)
        mCropView.load(sourceUri)
                .initialFrameRect(preFrameRect)
                //.useThumbnail(true)
                .useThumbnail(false)
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

    public void getCropImgList()
    {
        ((ImgSelectActivity)getActivity()).showProgress(getActivity(), true);

        if(mCropImgList == null)
            mCropImgList = new ArrayList<>();

        String imgPath = mArrImgList.get(mSavePos).mImgString;
        imgPath = "file://" + imgPath;
        Uri uri = Uri.parse(imgPath);


        if(mArrImgList.size() == 1)
        {
            mCropView.crop(uri).execute(mCropCallback);
            mSavePos += 1;
        }
        else
        {
            RectF frameRect = mCropView.getFrameRect();
            RectF imgRect = mCropView.getImageRect();
            for(CropImgItem item : mArrImgList)
            {
                if(item.mImgString.equalsIgnoreCase(mCurrentLoadImg))
                {
                    int left = (int)(frameRect.left - imgRect.left);
                    int top = (int)(frameRect.top - imgRect.top);
                    int right = (int)(frameRect.right - frameRect.left) + left;
                    int bottom = (int)(frameRect.bottom - frameRect.top) + top;

                    item.mCropRect = new RectF(left, top, right, bottom);
                    item.mImgRect = new RectF(imgRect);

                    //item.mImgRect = new RectF(imgRect);
                    break;
                }
            }

            AsyncCropImage async = new AsyncCropImage();
            async.execute();
        }





//        //Uri sourceUri = Uri.parse(filePath);
//        mSourceUri = Uri.parse(imgPath);
//
//        mCropView.crop(mSourceUri).execute(mCropCallback);
//        //return mArrImgList;
    }

    public Uri createSaveUri() {
        return createNewUri(getContext(), mCompressFormat);
    }

    public static Uri createNewUri(Context context, Bitmap.CompressFormat format){
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("mmss");
        //String title = dateFormat.format(today);
        String title = ""+currentTimeMillis;
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

    private void removeImgObject(String imgPath)
    {
        for(CropImgItem item : mArrImgList)
        {
            if(item.mImgString.equalsIgnoreCase(imgPath))
            {
                mArrImgList.remove(item);
                break;
            }
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position)
        {
            PhotoVO photoVO = galleryAdapter.getmPhotoList().get(position);
            String imgPath = photoVO.getImgPath();

            if(mArrImgList == null)
                mArrImgList = new ArrayList<>();

            if(galleryAdapter.getMultiSelect())
            {
                if(photoVO.isSelected())
                {
                    if(mCurrentLoadImg.equalsIgnoreCase(imgPath))
                    {
                        if(mArrImgList.size() == 1)
                            return;

                        photoVO.setSelected(false);
                        photoVO.setSelectCount(-1);
                        removeImgObject(imgPath);
                        //mArrImgList.remove(imgPath);
                        imgPath = mArrImgList.get(mArrImgList.size()-1).mImgString;
                        for(int i = 0; i < galleryAdapter.getmPhotoList().size(); i++)
                        {
                            for(int j = 0; j < mArrImgList.size(); j++)
                            {
                                if(galleryAdapter.getmPhotoList().get(i).getImgPath().equalsIgnoreCase(mArrImgList.get(j).mImgString))
                                {
                                    galleryAdapter.getmPhotoList().get(i).setSelectCount(j+1);
                                    break;
                                }
                            }
                        }
                        loadNewImage(imgPath);
                    }
                    else
                    {
                        loadNewImage(imgPath);
                    }
                }
                else
                {
                    if(mArrImgList.size() < 5)
                    {
                        photoVO.setSelected(true);
                        mArrImgList.add(new CropImgItem(imgPath, null, null));
                        photoVO.setSelectCount(mArrImgList.size());

                        loadNewImage(imgPath);
                    }

                }
            }
            else
            {
                List<PhotoVO> itemList = galleryAdapter.getmPhotoList();
                if(mArrImgList.size() < 1)
                {
                    mArrImgList.add(new CropImgItem(imgPath, null, null));
                }
                else
                {
                    mArrImgList.set(0, new CropImgItem(imgPath, null, null));
                    for(PhotoVO item : itemList)
                    {
                        if(item.getImgPath().equalsIgnoreCase(imgPath))
                        {
                            item.setSelected(true);
                            item.setSelectCount(mArrImgList.size());
                        }
                        else
                        {
                            item.setSelected(false);
                            item.setSelectCount(-1);
                        }
                    }
                }

                loadNewImage(imgPath);
            }



//            if(photoVO.isSelected()){
//                photoVO.setSelected(false);
//            }else{
//                photoVO.setSelected(true);
//            }
//
//            String imgPath = photoVO.getImgPath();
//            loadNewImage(imgPath);
//
//            if(mArrImgList == null)
//                mArrImgList = new ArrayList<>();
//
//            if(mArrImgList.size() < 1)
//                mArrImgList.add(imgPath);
//            else
//                mArrImgList.set(0, imgPath);

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
                    //mImgViewLayout.height = (int)getResources().getDimension(R.dimen.view_min);
                    int topMargin = (int)getResources().getDimension(R.dimen.view_min);
                    mImgViewLayout.topMargin = topMargin;
                    //mImgViewLayout.topMargin = (int)getResources().getDimension(R.dimen.view_min);
                    mRelImgViewer.setLayoutParams(mImgViewLayout);
                    //mRelImgViewer.setVisibility(View.GONE);
                    mBtnViewState.setBackgroundResource(R.drawable.btn_gallery_slide_down);
                    imgViewState = false;
                }
                else
                {
                    //mImgViewLayout.height = (int)getResources().getDimension(R.dimen.view_max);
                    mImgViewLayout.topMargin = (int)getResources().getDimension(R.dimen.view_max);
                    mRelImgViewer.setLayoutParams(mImgViewLayout);

                    //mRelImgViewer.setVisibility(View.VISIBLE);
                    mBtnViewState.setBackgroundResource(R.drawable.btn_gallery_slide);
                    imgViewState = true;
                }
            }
            else if(v.getId() == R.id.btn_crop_16_9)
            {
                if(mCropType  == 0 && mCropView.isCropEnable())
                {
                    mCropView.setCropEnabled(false);
                    for(CropImgItem item : mArrImgList)
                    {
                        item.mCropRect = null;
                    }
                }
                else
                {
                    mCropView.setCropEnabled(true);
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                    mCropType = 0;

                    for(CropImgItem item : mArrImgList)
                    {
                        float wf = item.mImgRect.right - item.mImgRect.left;
                        float hf = item.mImgRect.bottom - item.mImgRect.top;
                        if((wf == hf) || (wf < hf))
                        {
                            float height = ((9 * wf) / 16);
                            float left = 0;
                            float top = (hf-height) / 2;
                            float right = wf;
                            float bottom = top + height;

                            item.mCropRect = new RectF(left, top, right, bottom);
                        }
                        else if(wf > hf)
                        {
                            if((wf*9) == (hf*16))
                            {
                                item.mCropRect = new RectF(0, 0, wf, hf);
                            }
                            else
                            {
                                if((wf*9) > (hf*16))
                                {
                                    float width = (hf*16)/9;
                                    float left = (wf-width)/2;
                                    float top  = 0;
                                    float right = left + width;
                                    float bottom = hf;
                                    item.mCropRect = new RectF(left, top, right, bottom);
                                }
                                else
                                {
                                    float height = (wf*9)/16;
                                    float left = 0;
                                    float top = (hf-height)/2;
                                    float right = wf;
                                    float bottom = top + height;
                                    item.mCropRect = new RectF(left, top, right, bottom);
                                }
                            }
                        }
                    }
                }

            }
            else if(v.getId() == R.id.btn_crop_3_4)
            {
                if(mCropType == 1 && mCropView.isCropEnable())
                {
                    mCropView.setCropEnabled(false);
                    for(CropImgItem item : mArrImgList)
                    {
                        item.mCropRect = null;
                    }
                }
                else
                {
                    mCropView.setCropEnabled(true);
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                    mCropType = 1;

                    for(CropImgItem item : mArrImgList)
                    {
                        float wf = item.mImgRect.right - item.mImgRect.left;
                        float hf = item.mImgRect.bottom - item.mImgRect.top;
                        if((wf == hf) || (wf > hf))
                        {
                            float width = (hf*3)/4;
                            float left = (wf-width)/2;
                            float top  = 0;
                            float right = left + width;
                            float bottom = hf;
                            item.mCropRect = new RectF(left, top, right, bottom);
                        }
                        else if(wf < hf)
                        {
                            if((wf*4) == (hf*3))
                            {
                                item.mCropRect = new RectF(0, 0, wf, hf);
                            }
                            else
                            {
                                if((wf*4) < (hf*3))
                                {
                                    float height = (4*wf)/3;
                                    float left = 0;
                                    float top = (hf-height)/2;
                                    float right = wf;
                                    float bottom = top + height;
                                    item.mCropRect = new RectF(left, top, right, bottom);
                                }
                                else
                                {
                                    float width = (hf*3)/4;
                                    float left = (wf-width)/2;
                                    float top  = 0;
                                    float right = left + width;
                                    float bottom = hf;
                                    item.mCropRect = new RectF(left, top, right, bottom);
                                }
                            }
                        }
                    }
                }

            }
            else if( v.getId() == R.id.btn_crop_1_1)
            {
                if(mCropType == 2 && mCropView.isCropEnable())
                {
                    mCropView.setCropEnabled(false);
                    for(CropImgItem item : mArrImgList)
                    {
                        item.mCropRect = null;
                    }
                }
                else
                {
                    mCropView.setCropEnabled(true);
                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                    mCropType = 2;

                    for(CropImgItem item : mArrImgList)
                    {
                        float wf = item.mImgRect.right - item.mImgRect.left;
                        float hf = item.mImgRect.bottom - item.mImgRect.top;
                        if(wf == hf)
                        {
                            item.mCropRect = new RectF(0, 0, wf, hf);
                        }
                        else if(wf > hf)
                        {
                            float left = (wf - hf)/2;
                            float top  = 0;
                            float right = left + hf;
                            float bottom = hf;

                            item.mCropRect = new RectF(left, top, right, bottom);
                        }
                        else if(wf < hf)
                        {
                            float top = (hf - wf)/2;
                            float left = 0;
                            float right = wf;
                            float bottom = top + wf;
                            item.mCropRect = new RectF(left, top, right, bottom);
                        }

                        //item.mCropRect = null;
                    }
                }

            }
            else if(v.getId() == R.id.btn_img_sel_mode)
            {
                if(mToggleSelState.isChecked())
                {
                    galleryAdapter.setMultiSelectMode(true);
                }
                else
                {
                    galleryAdapter.setMultiSelectMode(false);
                    String imgPath = mArrImgList.get(mArrImgList.size()-1).mImgString;
                    mArrImgList.clear();
                    mArrImgList.add(new CropImgItem(imgPath, null, null));

                    List<PhotoVO> itemList = galleryAdapter.getmPhotoList();
                    for(PhotoVO item : itemList)
                    {
                        if(item.getImgPath().equalsIgnoreCase(imgPath))
                        {
                            item.setSelected(true);
                            item.setSelectCount(mArrImgList.size());
                        }
                        else
                        {
                            item.setSelected(false);
                            item.setSelectCount(-1);
                        }
                    }

                }
                galleryAdapter.notifyDataSetChanged();
            }
        }
    };


    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess() {
            RectF frameRect = mCropView.getFrameRect();
            RectF imgRect = mCropView.getImageRect();
            Uri uri = mCropView.getSourceUri();

            for(CropImgItem item : mArrImgList)
            {
                if(item.mImgString.equalsIgnoreCase(mCurrentLoadImg))
                {
                    item.mImgRect = new RectF(imgRect);
                    //item.mCropRect = new RectF(frameRect);
                }
            }
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override public void onSuccess(Bitmap cropped) {
            mCropView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createSaveUri(), mSaveCallback);
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri) {
            String uriStr = outputUri.toString();
            if(uriStr.startsWith("file"))
                uriStr = uriStr.substring(7);
            mCropImgList.add(uriStr);
            //dismissProgress();

            if(mArrImgList.size() == mSavePos )
            {
                ((ImgSelectActivity)getActivity()).showProgress(getActivity(), false);
                mSavePos = 0;
                //((ImgSelectActivity) getActivity()).startResultActivity(mArrImgList);
                ((ImgSelectActivity) getActivity()).startResultActivity(mCropImgList);

            }
            else
            {
                String imgPath = mArrImgList.get(mSavePos).mImgString;
                imgPath = "file://" + imgPath;
                Uri uri = Uri.parse(imgPath);

                if(mArrImgList.size() == 1)
                {
                    mCropView.crop(uri).execute(mCropCallback);
                    mSavePos += 1;
                }
            }
        }

        @Override public void onError(Throwable e) {
            //dismissProgress();
        }
    };


//    private Rect calcCropRect(int originalImageWidth, int originalImageHeight){
//        float scaleToOriginal =
//                getRotatedWidth(mAngle, originalImageWidth, originalImageHeight) / mImageRect.width();
//        float offsetX = mImageRect.left * scaleToOriginal;
//        float offsetY = mImageRect.top * scaleToOriginal;
//        int left = Math.round(mFrameRect.left * scaleToOriginal - offsetX);
//        int top = Math.round(mFrameRect.top * scaleToOriginal - offsetY);
//        int right = Math.round(mFrameRect.right * scaleToOriginal - offsetX);
//        int bottom = Math.round(mFrameRect.bottom * scaleToOriginal - offsetY);
//        int imageW = Math.round(getRotatedWidth(mAngle, originalImageWidth, originalImageHeight));
//        int imageH = Math.round(getRotatedHeight(mAngle, originalImageWidth, originalImageHeight));
//        return new Rect(Math.max(left, 0), Math.max(top, 0), Math.min(right, imageW),
//                Math.min(bottom, imageH));
//    }

    private class AsyncCropImage extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            for(CropImgItem item : mArrImgList)
            {
                Bitmap cropBitmap;
                try{
                    String filePath = "file://" + item.mImgString;
                    Uri sourceUri = Uri.parse(filePath);
                    int exifRotation = CropUtils.getExifOrientation(getContext(), sourceUri);

                    int sWidth = (int)(item.mImgRect.right - item.mImgRect.left);
                    int sHeight = (int)(item.mImgRect.bottom - item.mImgRect.top);

                    InputStream is = getContext().getContentResolver().openInputStream(sourceUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                    //Bitmap bitmap = ImageUtils.decodeUriToScaledBitmap(getActivity(), sourceUri, sWidth, sHeight);
                    if(exifRotation != 0)
                    {
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.setRotate(exifRotation, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                        bitmap =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
                    }
                    Bitmap scalBitmap = CropUtils.getScaledBitmap(bitmap, sWidth, sHeight);

                    if(item.mCropRect == null)
                    {
                        cropBitmap = scalBitmap;
                    }
                    else
                    {
                        Rect cropRect = new Rect(Math.max((int)item.mCropRect.left, 0), Math.max((int)item.mCropRect.top, 0),
                                Math.min((int)(item.mCropRect.right), sWidth), Math.min((int)(item.mCropRect.bottom), sHeight));

                        Log.d("AAAA", "creoRect "+cropRect);
                        cropBitmap = Bitmap.createBitmap(scalBitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());
                    }

                    Uri saveUri = createSaveUri();
                    File mypath = new File(saveUri.getPath());

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mypath);
                        cropBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();

                        Log.d("AAAA", "Save : "+saveUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("AAAA", "Save e "+e.toString());
                    }

                    mCropImgList.add(saveUri.getPath());

                    Thread.sleep(10);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
            ((ImgSelectActivity)getActivity()).showProgress(getActivity(), false);

            mSavePos = 0;
            //((ImgSelectActivity) getActivity()).startResultActivity(mArrImgList);
            ((ImgSelectActivity) getActivity()).startResultActivity(mCropImgList);
        }
    }

    public class CropImgItem
    {
        public RectF mImgRect;
        public RectF mCropRect;
        public String mImgString;
        public float mScale;

        public CropImgItem(String mImgString, RectF mImgRect, RectF mCropRect ){
            this.mImgRect = mImgRect;
            this.mCropRect = mCropRect;
            this.mImgString = mImgString;
        }
    }
}

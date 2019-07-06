package com.feed.plugin.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.feed.plugin.ImgSelectActivity;
import com.feed.plugin.ProfileActivity;
import com.feed.plugin.R;
import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.fragment.gallery.GalleryAdapter;
import com.feed.plugin.fragment.gallery.GalleryManager;
import com.feed.plugin.fragment.gallery.GridDividerDecoration;
import com.feed.plugin.fragment.gallery.OnItemClickListener;
import com.feed.plugin.fragment.gallery.PhotoVO;
import com.feed.plugin.widget.cookicrop.CookieCutterImageView;
import com.feed.plugin.widget.cookicrop.ImageUtils;
import com.feed.plugin.widget.crop.CropperView;
import com.feed.plugin.widget.cropimgview.util.CropUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileCookiGalleryFragment extends ImgSelFragment{

    private String profileImageName = "profileImage.jpg";
    private static ProfileCookiGalleryFragment instance;

    private RelativeLayout.LayoutParams mImgViewLayout;
    private GalleryManager mGalleryManager;
    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;


    private ProfileActivity mParentActivity;

    private CookieCutterImageView mImageView;
    protected ArrayList<String> mArrImgList;
    protected ArrayList<String> mCropImgList;

    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private int mSavePos = 0;

    private int PROFILE_SCALE_SIZE = 512;

    Handler mHandler = new Handler()
    {
        @Override
        public void dispatchMessage(Message msg){
            super.dispatchMessage(msg);
            loadNewImage(mArrImgList.get(0));
        }
    };

    public static ProfileCookiGalleryFragment getInstance()
    {
        if(instance == null)
        {
            Bundle args = new Bundle();
            instance = new ProfileCookiGalleryFragment();
            instance.setArguments(args);
        }
        return instance;
    }

    public ProfileCookiGalleryFragment()
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
        View rootView = inflater.inflate(R.layout.fragment_profile_cooki_gallery, container, false);
        mParentActivity = (ProfileActivity)getActivity();
        recyclerGallery = (RecyclerView) rootView.findViewById(R.id.recycler_gallery);

        mImageView = (CookieCutterImageView)rootView.findViewById(R.id.img_selimg);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams params = mImageView.getLayoutParams();
        params.height = width;
        mImageView.setLayoutParams(params);

        LinearLayout galleryLayout = (LinearLayout)rootView.findViewById(R.id.gallery_layout);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)galleryLayout.getLayoutParams();
        lp.setMargins(0, width, 0, 0);
        galleryLayout.setLayoutParams(lp);



        initRecyclerGallery();

        mHandler.sendEmptyMessageDelayed(1, 200);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();


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
            mArrImgList.add(itemList.get(0).getImgPath());
            itemList.get(0).setSelected(true);
            itemList.get(0).setSelectCount(mArrImgList.size());
            //loadNewImage(itemList.get(0).getImgPath());
        }



    }

    private void loadNewImage(String filePath) {
        try{
            String imgPath = "file://" + filePath;
            Uri imageUri = Uri.parse(imgPath);
            Point screenSize = ImageUtils.getScreenSize(getActivity());
            Bitmap scaledBitmap = ImageUtils.decodeUriToScaledBitmap(getActivity(), imageUri, screenSize.x, screenSize.y);
            mImageView.setImageBitmap(scaledBitmap);

//            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//            Bitmap scalBitmap = CropUtils.getScaledBitmapForWidth(bitmap, screenSize.x);
//            mImageView.setImageBitmap(scalBitmap);
        }catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    public void getCropImgList()
    {
        ((ProfileActivity)getActivity()).showProgress(getActivity(), true);

        if(mCropImgList == null)
            mCropImgList = new ArrayList<>();

//        String imgPath = mArrImgList.get(0);
//        imgPath = "file://" + imgPath;


//        //mCropView.crop(mSourceUri).execute(mCropCallback);
//        mCropView.crop(uri).execute(mCropCallback);
//        mSavePos += 1;

        ((ProfileActivity)getActivity()).showProgress(getActivity(), true);
        AsyncSaveTask task = new AsyncSaveTask();
        task.execute();


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

        path = "file://" + path;
        Uri newUri = Uri.parse(path);
        return newUri;

    }

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
                mArrImgList.set(0, imgPath);

            galleryAdapter.getmPhotoList().set(position,photoVO);
            galleryAdapter.notifyDataSetChanged();

        }
    };


    @Deprecated
    private class AsyncSaveTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(final Void... params) {
            Bitmap image = mImageView.getCroppedBitmap();

            // scale
            image = CropUtils.getScaledBitmap(image, PROFILE_SCALE_SIZE, PROFILE_SCALE_SIZE);

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String filePath = CropUtils.getDirPath(getContext());
            String savePath = filePath + "/" + profileImageName;

            File file = new File(savePath);
            try {
                file.getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                mCropImgList.add(savePath);
            } catch (FileNotFoundException fe) {
                fe.printStackTrace();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            ((ProfileActivity)getActivity()).showProgress(getActivity(), false);
            ((ProfileActivity) getActivity()).startResultActivity(mCropImgList);

        }


    }



}

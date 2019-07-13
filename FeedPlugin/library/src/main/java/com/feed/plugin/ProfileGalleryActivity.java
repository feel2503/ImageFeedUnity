package com.feed.plugin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feed.plugin.fragment.ProfileCookiGalleryFragment;
import com.feed.plugin.fragment.gallery.GalleryAdapter;
import com.feed.plugin.fragment.gallery.GalleryManager;
import com.feed.plugin.fragment.gallery.GridDividerDecoration;
import com.feed.plugin.fragment.gallery.OnItemClickListener;
import com.feed.plugin.fragment.gallery.PhotoVO;
import com.feed.plugin.widget.cookicrop.CookieCutterImageView;
import com.feed.plugin.widget.cookicrop.ImageUtils;
import com.feed.plugin.widget.cropimgview.util.CropUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileGalleryActivity extends AppCompatActivity{
    private int REQUEST_IMAGE_FILTER = 0x1002;

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

    protected ProgressDialog mProgress = null;

    private final int REQEUST_PERFMSSION_CODE = 0x1001;
    private int REQUEST_SETTING_FOR_PERMISSION = 0x1003;
    private String[] REQUIRED_PERMISSIONS  = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};  // 외부 저장소

    Handler mHandler = new Handler()
    {
        @Override
        public void dispatchMessage(Message msg){
            super.dispatchMessage(msg);
            loadNewImage(mArrImgList.get(0));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile_gallery);

        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setTypeface(Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf"));
        textTitle.setText(getString(R.string.gallelry));

        mProgress = new ProgressDialog(this);

        recyclerGallery = (RecyclerView) findViewById(R.id.recycler_gallery);

        mImageView = (CookieCutterImageView)findViewById(R.id.img_selimg);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams params = mImageView.getLayoutParams();
        params.height = width;
        mImageView.setLayoutParams(params);

        LinearLayout galleryLayout = (LinearLayout)findViewById(R.id.gallery_layout);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)galleryLayout.getLayoutParams();
        lp.setMargins(0, width, 0, 0);
        galleryLayout.setLayoutParams(lp);

        findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);


        boolean isPermission = checkVerify();
        if(isPermission)
        {
            initRecyclerGallery();
            mHandler.sendEmptyMessageDelayed(1, 200);
        }
    }


    /**
     * 갤러리 아미지 데이터 초기화
     */
    private List<PhotoVO> initGalleryPathList() {

        mGalleryManager = new GalleryManager(getApplicationContext());
        //return mGalleryManager.getDatePhotoPathList(2015, 9, 19);
        return mGalleryManager.getAllPhotoPathList();
    }


    private void initRecyclerGallery() {

        //galleryAdapter = new GalleryAdapter(getActivity(), initGalleryPathList(), R.layout.item_photo);
        galleryAdapter = new GalleryAdapter(ProfileGalleryActivity.this, initGalleryPathList(), R.layout.gallery_item_photo);
        galleryAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerGallery.setAdapter(galleryAdapter);
        recyclerGallery.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
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
            Point screenSize = ImageUtils.getScreenSize(ProfileGalleryActivity.this);
            Bitmap scaledBitmap = ImageUtils.decodeUriToScaledBitmap(ProfileGalleryActivity.this, imageUri, screenSize.x, screenSize.y);
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
        if(mCropImgList == null)
            mCropImgList = new ArrayList<>();

//        String imgPath = mArrImgList.get(0);
//        imgPath = "file://" + imgPath;


//        //mCropView.crop(mSourceUri).execute(mCropCallback);
//        mCropView.crop(uri).execute(mCropCallback);
//        mSavePos += 1;

        showProgress(ProfileGalleryActivity.this, true);
        AsyncSaveTask task = new AsyncSaveTask();
        task.execute();


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.btn_back)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
            else if(v.getId() == R.id.btn_next)
            {
                getCropImgList();
            }
        }
    };

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


    public boolean checkVerify()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
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

    public void showRequestAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileGalleryActivity.this);
        builder.setMessage(getString(R.string.requestpermission));
        builder.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_SETTING_FOR_PERMISSION);
                    //startActivity(intent);
                }catch (ActivityNotFoundException ae)
                {
                    ae.printStackTrace();

                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.create().show();
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
                initRecyclerGallery();
                mHandler.sendEmptyMessageDelayed(1, 200);
            }
            else {
                showRequestAgainDialog();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_FILTER)
        {
            if(resultCode == RESULT_OK)
            {
                finish();
            }
        }
        else if(requestCode == REQUEST_SETTING_FOR_PERMISSION)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
                {
                    initRecyclerGallery();
                    mHandler.sendEmptyMessageDelayed(1, 200);
                }
                else
                {
                    finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startResultActivity(ArrayList<String> imgList) {
        Intent intent = new Intent();
        //intent.setClass(getApplicationContext(), ImgEditActivity.class);
        intent.setClass(getApplicationContext(), ImgFilterActivity.class);
        intent.putStringArrayListExtra(BridgeCls.EXTRA_EDITIMG_LIST, imgList);
        intent.putExtra(BridgeCls.EXTRA_ACTIVITY_MODE, BridgeCls.ACTIVITY_MODE_PROFILE);
        startActivityForResult(intent, REQUEST_IMAGE_FILTER);
        imgList.clear();
    }

    @Deprecated
    private class AsyncSaveTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(final Void... params) {
            Bitmap image = mImageView.getCroppedBitmap();

            // scale
            image = CropUtils.getScaledBitmap(image, PROFILE_SCALE_SIZE, PROFILE_SCALE_SIZE);

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String filePath = CropUtils.getDirPath(getApplicationContext());
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
            showProgress(ProfileGalleryActivity.this, false);
            startResultActivity(mCropImgList);

        }


    }










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
}

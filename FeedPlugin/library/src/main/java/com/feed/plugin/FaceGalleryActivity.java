package com.feed.plugin;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.feed.plugin.adapter.ImageViewSlideAdapter;
import com.feed.plugin.fragment.gallery.GalleryAdapter;
import com.feed.plugin.fragment.gallery.GalleryManager;
import com.feed.plugin.fragment.gallery.GridDividerDecoration;
import com.feed.plugin.fragment.gallery.OnItemClickListener;
import com.feed.plugin.fragment.gallery.PhotoVO;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.List;

public class FaceGalleryActivity extends AppCompatActivity{
    private GalleryManager mGalleryManager;
    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;
    private ImageView mSelectImageView;

    private String mSelectImgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_face_gallery);

        mSelectImageView = (ImageView)findViewById(R.id.select_image_view);
        recyclerGallery = (RecyclerView) findViewById(R.id.recycler_gallery);

        galleryAdapter = new GalleryAdapter(FaceGalleryActivity.this, initGalleryPathList(), R.layout.gallery_item_photo);
        galleryAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerGallery.setAdapter(galleryAdapter);
        recyclerGallery.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        recyclerGallery.setItemAnimator(new DefaultItemAnimator());
        recyclerGallery.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));

        findViewById(R.id.btn_back).setOnClickListener(mOnClickListener);
        findViewById(R.id.text_ok).setOnClickListener(mOnClickListener);
    }


    /**
     * 갤러리 아미지 데이터 초기화
     */
    private List<PhotoVO> initGalleryPathList() {

        mGalleryManager = new GalleryManager(getApplicationContext());
        //return mGalleryManager.getDatePhotoPathList(2015, 9, 19);
        return mGalleryManager.getAllPhotoPathList();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.btn_back)
            {
                finish();
            }
            else if(v.getId() == R.id.text_ok)
            {
                if(mSelectImgPath != null && mSelectImgPath.length() > 0)
                {
                    Log.d("AAAA", "---------- mSelectImgPath : "+mSelectImgPath);
                    Log.d("AAAA", "---------- mSelectImgPath : "+mSelectImgPath);
                    UnityPlayer.UnitySendMessage("AndroidManager","CallByFacePhoto", mSelectImgPath);
                    //finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position)
        {
            PhotoVO photoVO = galleryAdapter.getmPhotoList().get(position);
            mSelectImgPath = photoVO.getImgPath();

            Uri imgUri = Uri.parse(mSelectImgPath);
            mSelectImageView.setImageURI(imgUri);

            galleryAdapter.getmPhotoList().set(position,photoVO);
            galleryAdapter.notifyDataSetChanged();

        }
    };
}

package com.feed.plugin.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.feed.plugin.R;
import com.feed.plugin.fragment.gallelry.GalleryAdapter;
import com.feed.plugin.fragment.gallelry.GalleryManager;
import com.feed.plugin.fragment.gallelry.GridDividerDecoration;
import com.feed.plugin.fragment.gallelry.OnItemClickListener;
import com.feed.plugin.fragment.gallelry.PhotoVO;

import java.util.List;

public class GalleryFragment extends Fragment{

    private RelativeLayout mRelImgViewer;

    private ImageView mImgSelImg;
    private GalleryManager mGalleryManager;
    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;

    private boolean imgViewState = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRelImgViewer = (RelativeLayout)rootView.findViewById(R.id.relative_imgviewer);
        mImgSelImg = (ImageView)rootView.findViewById(R.id.img_selimg);

        recyclerGallery = (RecyclerView) rootView.findViewById(R.id.recycler_gallery);
        ((Button)rootView.findViewById(R.id.btn_change_imgview_state)).setOnClickListener(mOnClickListenr);
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

        galleryAdapter = new GalleryAdapter(getActivity(), initGalleryPathList(), R.layout.item_photo);
        galleryAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerGallery.setAdapter(galleryAdapter);
        recyclerGallery.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerGallery.setItemAnimator(new DefaultItemAnimator());
        recyclerGallery.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));
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
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            mImgSelImg.setImageBitmap(bitmap);

            galleryAdapter.getmPhotoList().set(position,photoVO);
            galleryAdapter.notifyDataSetChanged();

        }
    };

    private View.OnClickListener mOnClickListenr = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.btn_change_imgview_state)
            {
                if(imgViewState)
                {
                    mRelImgViewer.setVisibility(View.GONE);
                    imgViewState = false;
                }
                else
                {
                    mRelImgViewer.setVisibility(View.VISIBLE);
                    imgViewState = true;
                }
            }
        }
    };

}

package com.feed.plugin.fragment.gallery;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.feed.plugin.R;

import java.util.ArrayList;
import java.util.List;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {
    private Activity mActivity;
    private int itemLayout;
    private List<PhotoVO> mPhotoList;

    private OnItemClickListener onItemClickListener;

    public List<PhotoVO> getmPhotoList() {
        return mPhotoList;
    }

    public List<PhotoVO> getSelectedPhotoList() {
        List<PhotoVO> mSelectPhotoList = new ArrayList<>();
        for (int i = 0; i < mPhotoList.size(); i++) {
            PhotoVO photoVO = mPhotoList.get(i);
            if (photoVO.isSelected()) {
                mSelectPhotoList.add(photoVO);
            }
        }
        return mSelectPhotoList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public GalleryAdapter(Activity activity, List<PhotoVO> photoList, int itemLayout) {

        mActivity = activity;

        this.mPhotoList = photoList;
        this.itemLayout = itemLayout;

    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder viewHolder, final int position) {

        final PhotoVO photoVO = mPhotoList.get(position);

        Glide.with(mActivity)
                .load(photoVO.getImgPath())
                .centerCrop()
                .crossFade()
                .into(viewHolder.imgPhoto);

        //선택
        if (photoVO.isSelected()) {
            viewHolder.layoutSelect.setVisibility(View.VISIBLE);
        } else {
            viewHolder.layoutSelect.setVisibility(View.INVISIBLE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(viewHolder, position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgPhoto;
        public RelativeLayout layoutSelect;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            imgPhoto = (ImageView) itemView.findViewById(R.id.img_photo);
            layoutSelect = (RelativeLayout) itemView.findViewById(R.id.layout_select);
        }

    }
}


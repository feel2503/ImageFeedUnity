package com.feed.plugin.fragment.gallery;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feed.plugin.R;

import java.util.ArrayList;
import java.util.List;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {
    private Activity mActivity;
    private int itemLayout;
    private List<PhotoVO> mPhotoList;

    private boolean isMultiSelect = false;

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

    public void setMultiSelectMode(boolean isMultiSelect)
    {
        this.isMultiSelect = isMultiSelect;
    }

    public boolean getMultiSelect()
    {
        return isMultiSelect;
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
        if(isMultiSelect)
        {
            viewHolder.imgSelect.setVisibility(View.VISIBLE);
            viewHolder.textCount.setVisibility(View.VISIBLE);
            if(photoVO.isSelected())
            {
                viewHolder.imgSelect.setImageResource(R.drawable.ico_select_circle_on);
                viewHolder.textCount.setText(""+photoVO.getSelectCount());
            }
            else
            {
                viewHolder.imgSelect.setImageResource(R.drawable.ico_select_circle_default);
                viewHolder.textCount.setVisibility(View.INVISIBLE);
            }

        }
        else
        {
            viewHolder.imgSelect.setVisibility(View.INVISIBLE);
            viewHolder.textCount.setVisibility(View.INVISIBLE);
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
        public ImageView imgSelect;
        public TextView textCount;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            imgPhoto = (ImageView) itemView.findViewById(R.id.img_photo);
            imgSelect = (ImageView) itemView.findViewById(R.id.image_select_state);
            textCount = (TextView) itemView.findViewById(R.id.text_select_count);
        }

    }
}


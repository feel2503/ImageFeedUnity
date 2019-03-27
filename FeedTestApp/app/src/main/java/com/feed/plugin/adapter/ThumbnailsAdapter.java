package com.feed.plugin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feed.plugin.R;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;

import java.util.List;



/**
 * Created by ravi on 23/10/17.
 */

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.MyViewHolder> {

    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailsAdapterListener listener;
    private Context mContext;
    private int selectedIndex = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //GPUImageView thumbnail;
        ImageView thumbnail;
        TextView filterName;
        Bitmap bitmap;

        public MyViewHolder(View view) {
            super(view);
        }
    }


    public ThumbnailsAdapter(Context context, List<ThumbnailItem> thumbnailItemList, ThumbnailsAdapterListener listener) {
        mContext = context;
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(itemView);
        //viewHolder.thumbnail = (GPUImageView) itemView.findViewById(R.id.gpuimage_thumbnail);
        viewHolder.thumbnail = (ImageView) itemView.findViewById(R.id.gpuimage_thumbnail);
        viewHolder.filterName = (TextView)itemView.findViewById(R.id.filter_name);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);
        //holder.thumbnail.setImage(thumbnailItem.image);
        holder.thumbnail.setImageBitmap(thumbnailItem.image);
        holder.filterName.setText(thumbnailItem.filterName);

        if (selectedIndex == position) {
            holder.filterName.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_selected));
            thumbnailItem.isSelected = true;
        } else {
            holder.filterName.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_normal));
            thumbnailItem.isSelected = false;
        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter, thumbnailItem.isSelected);
                //thumbnailItem.isSelected = true;

                Log.d("AAAA", "isSelected " + thumbnailItem.isSelected);

//                if(!thumbnailItem.isSelected)
//                {
//                    listener.onFilterSelected(thumbnailItem.filter, thumbnailItem.isSelected);
//                    thumbnailItem.isSelected = true;
//
//                    Log.d("AAAA", "isSelected = false");
//                }
//                else
//                {
//                    Log.d("AAAA", "isSelected = true");
//                }
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });



    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public interface ThumbnailsAdapterListener {
        void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect);
    }
}

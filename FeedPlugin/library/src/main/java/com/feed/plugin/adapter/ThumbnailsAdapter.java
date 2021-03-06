package com.feed.plugin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import com.feed.plugin.widget.FilterValueView;

import java.util.ArrayList;
import java.util.List;





public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.MyViewHolder> {

    private ArrayList<ThumbnailItem> thumbnailItemList;
    private FilterSelectListener listener;
    private Context mContext;
    public int selectedIndex = 0;
    private Typeface mTypeface;

    public String mSelectedName = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //GPUImageView thumbnail;
        ImageView thumbnail;
        TextView filterName;
        ImageView filterValueView;

        public MyViewHolder(View view) {
            super(view);
        }
    }


    public ThumbnailsAdapter(Context context, ArrayList<ThumbnailItem> thumbnailItemList, FilterSelectListener listener) {
        mContext = context;
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;

        mTypeface = Typeface.createFromAsset(context.getAssets(), "RingsideWide-Semibold.otf");
    }

    public void setThubmnailItemList(ArrayList<ThumbnailItem> thumbnailItemList)
    {
        this.thumbnailItemList = thumbnailItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(itemView);
        //viewHolder.thumbnail = (GPUImageView) itemView.findViewById(R.id.gpuimage_thumbnail);
        viewHolder.thumbnail = (ImageView) itemView.findViewById(R.id.gpuimage_thumbnail);
        viewHolder.filterName = (TextView)itemView.findViewById(R.id.filter_name);
        viewHolder.filterName.setTypeface(mTypeface);
        viewHolder.filterValueView = (ImageView)itemView.findViewById(R.id.filtervalue_view);


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

        if(thumbnailItem.isSetted)
            holder.filterValueView.setVisibility(View.VISIBLE);
        else
            holder.filterValueView.setVisibility(View.INVISIBLE);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thumbnailItem.filterName.equalsIgnoreCase("Normal") && thumbnailItem.isSelected)
                    return;

                mSelectedName = thumbnailItem.filterName;
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

//    public interface ThumbnailsAdapterListener {
//        void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect);
//    }
}

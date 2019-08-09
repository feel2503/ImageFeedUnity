package com.feed.plugin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feed.plugin.R;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.filter.GPUImageBrightnessFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageContrastFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSaturationFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageSharpenFilter;
import com.feed.plugin.android.gpuimage.filter.GPUImageVignetteFilter;
import com.feed.plugin.util.FilterUtils;

import java.util.ArrayList;


public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.MyViewHolder>
{
    private ArrayList<ThumbnailItem> editItemList;
    private FilterSelectListener listener;
    private Context mContext;
    private int selectedIndex = -1;
    private Typeface mTypeface;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView filterName;
        Bitmap bitmap;
        View seleteState;

        public MyViewHolder(View view) {
            super(view);
        }
    }


    public EditImageAdapter(Context context, FilterSelectListener listener) {
        mContext = context;
        this.listener = listener;

        mTypeface = Typeface.createFromAsset(context.getAssets(), "RingsideWide-Semibold.otf");

        initEditList();
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(itemView);
        //viewHolder.thumbnail = (GPUImageView) itemView.findViewById(R.id.gpuimage_thumbnail);
        viewHolder.thumbnail = (ImageView) itemView.findViewById(R.id.gpuimage_thumbnail);
        viewHolder.filterName = (TextView)itemView.findViewById(R.id.filter_name);
        viewHolder.filterName.setTypeface(mTypeface);
        viewHolder.seleteState = (View)itemView.findViewById(R.id.view_filter_selete);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = editItemList.get(position);
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
            holder.seleteState.setVisibility(View.VISIBLE);
        else
            holder.seleteState.setVisibility(View.INVISIBLE);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter, thumbnailItem.isSelected);
                //thumbnailItem.isSelected = true;
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });



    }

    @Override
    public int getItemCount() {
        return editItemList.size();
    }

    public ArrayList<ThumbnailItem> getEditItemList(){
        return editItemList;
    }

    public void setEditItemList(ArrayList<ThumbnailItem> editItemList){
        this.editItemList = editItemList;
    }

    public void initSelected()
    {
        selectedIndex = -1;
        for(ThumbnailItem item : editItemList)
        {
            item.isSelected = false;
            item.isSetted = false;
        }

        ((GPUImageBrightnessFilter)editItemList.get(0).filter).setBrightness(0.0f);
        ((GPUImageContrastFilter)editItemList.get(1).filter).setContrast(1.0f);
        ((GPUImageSharpenFilter)editItemList.get(2).filter).setSharpness(4.0f);
        ((GPUImageSaturationFilter)editItemList.get(3).filter).setSaturation(1.0f);
        ((GPUImageVignetteFilter)editItemList.get(4).filter).setVignetteStart(1.0f);

    }

    private void initEditList()
    {
        editItemList = new ArrayList<ThumbnailItem>();

        ThumbnailItem itemBrightness = new ThumbnailItem();
        itemBrightness.image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_brightness );
        itemBrightness.filterName = mContext.getString(R.string.Brightness);
        itemBrightness.filter = FilterUtils.createFilterForType(mContext, FilterUtils.FilterType.BRIGHTNESS);
        editItemList.add(itemBrightness);

        ThumbnailItem itemContrast = new ThumbnailItem();
        itemContrast.image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_contrast );
        itemContrast.filterName = mContext.getString(R.string.Contrast);
        itemContrast.filter = FilterUtils.createFilterForType(mContext, FilterUtils.FilterType.CONTRAST);
        editItemList.add(itemContrast);

        ThumbnailItem itemSharpen = new ThumbnailItem();
        itemSharpen.image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_sharpen );
        itemSharpen.filterName = mContext.getString(R.string.Sharpen);
        itemSharpen.filter = FilterUtils.createFilterForType(mContext, FilterUtils.FilterType.SHARPEN);
        editItemList.add(itemSharpen);

        ThumbnailItem itemSaturation = new ThumbnailItem();
        itemSaturation.image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_saturation_copy );
        itemSaturation.filterName = mContext.getString(R.string.Saturation);
        itemSaturation.filter = FilterUtils.createFilterForType(mContext, FilterUtils.FilterType.SATURATION);
        editItemList.add(itemSaturation);

        ThumbnailItem itemVignette = new ThumbnailItem();
        itemVignette.image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_vignette_copy );
        itemVignette.filterName = mContext.getString(R.string.Vignette);
        itemVignette.filter = FilterUtils.createFilterForType(mContext, FilterUtils.FilterType.VIGNETTE);
        editItemList.add(itemVignette);

    }
}

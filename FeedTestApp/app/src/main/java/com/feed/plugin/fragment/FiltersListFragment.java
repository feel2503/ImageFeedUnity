package com.feed.plugin.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.feed.plugin.R;
import com.feed.plugin.adapter.ThumbnailsAdapter;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.util.BitmapUtils;
import com.feed.plugin.util.FilterUtils;
import com.feed.plugin.util.SpacesItemDecoration;


import java.util.ArrayList;
import java.util.List;



public class FiltersListFragment extends Fragment implements ThumbnailsAdapter.ThumbnailsAdapterListener {

    private RecyclerView mRecyclerView;
    private RelativeLayout mRelativeSeekbar;
    private ThumbnailsAdapter mAdapter;
    private List<ThumbnailItem> thumbnailItemList;

    private FiltersListFragmentListener listener;

    private ArrayList<String> mImagList;

    private int maxWidth = 120;
    private int maxHeight = 120;

    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filters_list, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        mRelativeSeekbar = (RelativeLayout)view.findViewById(R.id.relative_SeekBar);

        thumbnailItemList = new ArrayList<>();
        mAdapter = new ThumbnailsAdapter(getActivity(), thumbnailItemList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(space));
        mRecyclerView.setAdapter(mAdapter);

        //prepareThumbnail(null);
        //initFilterList();
        AsyncCreateFiter async = new AsyncCreateFiter();
        async.execute();

        return view;
    }

    public void setImageList(ArrayList<String> listImg)
    {
        mImagList = listImg;
    }

    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    class AsyncCreateFiter extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids){

            Bitmap bitmap = BitmapFactory.decodeFile(mImagList.get(0));
            bitmap = scaleBitmap(bitmap);

            thumbnailItemList.clear();

            // add normal bitmap first
            ThumbnailItem thumbnailItem = new ThumbnailItem();
            thumbnailItem.image = bitmap;
            thumbnailItem.filterName = "Normal";

            thumbnailItemList.add(thumbnailItem);

            GPUImage gpumage = new GPUImage(getActivity());
            //gpumage.setImage(bitmap);


            ArrayList<FilterUtils.FilterType> filterTypes = FilterUtils.getFilterTypeList();
            for(FilterUtils.FilterType type: filterTypes)
            {
                GPUImageFilter filter = FilterUtils.createFilterForType(getActivity(), type);
                gpumage.setFilter(filter);
                //gpumage.requestRender();
                Bitmap filterBitmp = gpumage.getBitmapWithFilterApplied(bitmap);
                //Bitmap filterBitmp = gpumage.getBitmapWithFilterApplied(bitmap, true);    // 앱 크레쉬 발생

                ThumbnailItem ti = new ThumbnailItem();
                ti.image = filterBitmp;
                ti.filter = filter;
                ti.filterName = FilterUtils.getFilterName(type);

                thumbnailItemList.add(ti);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }




    @Override
    public void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect) {
        if (listener != null)
        {
            if(!isSecondSelect)
            {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRelativeSeekbar.setVisibility(View.INVISIBLE);
                listener.onFilterSelected(filter);
            }

            else
            {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mRelativeSeekbar.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface FiltersListFragmentListener {
        void onFilterSelected(GPUImageFilter filter);
    }
}
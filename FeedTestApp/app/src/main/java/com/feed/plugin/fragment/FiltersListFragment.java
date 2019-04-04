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
import com.feed.plugin.adapter.FilterSelectListener;
import com.feed.plugin.adapter.ThumbnailsAdapter;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImage;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.util.FilterUtils;
import com.feed.plugin.util.SpacesItemDecoration;

import java.util.ArrayList;



public class FiltersListFragment extends Fragment implements FilterSelectListener{

    private RecyclerView mRecyclerView;
    private ThumbnailsAdapter mAdapter;


    private ArrayList<ThumbnailItem> thumbnailItemList;

    private FiltersListSelectListener listener;

    private ArrayList<String> mImagList;
    private String mImgPath;

    private int maxWidth = 120;
    private int maxHeight = 120;

    public FiltersListFragment() {
        // Required empty public constructor
    }

    public void setListener(FiltersListSelectListener listener) {
        this.listener = listener;
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

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_filter_view);

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
        async.execute(mImgPath);


        return view;
    }

    public void setImageList(ArrayList<String> listImg)
    {
        mImagList = listImg;
    }

    public ArrayList<ThumbnailItem> getThumbnailItemList(){
        return thumbnailItemList;
    }

    public void setThumbnailItemList(ArrayList<ThumbnailItem> thumbnailItemList){
        this.thumbnailItemList = thumbnailItemList;
    }

    public void setImagePath(String path)
    {
        mImgPath = path;
    }

    public void updateThumbnail(String path)
    {
        AsyncCreateFiter async = new AsyncCreateFiter();
        async.execute(path);
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

    class AsyncCreateFiter extends AsyncTask<String, Void, ArrayList<ThumbnailItem>>
    {
        @Override
        protected ArrayList<ThumbnailItem> doInBackground(String... params)
        {
            ArrayList<ThumbnailItem> result = new ArrayList<ThumbnailItem>();
            String path = params[0];;
//            if(params != null && params.length > 0)
//                path = params[0];

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmap = scaleBitmap(bitmap);

            result.clear();

            // add normal bitmap first
            ThumbnailItem thumbnailItem = new ThumbnailItem();
            thumbnailItem.image = bitmap;
            thumbnailItem.filterName = "Normal";

            result.add(thumbnailItem);

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

                result.add(ti);
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<ThumbnailItem> items){
            super.onPostExecute(items);
            mAdapter.setThubmnailItemList(items);
            thumbnailItemList = items;
            mAdapter.notifyDataSetChanged();
        }
    }




    @Override
    public void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect) {
        if (listener != null)
        {
            listener.onFilterSelected(filter, isSecondSelect);

//            if(!isSecondSelect)
//            {
//                mRecyclerView.setVisibility(View.VISIBLE);
//                mRelativeSeekbar.setVisibility(View.INVISIBLE);
//                listener.onFilterSelected(filter, isSecondSelect);
//            }
//
//            else
//            {
//                mRecyclerView.setVisibility(View.INVISIBLE);
//                mRelativeSeekbar.setVisibility(View.VISIBLE);
//            }
        }
    }

//    public interface FiltersListFragmentListener {
//        void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect);
//    }
}
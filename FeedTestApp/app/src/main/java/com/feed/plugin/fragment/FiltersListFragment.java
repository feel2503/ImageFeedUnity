package com.feed.plugin.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feed.plugin.R;
import com.feed.plugin.adapter.ThumbnailsAdapter;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.GPUImageView;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.util.BitmapUtils;
import com.feed.plugin.util.FilterUtils;
import com.feed.plugin.util.SpacesItemDecoration;
import com.feed.plugin.util.ThumbnailsManager;


import java.util.ArrayList;
import java.util.List;



public class FiltersListFragment extends Fragment implements ThumbnailsAdapter.ThumbnailsAdapterListener {

    private RecyclerView mRecyclerView;
    private ThumbnailsAdapter mAdapter;
    private List<ThumbnailItem> thumbnailItemList;

    private FiltersListFragmentListener listener;

    private ArrayList<String> mImagList;

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
        thumbnailItemList = new ArrayList<>();
        mAdapter = new ThumbnailsAdapter(getActivity(), thumbnailItemList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(space));
        mRecyclerView.setAdapter(mAdapter);

        prepareThumbnail(null);

        return view;
    }

    public void setImageList(ArrayList<String> listImg)
    {
        mImagList = listImg;
    }
    /**
     * Renders thumbnails in horizontal list
     * loads default image from Assets if passed param is null
     *
     * @param bitmap
     */
    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage;

                if (bitmap == null) {
                    //thumbImage = BitmapUtils.getBitmapFromAssets(getActivity(), MainActivity.IMAGE_NAME, 100, 100);
                    //thumbImage = BitmapUtils.getBitmapFromAssets(getActivity(), mImagList.get(0), 100, 100);
                    thumbImage = BitmapFactory.decodeFile(mImagList.get(0));
                } else {
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }

                if (thumbImage == null)
                    return;

                ThumbnailsManager.clearThumbs();
                thumbnailItemList.clear();

                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = "Normal";
                //thumbnailItem.gpuImageView = new GPUImageView(getActivity());

                ThumbnailsManager.addThumb(thumbnailItem);

                ArrayList<FilterUtils.FilterType> filterTypes = FilterUtils.getFilterTypeList();
                for(FilterUtils.FilterType type: filterTypes)
                {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImage;
                    tI.filter = FilterUtils.createFilterForType(getActivity(), type);
                    tI.filterName = FilterUtils.getFilterName(type);
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(GPUImageFilter filter) {
        if (listener != null)
            listener.onFilterSelected(filter);
    }

    public interface FiltersListFragmentListener {
        void onFilterSelected(GPUImageFilter filter);
    }
}
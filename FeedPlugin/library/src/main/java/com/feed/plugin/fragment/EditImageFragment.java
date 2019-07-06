package com.feed.plugin.fragment;

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
import com.feed.plugin.adapter.EditImageAdapter;
import com.feed.plugin.adapter.FilterSelectListener;
import com.feed.plugin.adapter.ThumbnailsAdapter;
import com.feed.plugin.adapter.items.ThumbnailItem;
import com.feed.plugin.android.gpuimage.filter.GPUImageFilter;
import com.feed.plugin.util.SpacesItemDecoration;

import java.util.ArrayList;


public class EditImageFragment extends Fragment implements FilterSelectListener{

    private RecyclerView mRecyclerView;
    private EditImageAdapter mAdapter;
    private FiltersListSelectListener listener;


    public EditImageFragment() {
        // Required empty public constructor
    }


    public void setListener(FiltersListSelectListener listener) {
        this.listener = listener;
    }

    public ArrayList<ThumbnailItem> getEditItemList(){
        return mAdapter.getEditItemList();
    }

    public void initSelected()
    {
        mAdapter.initSelected();
        mAdapter.notifyDataSetChanged();
    }

    public void setEditItemList(ArrayList<ThumbnailItem> editItemList){
        mAdapter.setEditItemList(editItemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_edit_view);

        mAdapter = new EditImageAdapter(getActivity(), this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(space));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onFilterSelected(GPUImageFilter filter, boolean isSecondSelect)
    {
        if (listener != null)
        {
            listener.onFilterSelected(filter, isSecondSelect);
        }
    }
}

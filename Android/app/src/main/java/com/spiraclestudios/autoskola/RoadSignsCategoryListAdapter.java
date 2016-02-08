/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 15/10/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spiraclestudios.autoskola.activities.RoadSignsListActivity;
import com.spiraclestudios.autoskola.fragments.RoadSignsDetailFragment;
import com.spiraclestudios.autoskola.fragments.RoadSignsListFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import timber.log.Timber;

public class RoadSignsCategoryListAdapter extends RecyclerView.Adapter<RoadSignsCategoryListAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<RoadSignsCategoryListEntry> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public IViewOnClickListener mListener;

        public TextView category_name;
        public ImageView category_image;

        public ViewHolder(View view, IViewOnClickListener listener) {
            super(view);
            mListener = listener;
            category_name = (TextView) view.findViewById(R.id.category_name);
            category_image = (ImageView) view.findViewById(R.id.category_image);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(view);
        }

        public interface IViewOnClickListener {
            void onItemClick(View view);
        }
    }

    public RoadSignsCategoryListAdapter(ArrayList<RoadSignsCategoryListEntry> dataset) {
        mDataSet = dataset;
    }

    @Override
    public RoadSignsCategoryListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.road_signs_category_list_entry, parent, false);

        return new ViewHolder(view, new ViewHolder.IViewOnClickListener() {
            public void onItemClick(View view1) {
                RoadSignsCategoryListEntry entry = mDataSet.get(
                        ((RecyclerView) parent.findViewById(R.id.recycler_view))
                        .getChildAdapterPosition(view1));

                String category = entry.getCategory();
                String categoryName = entry.getCategoryName();

                Intent intent = new Intent(mContext, RoadSignsListActivity.class);
                intent.putExtra(RoadSignsListFragment.ARG_ROAD_SIGN_CATEGORY, category);
                intent.putExtra(RoadSignsListFragment.ARG_ROAD_SIGN_CATEGORY_NAME, categoryName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RoadSignsCategoryListEntry item = getItem(position);
        Drawable categoryImage;

        try {
            InputStream inputStream = mContext.getAssets()
                    .open("images/" + item.getImagePath() + ".png");
            categoryImage = Drawable.createFromStream(inputStream, null);
        } catch (IOException ex) {
            // If file doesn't exist, use the placeholder image.
            categoryImage = ContextCompat.getDrawable(mContext,
                    R.drawable.placeholder_small);
            Timber.d("Image \"images/%s.png\" does not exist.", item.getImagePath());
        }

        holder.category_name.setText(item.getCategoryName());
        holder.category_image.setImageDrawable(categoryImage);
    }

    public void addItem(RoadSignsCategoryListEntry dataObj, int index) {
        mDataSet.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }

    public RoadSignsCategoryListEntry getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
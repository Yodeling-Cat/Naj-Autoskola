/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 15/10/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spiraclestudios.autoskola.activities.RoadSignsDetailActivity;
import com.spiraclestudios.autoskola.fragments.RoadSignsDetailFragment;

import java.util.ArrayList;

public class RoadSignsListAdapter extends RecyclerView.Adapter<RoadSignsListAdapter.ViewHolder> {

    private ArrayList<RoadSignsListEntry> mDataSet;

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

    public RoadSignsListAdapter(ArrayList<RoadSignsListEntry> dataset) {
        mDataSet = dataset;
    }

    @Override
    public RoadSignsListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.road_signs_list_entry, parent, false);

        return new ViewHolder(view, new ViewHolder.IViewOnClickListener() {
            public void onItemClick(View view1) {
                String category = mDataSet.get(((RecyclerView) parent.findViewById(R.id.recycler_view))
                        .getChildAdapterPosition(view1)).getCategory();
                String categoryName = mDataSet.get(((RecyclerView) parent.findViewById(R.id.recycler_view))
                        .getChildAdapterPosition(view1)).getCategoryName();

                Toast.makeText(context, "Clicked on " + category, Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(context, RoadSignsDetailActivity.class);
                detailIntent.putExtra(RoadSignsDetailFragment.ARG_CATEGORY, category);
                detailIntent.putExtra(RoadSignsDetailFragment.ARG_CATEGORY_NAME, categoryName);
                context.startActivity(detailIntent);
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.category_name.setText(getItem(position).getCategoryName());
        //holder.category_image.setImageDrawable();
    }

    public void addItem(RoadSignsListEntry dataObj, int index) {
        mDataSet.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }

    public RoadSignsListEntry getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
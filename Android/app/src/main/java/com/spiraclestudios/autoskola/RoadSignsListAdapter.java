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

import com.spiraclestudios.autoskola.activities.RoadSignsDetailActivity;
import com.spiraclestudios.autoskola.fragments.RoadSignsDetailFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import timber.log.Timber;

public class RoadSignsListAdapter extends RecyclerView.Adapter<RoadSignsListAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<RoadSignsListEntry> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public IViewOnClickListener mListener;

        public TextView road_sign_name;
        public TextView road_sign_desc;
        public ImageView road_sign_image;

        public ViewHolder(View view, IViewOnClickListener listener) {
            super(view);
            mListener = listener;
            road_sign_name = (TextView) view.findViewById(R.id.road_sign_name);
            road_sign_desc = (TextView) view.findViewById(R.id.road_sign_desc);
            road_sign_image = (ImageView) view.findViewById(R.id.road_sign_image);

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
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.road_signs_list_entry, parent, false);

        return new ViewHolder(view, new ViewHolder.IViewOnClickListener() {
            public void onItemClick(View view1) {
                String roadSignName = mDataSet.get(((RecyclerView) parent.findViewById(R.id.recycler_view))
                        .getChildAdapterPosition(view1)).getRoadSignName();

                String roadSignDesc = mDataSet.get(((RecyclerView) parent.findViewById(R.id.recycler_view))
                        .getChildAdapterPosition(view1)).getRoadSignDesc();

                String imagePath = mDataSet.get(((RecyclerView) parent.findViewById(R.id.recycler_view))
                        .getChildAdapterPosition(view1)).getImagePath();

                Intent intent = new Intent(mContext, RoadSignsDetailActivity.class);
                intent.putExtra(RoadSignsDetailFragment.ARG_ROAD_SIGN_NAME, roadSignName);
                intent.putExtra(RoadSignsDetailFragment.ARG_ROAD_SIGN_DESC, roadSignDesc);
                intent.putExtra(RoadSignsDetailFragment.ARG_ROAD_SIGN_IMAGE_PATH, imagePath);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RoadSignsListEntry item = getItem(position);
        Drawable roadSignImage;

        try {
            InputStream inputStream = mContext.getAssets()
                    .open("images/road_signs/" + item.getImagePath() + ".png");
            roadSignImage = Drawable.createFromStream(inputStream, null);
        } catch (IOException ex) {
            // If file doesn't exist, use the placeholder image.
            roadSignImage = ContextCompat.getDrawable(mContext,
                    R.drawable.placeholder_small);
            Timber.d("Image \"images/road_signs/%s.png\" does not exist.", item.getImagePath());
        }

        holder.road_sign_name.setText(item.getRoadSignName());
        holder.road_sign_desc.setText(item.getRoadSignDesc());
        holder.road_sign_image.setImageDrawable(roadSignImage);
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
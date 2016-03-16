/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.items;

/**
 * Added by benji on 15/10/2015.
 */

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.activities.RoadSignsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RoadSignsItem
        extends AbstractItem<RoadSignsItem, RoadSignsItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public String roadSignName;
    /** Used just to pass it to the details activity. */
    public String roadSignDesc;
    public String imagePath;
    public boolean isGridView;

    public RoadSignsItem(String roadSignName, String roadSignDesc, String imagePath, boolean isGridView) {
        this.roadSignName = roadSignName;
        this.roadSignDesc = roadSignDesc;
        this.imagePath = imagePath;
        this.isGridView = isGridView;
    }

    /** The unique ID for this type of item */
    @Override
    public int getType() {
        return R.id.road_signs_list_item_id;
    }

    /** The layout to be used for this type of item */
    @Override
    public int getLayoutRes() {
        return isGridView ? R.layout.item_road_signs_grid : R.layout.item_road_signs;
    }

    /** The logic to bind your data to the view */
    @Override
    public void bindView(ViewHolder viewHolder) {
        // Call super so the selection is already handled for you.
        super.bindView(viewHolder);
        Context context = viewHolder.itemView.getContext();

        Glide.with((RoadSignsActivity) context)
                .load(Uri.parse("file:///android_asset/images/road_signs/" + imagePath + ".png"))
                //.placeholder(R.drawable.placeholder_small)
                .into(viewHolder.road_sign_image);

        if (!isGridView && viewHolder.road_sign_name != null)
            viewHolder.road_sign_name.setText(roadSignName);
    }

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for
     * ViewHolder
     * creation,
     * and it is also many many times more efficient if you define custom listeners on views within
     * your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {

        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    /**
     * The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so
     * scrolling is blazing fast
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout view;
        @Nullable
        @Bind(R.id.road_sign_name)
        protected TextView road_sign_name;
        @Bind(R.id.road_sign_image)
        protected ImageView road_sign_image;

        public ViewHolder(View view) {
            super(view);
            this.view = (LinearLayout) view;
            ButterKnife.bind(this, view);

            // Optimization to preset the correct height for our device.
            // NOTE: Came with the sample, I don't actually know what it does.
            /*int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
            int finalHeight = (int) (screenWidth / 1.5) / 2;
            road_sign_image.setMinimumHeight(finalHeight);
            road_sign_image.setMaxHeight(finalHeight);
            road_sign_image.setAdjustViewBounds(false);
            // Set height as layoutParameter too.
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) road_sign_image.getLayoutParams();
            lp.height = finalHeight;
            road_sign_image.setLayoutParams(lp);*/
        }
    }
}
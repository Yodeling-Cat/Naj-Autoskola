/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 06/03/2016.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RoadSignsCategoryListItem
        extends AbstractItem<RoadSignsCategoryListItem, RoadSignsCategoryListItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public String category;
    public String categoryName;
    public String imagePath;

    public RoadSignsCategoryListItem(String category, String categoryName, String imagePath) {
        this.category = category;
        this.categoryName = categoryName;
        this.imagePath = imagePath;
    }

    /** The unique ID for this type of item */
    @Override
    public int getType() {
        return R.id.road_signs_category_item_id;
    }

    /** The layout to be used for this type of item */
    @Override
    public int getLayoutRes() {
        return R.layout.item_road_signs_category;
    }

    /** The logic to bind your data to the view */
    @Override
    public void bindView(ViewHolder viewHolder) {
        // Call super so the selection is already handled for you.
        super.bindView(viewHolder);

        Context context = viewHolder.itemView.getContext();

        Drawable categoryImage;
        try {
            InputStream inputStream = context.getAssets().open("images/road_signs/" + imagePath + ".png");
            categoryImage = Drawable.createFromStream(inputStream, null);
        } catch (IOException ex) {
            // If file doesn't exist, use the placeholder image.
            categoryImage = ContextCompat.getDrawable(context, R.drawable.placeholder_small);
            Timber.d("Image \"images/road_signs/%s.png\" does not exist.", imagePath);
        }

        viewHolder.category_name.setText(categoryName);
        viewHolder.category_image.setImageDrawable(categoryImage);
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
        @Bind(R.id.category_name)
        protected TextView category_name;
        @Bind(R.id.category_image)
        protected ImageView category_image;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = (LinearLayout) view;

            // Optimization to preset the correct height for our device.
            // NOTE: Came with the sample, I don't actually know what it does.
            int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
            int finalHeight = (int) (screenWidth / 1.5) / 2;
            category_image.setMinimumHeight(finalHeight);
            category_image.setMaxHeight(finalHeight);
            category_image.setAdjustViewBounds(false);
            // Set height as layoutParameter too.
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) category_image.getLayoutParams();
            lp.height = finalHeight;
            category_image.setLayoutParams(lp);
        }
    }
}
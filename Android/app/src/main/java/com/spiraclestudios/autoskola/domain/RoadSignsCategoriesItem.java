/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.domain;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.presentation.ui.activities.RoadSignsCategoriesActivity;
import java.util.List;

/**
 * Added by benji on 06/03/2016.
 */
public class RoadSignsCategoriesItem
    extends AbstractItem<RoadSignsCategoriesItem, RoadSignsCategoriesItem.ViewHolder> {

  public String category;
  public String categoryName;
  public String imagePath;

  public RoadSignsCategoriesItem(String category, String categoryName, String imagePath) {
    this.category = category;
    this.categoryName = categoryName;
    this.imagePath = imagePath;
  }

  @Override public int getType() {
    return R.id.road_signs_category__list_entry;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_road_signs_category;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    Context context = holder.itemView.getContext();

    Glide.with((RoadSignsCategoriesActivity) context)
        .load(Uri.parse("file:///android_asset/images/road_signs/" + imagePath + ".png"))
        .into(holder.category_image);

    holder.category_name.setText(categoryName);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    Glide.clear(holder.category_image);
    holder.category_name.setText(null);
    holder.category_image.setImageDrawable(null);
  }

  @Override public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout view;
    @BindView(R.id.category_name) protected TextView category_name;
    @BindView(R.id.category_image) protected ImageView category_image;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      this.view = (LinearLayout) view;

      // Optimization to preset the correct height for our device.
      // NOTE: Came with the sample, I don't actually know what it does.
      /*int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
      int finalHeight = (int) (screenWidth / 1.5) / 2;
      category_image.setMinimumHeight(finalHeight);
      category_image.setMaxHeight(finalHeight);
      category_image.setAdjustViewBounds(false);
      // Set height as layoutParameter too.
      LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) category_image.getLayoutParams();
      lp.height = finalHeight;
      category_image.setLayoutParams(lp);*/
    }
  }
}
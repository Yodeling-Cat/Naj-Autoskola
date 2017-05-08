/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.domain;

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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.presentation.ui.activities.RoadSignsActivity;
import java.util.List;

public class RoadSignsItem extends AbstractItem<RoadSignsItem, RoadSignsItem.ViewHolder> {

  public String roadSignName;
  /** Used just to pass it to the details activity. */
  public String roadSignDesc;
  public String imagePath;
  public boolean isGridView;

  public RoadSignsItem(String roadSignName, String roadSignDesc, String imagePath,
      boolean isGridView) {
    this.roadSignName = roadSignName;
    this.roadSignDesc = roadSignDesc;
    this.imagePath = imagePath;
    this.isGridView = isGridView;
  }

  @Override public int getType() {
    return R.id.road_signs__list_entry;
  }

  @Override public int getLayoutRes() {
    return isGridView ? R.layout.item_road_signs_grid : R.layout.item_road_signs;
  }

  @Override public void bindView(ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    Context context = holder.itemView.getContext();

    Glide.with((RoadSignsActivity) context)
        .load(Uri.parse("file:///android_asset/images/road_signs/" + imagePath + ".png"))
        .into(holder.road_sign_image);

    if (!isGridView && holder.road_sign_name != null) {
      holder.road_sign_name.setText(roadSignName);
    }
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    Glide.clear(holder.road_sign_image);
    if (holder.road_sign_name != null) {
      holder.road_sign_name.setText(null);
    }
    holder.road_sign_image.setImageDrawable(null);
  }

  @Override public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout view;
    @Nullable @BindView(R.id.road_sign_name) protected TextView road_sign_name;
    @BindView(R.id.road_sign_image) protected ImageView road_sign_image;

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
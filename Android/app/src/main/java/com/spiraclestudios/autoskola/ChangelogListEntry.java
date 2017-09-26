// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.domain.Changelog;
import java.util.List;

import static com.spiraclestudios.autoskola.framework.platform.AttributeResolver.resolveColorAttr;

public class ChangelogListEntry
    extends AbstractItem<ChangelogListEntry, ChangelogListEntry.ViewHolder> {

  private Context ctx;
  private final Changelog changelog;

  public ChangelogListEntry(Changelog changelog) {
    this.changelog = changelog;
  }

  @Override public int getType() {
    return R.id.changelog__list__entry;
  }

  @Override public int getLayoutRes() {
    return R.layout.changelog__list__entry;
  }

  @Override public void bindView(final ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    ctx = holder.itemView.getContext();

    holder.typeIndicator.setBackgroundColor(getColorForChangelogType());
    holder.changelog.setText(changelog.getDescription());
  }

  @ColorInt private int getColorForChangelogType() {
    @AttrRes int colorAttr = -1;
    switch (changelog.getType()) {
      case ADDED:
        colorAttr = R.attr.changelogAddedColor;
        break;
      case REMOVED:
        colorAttr = R.attr.changelogRemovedColor;
        break;
      case CHANGED:
        colorAttr = R.attr.changelogChangedColor;
        break;
      case FIXED:
        colorAttr = R.attr.changelogFixedColor;
        break;
    }
    return resolveColorAttr(ctx, colorAttr);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.changelog.setText(null);
  }

  @Override public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.type_indicator) View typeIndicator;
    @BindView(R.id.changelog) TextView changelog;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}

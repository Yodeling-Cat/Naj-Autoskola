// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;

public class ChangelogListHeader
    extends AbstractItem<ChangelogListHeader, ChangelogListHeader.ViewHolder> {

  private String headerText;

  @Override public int getType() {
    return R.id.changelog__list__header;
  }

  @Override public int getLayoutRes() {
    return R.layout.changelog__list__header;
  }

  @Override public void bindView(final ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    holder.header.setText(headerText);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.header.setText(null);
  }

  @Override public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.changelog_header) TextView header;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public void setHeaderText(String headerText) {
    this.headerText = headerText;
  }
}

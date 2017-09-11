// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.presentation.ui.activities.HistoryActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.TestActivity;
import java.util.List;

public class ChangelogListEntry extends AbstractItem<ChangelogListEntry, ChangelogListEntry.ViewHolder> {

  private Spanned headerText;
  private Spanned changelogText;

  @Override public int getType() {
    return R.id.changelog__list_entry;
  }

  @Override public int getLayoutRes() {
    return R.layout.changelog__list_entry;
  }

  @Override public void bindView(final ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);

    holder.header.setText(headerText);
    holder.changelog.setText(changelogText);
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.header.setText(null);
    holder.changelog.setText(null);
  }

  @Override public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.changelog_header) TextView header;
    @BindView(R.id.changelog) TextView changelog;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public void setHeaderText(String headerText) {
    this.headerText = Html.fromHtml(headerText);
  }

  public void setChangelogText(String changelogText) {
    this.changelogText = Html.fromHtml(changelogText);
  }
}

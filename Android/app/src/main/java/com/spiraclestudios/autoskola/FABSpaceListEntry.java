// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Added by benji on 15/10/2015.
 */
public class FABSpaceListEntry
    extends AbstractItem<FABSpaceListEntry, FABSpaceListEntry.ViewHolder> {

  @Override public int getType() {
    return R.id.fab_space__list_entry;
  }

  @Override public int getLayoutRes() {
    return R.layout.fab_space__list_entry;
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View view) {
      super(view);
    }
  }
}

// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Added by benji on 15/10/2015.
 */
public class TestListEntry extends AbstractItem<TestListEntry, TestListEntry.ViewHolder> {

  public int index;
  public int timesCompleted;

  @Override public int getType() {
    return R.id.test__list_entry;
  }

  @Override public int getLayoutRes() {
    return R.layout.test__list_entry;
  }

  @Override public void bindView(final ViewHolder viewHolder) {
    super.bindView(viewHolder);
    Context ctx = viewHolder.itemView.getContext();

    viewHolder.testIndex.setText(Integer.toString(index));
    viewHolder.timesCompleted.setText(
        ctx.getString(R.string.tests_list__text__completed, timesCompleted));
    viewHolder.overflowButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(viewHolder.overflowButton.getContext(), "Clicked overflow button",
            Toast.LENGTH_SHORT).show();
      }
    });
  }
  // TODO: No more unBind() method?

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    protected TextView testIndex;
    protected TextView timesCompleted;
    protected ImageButton overflowButton;

    public ViewHolder(View view) {
      super(view);
      this.testIndex = (TextView) view.findViewById(R.id.test_index);
      this.timesCompleted = (TextView) view.findViewById(R.id.times_completed);
      this.overflowButton = (ImageButton) view.findViewById(R.id.overflow_button);
    }
  }
}

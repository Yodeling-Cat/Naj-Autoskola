// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.spiraclestudios.autoskola.presentation.ui.activities.HistoryActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.TestActivity;

/**
 * Added by benji on 15/10/2015.
 */
public class TestListEntry extends AbstractItem<TestListEntry, TestListEntry.ViewHolder> {

  private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

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
    final Context ctx = viewHolder.itemView.getContext();

    viewHolder.testIndex.setText(Integer.toString(index));
    viewHolder.timesCompleted.setText(
        ctx.getString(R.string.tests_list__text__completed, timesCompleted));


    viewHolder.overflowButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(final View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override public boolean onMenuItemClick(MenuItem item) {
            Intent intent;

            /*switch (item.getItemId()) {
              case R.id.item__correct_answers:
                intent = new Intent(ctx, TestActivity.class);
                intent.putExtra(TestActivity.EXTRA_TEST_TYPE,
                    TestActivity.TestTypes.CORRECT_ANSWERS);
                intent.putExtra(TestActivity.EXTRA_TEST_ID, entry.getIndex());
                view.getContext().startActivity(intent);
                return true;

              case R.id.item__history:
                if (entry.getTimesCompleted() > 0) {
                  intent = new Intent(ctx, HistoryActivity.class);
                  intent.putExtra(HistoryActivity.EXTRA_TEST_ID, entry.getIndex());
                  ctx.startActivity(intent);
                } else {
                  Toast.makeText(ctx, R.string.history__toast__history_is_empty, Toast.LENGTH_SHORT)
                      .show();
                }
                return true;
            }*/
            return true;
          }
        });

        popupMenu.inflate(R.menu.list__tests);
        popupMenu.show();
      }
    });
  }

  /**
   * our ItemFactory implementation which creates the ViewHolder for our adapter.
   * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder
   * creation,
   * and it is also many many times more efficient if you define custom listeners on views within
   * your item.
   */
  protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
    public ViewHolder create(View v) {
      return new ViewHolder(v);
    }
  }

  @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
    return FACTORY;
  }

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

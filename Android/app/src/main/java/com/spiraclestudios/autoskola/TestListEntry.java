// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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

import static com.spiraclestudios.autoskola.framework.platform.AttributeResolver.resolveColorAttr;

/**
 * Added by benji on 15/10/2015.
 */
public class TestListEntry extends AbstractItem<TestListEntry, TestListEntry.ViewHolder> {

  public int index;
  public int timesCompleted;
  public int mostPoints;

  @Override public int getType() {
    return R.id.test__list_entry;
  }

  @Override public int getLayoutRes() {
    return R.layout.test__list_entry;
  }

  @Override public void bindView(final ViewHolder holder, List<Object> payloads) {
    super.bindView(holder, payloads);
    final Context ctx = holder.itemView.getContext();

    @ColorInt int testIndexTextColor;
    @ColorInt int testInfoTextColor;
    boolean wasSuccessful = Utils.getTestSuccessful(mostPoints);
    if (wasSuccessful) {
      testIndexTextColor = resolveColorAttr(ctx, R.attr.positiveColor);
      testInfoTextColor = testIndexTextColor;
    } else {
      testIndexTextColor = resolveColorAttr(ctx, android.R.attr.textColorPrimary);
      testInfoTextColor = resolveColorAttr(ctx, android.R.attr.textColorSecondary);
    }
    holder.testIndex.setTextColor(testIndexTextColor);
    holder.mostPoints.setTextColor(testInfoTextColor);
    holder.timesCompleted.setTextColor(testInfoTextColor);

    holder.testIndex.setText(Integer.toString(index));
    holder.timesCompleted.setText(
        ctx.getString(R.string.tests_list__text__times_completed, timesCompleted));

    holder.mostPoints.setText(ctx.getString(R.string.tests_list__text__most_points, mostPoints));

    holder.overflowButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(final View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.RIGHT);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override public boolean onMenuItemClick(MenuItem item) {
            Intent intent;

            switch (item.getItemId()) {
              case R.id.item__correct_answers:
                intent = new Intent(ctx, TestActivity.class);
                intent.putExtra(TestActivity.EXTRA_TEST_TYPE,
                    TestActivity.TestTypes.CORRECT_ANSWERS);
                intent.putExtra(TestActivity.EXTRA_TEST_ID, index);
                view.getContext().startActivity(intent);
                return true;

              case R.id.item__history:
                if (timesCompleted > 0) {
                  intent = new Intent(ctx, HistoryActivity.class);
                  intent.putExtra(HistoryActivity.EXTRA_TEST_ID, index);
                  ctx.startActivity(intent);
                } else {
                  Toast.makeText(ctx, R.string.history__toast__history_is_empty, Toast.LENGTH_SHORT)
                      .show();
                }
                return true;
            }
            return true;
          }
        });

        popupMenu.inflate(R.menu.list__tests);
        popupMenu.show();
      }
    });
  }

  @Override public void unbindView(ViewHolder holder) {
    super.unbindView(holder);
    holder.testIndex.setText(null);
    holder.timesCompleted.setText(null);
    holder.mostPoints.setText(null);
    holder.overflowButton.setOnClickListener(null);
  }

  @Override public ViewHolder getViewHolder(View v) {
    return new ViewHolder(v);
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.test_index) TextView testIndex;
    @BindView(R.id.times_completed) TextView timesCompleted;
    @BindView(R.id.most_points) TextView mostPoints;
    @BindView(R.id.overflow_button) ImageButton overflowButton;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}

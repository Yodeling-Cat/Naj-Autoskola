// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 19/2/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.spiraclestudios.autoskola.view.activities.HistoryActivity;
import com.spiraclestudios.autoskola.view.activities.TestActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

  private Context mContext;
  private ArrayList<HistoryListEntry> mDataSet;

  public HistoryListAdapter(ArrayList<HistoryListEntry> dataSet) {
    mDataSet = dataSet;
  }

  public void setContext(Context context) {
    this.mContext = context;
  }

  @Override
  public HistoryListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);

    return new ViewHolder(view, new ViewHolder.IViewOnClickListener() {
      public void onItemClick(View view) {
        HistoryListEntry entry = getItem(
            ((RecyclerView) parent.findViewById(R.id.recycler_view)).getChildAdapterPosition(view));

        // Start TestActivity.
        Intent intent = new Intent(mContext.getApplicationContext(), TestActivity.class);
        intent.putExtra(TestActivity.EXTRA_TEST_TYPE, TestActivity.TestTypes.HISTORY);
        intent.putExtra(TestActivity.EXTRA_TEST_ID, entry.getIndex());
        intent.putExtra(TestActivity.EXTRA_USES_QUESTIONS, entry.getUsesQuestions());
        intent.putExtra(TestActivity.EXTRA_USES_ROAD_SIGNS, entry.getUsesRoadSigns());
        intent.putExtra(TestActivity.EXTRA_USES_INTERSECTIONS, entry.getUsesIntersections());
        intent.putExtra(TestActivity.EXTRA_POINTS, entry.getPoints());
        intent.putExtra(TestActivity.EXTRA_MAX_POINTS, entry.getMaxPoints());
        intent.putExtra(TestActivity.EXTRA_ELAPSED_TIME, entry.getElapsedTime());
        intent.putExtra(TestActivity.EXTRA_ANSWERS, entry.getAnswers());
        mContext.startActivity(intent);
        //((Activity) view.getContext()).getFragmentManager().popBackStackImmediate();
      }
    }, new ViewHolder.IViewOnLongClickListener() {
      public boolean onItemLongClick(final View view) {
        final int position =
            ((RecyclerView) parent.findViewById(R.id.recycler_view)).getChildAdapterPosition(view);
        final HistoryListEntry entry = getItem(position);

        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
              case R.id.item_share:
                Helper.shareTest(mContext, entry.getIndex(), entry.getPoints(),
                    entry.getMaxPoints(), entry.getAmountCorrect(), entry.getAmountIncorrect(), 0,
                    entry.getElapsedTime());
                return true;

              case R.id.item_delete:
                // Set up the Database.
                DbHelper dbHelper = new DbHelper(mContext);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.execSQL("DELETE FROM " + DbContract.History.TABLE_NAME +
                    " WHERE " + DbContract.History._ID +
                    " = " + entry.getDbIndex());

                db.close();
                dbHelper.close();

                deleteItem(position);
                return true;
            }
            return true;
          }
        });
        popupMenu.inflate(R.menu.list__history);
        popupMenu.show();
        return true;
      }
    });
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {

    Resources res = mContext.getResources();
    HistoryListEntry entry = getItem(position);

    String subtitleString;
    Resources.Theme theme = mContext.getTheme();
    TypedValue statusTextColor = new TypedValue();

    // Set text and color of test_subtitle
    if (!entry.getUsesQuestions() || !entry.getUsesRoadSigns() || !entry.getUsesIntersections()) {
      String questions = entry.getUsesQuestions() ? res.getString(R.string.questions) : "";
      String roadSigns = entry.getUsesRoadSigns() ? res.getString(R.string.road_signs_short) : "";
      String intersections =
          entry.getUsesIntersections() ? res.getString(R.string.intersections) : "";
      subtitleString = questions;
      if (!roadSigns.isEmpty()) {
        if (!questions.isEmpty()) {
          subtitleString += ", " + roadSigns;
        } else {
          subtitleString += roadSigns;
        }
      }
      if (!intersections.isEmpty()) {
        if (!questions.isEmpty() || !roadSigns.isEmpty()) {
          subtitleString += ", " + intersections;
        } else {
          subtitleString += intersections;
        }
      }
      theme.resolveAttribute(R.attr.colorSecondaryText, statusTextColor, true);
    } else {
      if (entry.getWasSuccessful()) {
        subtitleString = res.getString(R.string.successful);
        theme.resolveAttribute(R.attr.colorCorrectText, statusTextColor, true);
      } else {
        subtitleString = res.getString(R.string.unsuccessful);
        theme.resolveAttribute(R.attr.colorIncorrectText, statusTextColor, true);
      }
    }

    subtitleString = subtitleString.toLowerCase();
    holder.test_subtitle.setTextColor(statusTextColor.data);

    // Get date
    long dateTime = entry.getDateTime();
    Date date = new Date(dateTime * 1000);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.", Locale.getDefault());
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    holder.test_title.setText(
        String.format(Locale.ENGLISH, res.getString(R.string.test_number_of), entry.getIndex()));
    holder.test_subtitle.setText(subtitleString);
    holder.results_points.setText(
        String.format(Locale.ENGLISH, "%d/%d", entry.getPoints(), entry.getMaxPoints()));
    holder.results_elapsed_time.setText(String.format(Locale.ENGLISH, "%s",
        DateUtils.formatElapsedTime(entry.getElapsedTime() / 1000)));
    holder.results_date.setText(dateFormat.format(date));
    holder.results_time.setText(timeFormat.format(date));
  }

  public void addItem(HistoryListEntry dataObj, int index) {
    mDataSet.add(dataObj);
    notifyItemInserted(index);
  }

  public void deleteItem(int index) {
    mDataSet.remove(index);
    notifyItemRemoved(index);

    // Show empty state if there are no more items left.
    if (mDataSet.size() == 0) {
      ((HistoryActivity) mContext).showEmptyState(true);
    }
  }

  public HistoryListEntry getItem(int position) {
    return mDataSet.get(position);
  }

  @Override public int getItemCount() {
    return mDataSet.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener, View.OnLongClickListener {

    IViewOnClickListener mClickListener;
    IViewOnLongClickListener mLongClickListener;

    TextView test_title;
    TextView test_subtitle;
    TextView results_points;
    TextView results_elapsed_time;
    TextView results_date;
    TextView results_time;

    ViewHolder(View view, IViewOnClickListener clickListener,
        IViewOnLongClickListener longClickListener) {
      super(view);
      view.setLongClickable(true);
      mClickListener = clickListener;
      mLongClickListener = longClickListener;
      test_title = (TextView) view.findViewById(R.id.test_title);
      test_subtitle = (TextView) view.findViewById(R.id.test_subtitle);
      results_points = (TextView) view.findViewById(R.id.results_points);
      results_elapsed_time = (TextView) view.findViewById(R.id.results_elapsed_time);
      results_time = (TextView) view.findViewById(R.id.results_time);
      results_date = (TextView) view.findViewById(R.id.results_date);

      view.setOnClickListener(this);
      view.setOnLongClickListener(this);
    }

    interface IViewOnClickListener {

      void onItemClick(View view);
    }

    interface IViewOnLongClickListener {
      boolean onItemLongClick(View view);
    }

    @Override public void onClick(View view) {
      mClickListener.onItemClick(view);
    }

    @Override public boolean onLongClick(View view) {
      return mLongClickListener.onItemLongClick(view);
    }
  }
}
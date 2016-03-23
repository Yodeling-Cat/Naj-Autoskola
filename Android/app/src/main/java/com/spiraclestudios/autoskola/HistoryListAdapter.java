/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 19/2/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spiraclestudios.autoskola.activities.TestActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HistoryListEntry> mDataSet;

    public HistoryListAdapter(ArrayList<HistoryListEntry> dataSet) {
        mDataSet = dataSet;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);

        return new ViewHolder(view,
                new ViewHolder.IViewOnClickListener() {
                    public void onItemClick(View view) {
                        HistoryListEntry entry = getItem(((RecyclerView) parent.findViewById(R.id.recycler_view))
                                .getChildAdapterPosition(view));

                        // Start TestActivity.
                        Intent intent = new Intent(context.getApplicationContext(),
                                TestActivity.class);
                        intent.putExtra(TestActivity.EXTRA_TEST_TYPE, TestActivity.TestTypes.HISTORY);
                        intent.putExtra(TestActivity.EXTRA_TEST_ID, entry.getIndex());
                        intent.putExtra(TestActivity.EXTRA_USES_QUESTIONS, entry.getUsesQuestions());
                        intent.putExtra(TestActivity.EXTRA_USES_ROAD_SIGNS, entry.getUsesRoadSigns());
                        intent.putExtra(TestActivity.EXTRA_USES_INTERSECTIONS, entry.getUsesIntersections());
                        intent.putExtra(TestActivity.EXTRA_POINTS, entry.getPoints());
                        intent.putExtra(TestActivity.EXTRA_MAX_POINTS, entry.getMaxPoints());
                        intent.putExtra(TestActivity.EXTRA_ELAPSED_TIME, entry.getElapsedTime());
                        intent.putExtra(TestActivity.EXTRA_ANSWERS, entry.getAnswers());
                        context.startActivity(intent);
                        //((Activity) view.getContext()).getFragmentManager().popBackStackImmediate();
                    }
                }/*,
        new ViewHolder.IViewOnLongClickListener()
        {
          public boolean onItemLongClick(View view)
          {
            int position = ((RecyclerView) parent.findViewById(R.id.recycler_view)).getChildAdapterPosition(view);

            Toast.makeText(context, "Longclicked #" + position, Toast.LENGTH_SHORT).show();
            return true;
          }*/
        );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Resources res = context.getResources();
        HistoryListEntry entry = getItem(position);

        String statusString;
        Resources.Theme theme = context.getTheme();
        TypedValue statusTextColor = new TypedValue();
        if (!entry.getUsesQuestions() || !entry.getUsesRoadSigns() || !entry.getUsesIntersections()) {
            statusString = res.getString(R.string.partial);
            theme.resolveAttribute(R.attr.colorPrimaryText, statusTextColor, true);
        } else {
            if (entry.getWasSuccessful()) {
                statusString = res.getString(R.string.successful);
                theme.resolveAttribute(R.attr.colorCorrectText, statusTextColor, true);
            } else {
                statusString = res.getString(R.string.unsuccessful);
                theme.resolveAttribute(R.attr.colorIncorrectText, statusTextColor, true);
            }
        }
        holder.test_status.setTextColor(statusTextColor.data);

        String questions = entry.getUsesQuestions() ? res.getString(R.string.questions) : "";
        String roadSigns = entry.getUsesRoadSigns() ? res.getString(R.string.road_signs_short) : "";
        String intersections = entry.getUsesIntersections() ? res.getString(R.string.intersections) : "";
        String optionsString = questions;
        if (!roadSigns.isEmpty()) {
            if (!questions.isEmpty()) {
                optionsString += ", " + roadSigns.toLowerCase();
            } else {
                optionsString += roadSigns;
            }
        }
        if (!intersections.isEmpty()) {
            if (!questions.isEmpty() || !roadSigns.isEmpty()) {
                optionsString += ", " + intersections.toLowerCase();
            } else {
                optionsString += intersections;
            }
        }

        // Get date
        long dateTime = entry.getDateTime();
        Date date = new Date(dateTime * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        holder.test_status.setText(statusString);
        holder.test_options.setText(optionsString);
        holder.results_points.setText(String.format(Locale.ENGLISH, "%d/%d", entry.getPoints(), entry.getMaxPoints()));
        holder.results_elapsed_time.setText(String.format(Locale.ENGLISH, "%s", DateUtils.formatElapsedTime(entry.getElapsedTime() / 1000)));
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
    }

    public HistoryListEntry getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public IViewOnClickListener mClickListener;
        //public IViewOnLongClickListener mLongClickListener;

        public TextView test_status;
        public TextView test_options;
        public TextView results_points;
        public TextView results_elapsed_time;
        public TextView results_date;
        public TextView results_time;

        public ViewHolder(View view, IViewOnClickListener clickListener) {
            super(view);
            view.setLongClickable(true);
            mClickListener = clickListener;
            //mLongClickListener = longClickListener;
            test_status = (TextView) view.findViewById(R.id.test_status);
            test_options = (TextView) view.findViewById(R.id.test_options);
            results_points = (TextView) view.findViewById(R.id.results_points);
            results_elapsed_time = (TextView) view.findViewById(R.id.results_elapsed_time);
            results_time = (TextView) view.findViewById(R.id.results_time);
            results_date = (TextView) view.findViewById(R.id.results_date);

            view.setOnClickListener(this);
            //view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(view);
        }

    /*@Override
    public boolean onLongClick(View view)
    {
      return mLongClickListener.onItemLongClick(view);
    }*/

        public interface IViewOnClickListener {

            void onItemClick(View view);
        }

    /*public interface IViewOnLongClickListener
    {
      boolean onItemLongClick(View view);
    }*/
    }
}
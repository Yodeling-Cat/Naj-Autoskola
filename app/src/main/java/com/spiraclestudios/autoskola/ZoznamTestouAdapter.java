package com.spiraclestudios.autoskola;

/**
 * Created by benji on 15/10/2015.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ZoznamTestouAdapter extends RecyclerView.Adapter<ZoznamTestouAdapter.ViewHolder> {
    private static String LOG_TAG = "ZoznamTestouAdapter";
    private ArrayList<DataObject> mDataset;
    private static TestEntryClickListener testEntryClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView test_id;
        TextView times_played;
        ImageButton view_answers;
        ImageButton toggle_history;
        LinearLayout history;

        public ViewHolder(View itemView) {
            super(itemView);
            test_id = (TextView) itemView.findViewById(R.id.test_id);
            times_played = (TextView) itemView.findViewById(R.id.times_played);
            view_answers = (ImageButton) itemView.findViewById(R.id.view_answers);
            toggle_history = (ImageButton) itemView.findViewById(R.id.toggle_history);
            history = (LinearLayout) itemView.findViewById(R.id.history);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            testEntryClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(TestEntryClickListener clickListener) {
        testEntryClickListener = clickListener;
    }

    public ZoznamTestouAdapter(ArrayList<DataObject> dataset) {
        mDataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zoznam_testou_entry, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.test_id.setText("#" + mDataset.get(position).getIndex());
        holder.times_played.setText("spustené - " + "2" + "x");

        holder.toggle_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
                /*if (history.isShown()) {
                    Effects.slide_up(getContext(), history);
                    history.setVisibility(GONE);
                } else {
                    history.setVisibility(VISIBLE);
                    Effects.slide_down(getContext(), history);
                }*/
            }
        });

        holder.view_answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Launch a test with a flag that makes it mark all the correct answers
            }
        });
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface TestEntryClickListener {
        void onItemClick(int position, View view);
    }
}
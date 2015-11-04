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

import com.spiraclestudios.autoskola.Activities.MainActivity;
import com.spiraclestudios.autoskola.Dialogs.TestOptionsDialog;

import java.util.ArrayList;

public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.ViewHolder> {
    private static String TAG = "TestsListAdapter";
    private ArrayList<TestsListEntry> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public IViewOnClickListener mListener;

        public TextView test_id;
        public TextView times_played;
        public ImageButton view_answers;
        public ImageButton toggle_history;
        public LinearLayout history;

        public ViewHolder(View view, IViewOnClickListener listener) {
            super(view);
            mListener = listener;
            test_id = (TextView) view.findViewById(R.id.test_id);
            times_played = (TextView) view.findViewById(R.id.times_played);
            view_answers = (ImageButton) view.findViewById(R.id.view_answers);
            toggle_history = (ImageButton) view.findViewById(R.id.toggle_history);
            history = (LinearLayout) view.findViewById(R.id.history);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //if (view instanceof ImageButton) { mListener.onExpandButtonClick; }
            mListener.onItemClick(view);
        }

        public interface IViewOnClickListener {
            void onItemClick(View view);
            //void onExpandButtonClick(View view);
        }
    }

    public TestsListAdapter(ArrayList<TestsListEntry> dataset) {
        mDataset = dataset;
    }

    @Override
    public TestsListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tests_list_entry, parent, false);

        TestsListAdapter.ViewHolder viewHolder = new ViewHolder(view, new TestsListAdapter.ViewHolder.IViewOnClickListener() {
            public void onItemClick(View view) {
                int index = mDataset.get(((RecyclerView) parent.findViewById(R.id.recycler_view)).getChildAdapterPosition(view)).getIndex();
                TestOptionsDialog dialog = TestOptionsDialog.newInstance(index);

                dialog.show(((MainActivity) view.getContext()).getSupportFragmentManager(),
                        "MoznostiTestu");
            }

            // public void onExpandButtonClick(View view) { }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.test_id.setText("#" + getItem(position).getIndex());
        holder.times_played.setText("spustené - " + "2" + "x");

        holder.toggle_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement
                if (holder.history.isShown()) {
                    Effects.slide_up(view.getContext(), holder.history);
                    holder.history.setVisibility(View.GONE);
                } else {
                    holder.history.setVisibility(View.VISIBLE);
                    Effects.slide_down(view.getContext(), holder.history);
                }
            }
        });

        /*holder.view_answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Launch a test with a flag that makes it mark all the correct answers
            }
        });*/
    }

    public void addItem(TestsListEntry dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public TestsListEntry getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
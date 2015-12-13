package com.spiraclestudios.autoskola;

/**
 * Created by benji on 15/10/2015.
 */

import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spiraclestudios.autoskola.Activities.MainActivity;
import com.spiraclestudios.autoskola.Activities.TestActivity;
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
        public ImageButton overflow_button;

        public ViewHolder(View view, IViewOnClickListener listener) {
            super(view);
            mListener = listener;
            test_id = (TextView) view.findViewById(R.id.test_id);
            times_played = (TextView) view.findViewById(R.id.times_played);
            overflow_button = (ImageButton) view.findViewById(R.id.overflow_button);

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
                int index = mDataset.get(((RecyclerView) parent.findViewById(R.id.recycler_view))
                        .getChildAdapterPosition(view)).getIndex();
                TestOptionsDialog dialog = TestOptionsDialog.newInstance(index);

                dialog.show(((MainActivity) view.getContext()).getSupportFragmentManager(),
                        "MoznostiTestu");
            }

            // public void onExpandButtonClick(View view) { }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.test_id.setText("#" + getItem(position).getIndex());
        holder.times_played.setText(holder.times_played.getContext().getResources().
                getText(R.string.dokoncene) + " - " + "0" + "x");

        holder.overflow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;

                        switch (item.getItemId()) {
                            case R.id.item_correct_answers:
                                // Start TestActivity with the EXTRA_MARK_CORRECT_ANSWERS flag
                                intent = new Intent(view.getContext(), TestActivity.class);

                                intent.putExtra(TestActivity.EXTRA_INDEX, getItem(position)
                                        .getIndex());
                                intent.putExtra(TestActivity.EXTRA_MARK_CORRECT_ANSWERS, true);
                                view.getContext().startActivity(intent);
                                return true;

                            case R.id.item_history:
                                // Start TestActivity
                                /*intent = new Intent(view.getContext(), TestActivity.class);

                                intent.putExtra(TestActivity.EXTRA_INDEX, getItem(position)
                                        .getIndex());
                                intent.putExtra(TestActivity.EXTRA_MARK_CORRECT_ANSWERS, true);
                                view.getContext().startActivity(intent);*/

                                Toast.makeText(view.getContext(),
                                        R.string.toast_not_yet_implemented,
                                        Toast.LENGTH_SHORT)
                                        .show();
                                return true;
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.tests_list);
                popupMenu.show();
            }
        });
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
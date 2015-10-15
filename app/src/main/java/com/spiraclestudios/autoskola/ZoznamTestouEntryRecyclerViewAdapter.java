package com.spiraclestudios.autoskola;

/**
 * Created by benji on 15/10/2015.
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ZoznamTestouEntryRecyclerViewAdapter extends RecyclerView
        .Adapter<ZoznamTestouEntryRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "ZoznamTestouEntryRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static TestEntryClickListener testEntryClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView text;

        public DataObjectHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            Log.i(LOG_TAG, "Adding Listener");
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

    public ZoznamTestouEntryRecyclerViewAdapter(ArrayList<DataObject> dataset) {
        mDataset = dataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zoznam_testou_entry, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.text.setText("#" + mDataset.get(position).getIndex() + " - " + "x3");
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
        public void onItemClick(int position, View view);
    }
}
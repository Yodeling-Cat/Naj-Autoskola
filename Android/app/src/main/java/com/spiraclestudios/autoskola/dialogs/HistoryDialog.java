/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.HistoryListAdapter;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.TestsListAdapter;
import com.spiraclestudios.autoskola.HistoryListEntry;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Original created by benji on 19/2/2016.
 */
public class HistoryDialog extends AppCompatDialogFragment {

    @Bind(R.id.recycler_view)
    public RecyclerView recycler_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_history, container, false);
        ButterKnife.bind(this, view);

        if (view != null) {
            recycler_view.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recycler_view.setLayoutManager(layoutManager);
            RecyclerView.Adapter<HistoryListAdapter.ViewHolder> adapter =
                    new HistoryListAdapter(getDataSet());
            recycler_view.setAdapter(adapter);

            //RecyclerView.ItemDecoration itemDecoration =
            //        new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
            //recycler_view.addItemDecoration(itemDecoration);
        }
        return view;
    }

    // Returns data to populate the adapter with.
    private ArrayList<HistoryListEntry> getDataSet() {
        ArrayList<HistoryListEntry> results = new ArrayList<>();
        int start;
        int end;

        start = 1;
        end = 10;

        for (int i = start; i < end; i++) {
            HistoryListEntry entry = new HistoryListEntry(i);
            results.add(entry);
        }

        return results;
    }
}

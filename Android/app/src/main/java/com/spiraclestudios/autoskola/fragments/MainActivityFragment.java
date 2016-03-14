/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.TestsListAdapter;
import com.spiraclestudios.autoskola.TestsListEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Added by benji on 14/10/2015.
 */
public class MainActivityFragment extends Fragment {

    public Helper.Groups group = Helper.Groups.AB;

    @Bind(R.id.recycler_view)
    public RecyclerView recycler_view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public MainActivityFragment() {}

    public static MainActivityFragment newInstance(Helper.Groups group) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable("group", group);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        group = (Helper.Groups) getArguments().getSerializable("group");

        Helper.setTheme(getContext());
        View view = inflater.inflate(R.layout.list_tests, container, false);
        ButterKnife.bind(this, view);

        if (view != null) {
            recycler_view.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recycler_view.setLayoutManager(layoutManager);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView.Adapter<TestsListAdapter.ViewHolder> adapter = new TestsListAdapter(getDataSet());
        recycler_view.setAdapter(adapter);
        //recycler_view.getAdapter().notifyDataSetChanged();
    }

    // Returns data to populate the adapter with.
    private ArrayList<TestsListEntry> getDataSet() {
        ArrayList<TestsListEntry> results = new ArrayList<>();

        // Set up the Database.
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the History for this test version.
        String query = "SELECT " +
                DbContract.History.COLUMN_TEST_ID +
                ", count(" + DbContract.History.COLUMN_TEST_ID +
                ") FROM " + DbContract.History.TABLE_NAME +
                " GROUP by " + DbContract.History.COLUMN_TEST_ID;

        Cursor cHistory = db.rawQuery(query, new String[] {});
        Map<Integer, Integer> timesCompletedMap = new HashMap<>();

        for (cHistory.moveToFirst(); !cHistory.isAfterLast(); cHistory.moveToNext()) {
            int testId = cHistory.getInt(0);
            int testCount = cHistory.getInt(1);

            if (testId != 0) {
                timesCompletedMap.put(testId, testCount);
            }
        }

        int start;
        int end;
        if (group == Helper.Groups.AB) {
            start = 1;
            end = 36;
        } else {
            start = 36;
            end = 61;
        }

        for (int i = start; i < end; i++) {
            int timesCompleted = timesCompletedMap.containsKey(i) ? timesCompletedMap.get(i) : 0;
            TestsListEntry entry = new TestsListEntry(i, timesCompleted);
            results.add(entry);
        }

        cHistory.close();
        return results;
    }
}
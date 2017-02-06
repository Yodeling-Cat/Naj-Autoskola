// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.FABSpaceListEntry;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.TestsListAdapter;
import com.spiraclestudios.autoskola.TestListEntry;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Added by benji on 14/10/2015.
 */
public class MainActivityFragment extends Fragment {

  private final static String STATE_RECYCLER_VIEW_LAST_POSITION = "recyclerViewLastPosition";

  public Utils.Groups group = Utils.Groups.AB;

  private int recyclerViewLastPosition = 0;

  @Bind(R.id.recycler_view) public RecyclerView recycler_view;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
   * screen orientation changes).
   */
  public MainActivityFragment() {
  }

  public static MainActivityFragment newInstance(Utils.Groups group) {
    MainActivityFragment fragment = new MainActivityFragment();
    Bundle bundle = new Bundle();

    bundle.putSerializable("group", group);

    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    group = (Utils.Groups) getArguments().getSerializable("group");

    BaseActivity.applyAppTheme(getContext());
    View view = inflater.inflate(R.layout.fragment_main_tests_list, container, false);
    ButterKnife.bind(this, view);

    if (view != null) {
      recycler_view.setHasFixedSize(true);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
      recycler_view.setLayoutManager(layoutManager);
    }

    if (savedInstanceState != null) {
      // Restore recycler view scrolling position.
      recyclerViewLastPosition = savedInstanceState.getInt(STATE_RECYCLER_VIEW_LAST_POSITION);
      recycler_view.getLayoutManager().scrollToPosition(recyclerViewLastPosition);
    }

    return view;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    // Save recycler view scrolling position.
    outState.putInt(STATE_RECYCLER_VIEW_LAST_POSITION,
        ((LinearLayoutManager) recycler_view.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
  }

  @Override public void onPause() {
    super.onPause();

    // Save recycler view scrolling position.
    recyclerViewLastPosition =
        ((LinearLayoutManager) recycler_view.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
  }

  @Override public void onResume() {
    super.onResume();

    FastItemAdapter adapter = new FastItemAdapter();
    recycler_view.setAdapter(adapter);
    adapter.add(getDataSet());
    //recycler_view.getAdapter().notifyDataSetChanged();

    // Restore recycler view scrolling position.
    recycler_view.getLayoutManager().scrollToPosition(recyclerViewLastPosition);
  }

  /**
   * Returns data to populate the adapter with.
   */
  private ArrayList<AbstractItem> getDataSet() {
    ArrayList<AbstractItem> results = new ArrayList<>();

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
      TestListEntry entry = new TestListEntry();
      entry.index = i;
      entry.timesCompleted = timesCompleted;
      results.add(entry);
    }

    cHistory.close();
    results.add(new FABSpaceListEntry());
    return results;
  }
}
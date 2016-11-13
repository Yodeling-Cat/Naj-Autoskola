/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.activities.RoadSignsDetailActivity;
import com.spiraclestudios.autoskola.items.RoadSignsItem;
import java.util.ArrayList;

public class RoadSignsFragment extends Fragment {

  public static final String ARG_CATEGORY = "road_sign_category";
  public static final String ARG_CATEGORY_NAME = "road_sign_category_name";

  private static final String STATE_CATEGORY = "category";
  private static final String STATE_CATEGORY_NAME = "categoryName";

  private String category;
  private String categoryName;

  @Bind(R.id.recycler_view) public RecyclerView recycler_view;

    /*/**
     * The serialization (saved instance state) Bundle key representing the activated item
     * position.
     * Only used on tablets.
     */
  //private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /*/**
     * The current activated item position. Only used on tablets.
     */
  //private int mActivatedPosition = RecyclerView.NO_POSITION;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
   * screen orientation changes).
   */
  public RoadSignsFragment() {
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {
      category = getArguments().getString(ARG_CATEGORY);
      categoryName = getArguments().getString(ARG_CATEGORY_NAME);
    } else {
      category = savedInstanceState.getString(STATE_CATEGORY);
      categoryName = savedInstanceState.getString(STATE_CATEGORY_NAME);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_CATEGORY, category);
    outState.putString(STATE_CATEGORY_NAME, categoryName);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    Helper.setTheme(getContext());
    View view = inflater.inflate(R.layout.fragment_road_signs, container, false);
    ButterKnife.bind(this, view);

    // Switch to Grid layout if viewing the SPEC category.
    boolean useGridLayout;
    if (category.equals("SPEC")) {
      useGridLayout = true;
    } else {
      SharedPreferences prefs =
          getActivity().getSharedPreferences(G.PREFS_GENERIC, Context.MODE_PRIVATE);
      useGridLayout = prefs.getBoolean("road_signs_list_use_grid_layout", false);
    }
    setLayoutMode(useGridLayout);
    recycler_view.setHasFixedSize(true);

    return view;
  }

  /**
   * Switching between List and Grid layout managers.
   *
   * @param useGridLayout Use Grid layout manager? List otherwise.
   */
  public void setLayoutMode(boolean useGridLayout) {
    FastItemAdapter<RoadSignsItem> fastItemAdapter = new FastItemAdapter<>();

    if (useGridLayout) {
      int columns = getResources().getInteger(R.integer.road_signs_columns);
      recycler_view.setLayoutManager(new GridLayoutManager(getContext(), columns));
    } else {
      recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<RoadSignsItem>() {
      @Override public boolean onClick(View v, IAdapter<RoadSignsItem> adapter, RoadSignsItem item,
          int position) {
        Context context = getContext();
        Intent intent = new Intent(context, RoadSignsDetailActivity.class);
        intent.putExtra(RoadSignsDetailActivity.EXTRA_NAME, item.roadSignName);
        intent.putExtra(RoadSignsDetailActivity.EXTRA_DESCRIPTION, item.roadSignDesc);
        intent.putExtra(RoadSignsDetailActivity.EXTRA_IMAGE_PATH, item.imagePath);
        intent.putExtra(RoadSignsDetailActivity.EXTRA_CATEGORY_NAME, categoryName);
        context.startActivity(intent);
        return false;
      }
    });

    recycler_view.setAdapter(fastItemAdapter);
    fastItemAdapter.add(getDataSet(useGridLayout));
    fastItemAdapter.notifyAdapterDataSetChanged();
  }

  /**
   * Returns data to populate the adapter with.
   */
  private ArrayList<RoadSignsItem> getDataSet(boolean isGridView) {
    ArrayList<RoadSignsItem> results = new ArrayList<>();

    // Read the name, description and image paths from the database.
    DbHelper dbHelper = new DbHelper(getContext());
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Cursor cursor = db.rawQuery("SELECT " + DbContract.RoadSigns.COLUMN_NAME + ", " +
        DbContract.RoadSigns.COLUMN_DESCRIPTION + ", " +
        DbContract.RoadSigns.COLUMN_IMAGE + " FROM " +
        DbContract.RoadSigns.TABLE_NAME + " WHERE " +
        DbContract.RoadSigns.COLUMN_CATEGORY + " = ?", new String[] { category });

    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      String name = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.
          RoadSigns.COLUMN_NAME));

      String desc = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.
          RoadSigns.COLUMN_DESCRIPTION));

      String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.
          RoadSigns.COLUMN_IMAGE));

      results.add(new RoadSignsItem(name, desc, category + "/" + imagePath, isGridView));
    }

    cursor.close();
    db.close();
    dbHelper.close();

    return results;
  }

    /*@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
        && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }*/

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != RecyclerView.NO_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }*/

  /**
   * Turns on activate-on-click mode. When this mode is on, list items will be
   * given the 'activated' state when touched.
   */
    /*public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }*/

    /*private void setActivatedPosition(int position) {
        if (position == RecyclerView.NO_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }*/
}

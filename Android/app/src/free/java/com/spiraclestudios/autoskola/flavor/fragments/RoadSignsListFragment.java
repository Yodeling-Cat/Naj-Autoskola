/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

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

import com.spiraclestudios.autoskola.DbContract;
import com.spiraclestudios.autoskola.DbHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.RoadSignsListAdapter;
import com.spiraclestudios.autoskola.RoadSignsListEntry;

import java.util.ArrayList;

public class RoadSignsListFragment extends Fragment
{

  public static final String ARG_ROAD_SIGN_CATEGORY = "road_sign_category";
  public static final String ARG_ROAD_SIGN_CATEGORY_NAME = "road_sign_category_name";
  /**
   * The serialization (saved instance state) Bundle key representing the activated item position.
   * Only used on tablets.
   */
  private static final String STATE_ACTIVATED_POSITION = "activated_position";
  public RecyclerView recycler_view;
  private String mCategory;
  private String mCategoryName;
  /**
   * The current activated item position. Only used on tablets.
   */
  private int mActivatedPosition = RecyclerView.NO_POSITION;
  private RecyclerView.Adapter<RoadSignsListAdapter.ViewHolder> adapter;
  private RecyclerView.LayoutManager layoutManager;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
   * screen orientation changes).
   */
  public RoadSignsListFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState)
  {

    super.onCreate(savedInstanceState);

    mCategory = getArguments().getString(ARG_ROAD_SIGN_CATEGORY);
    mCategoryName = getArguments().getString(ARG_ROAD_SIGN_CATEGORY_NAME);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {

    Helper.setTheme(getContext());
    View view = inflater.inflate(R.layout.road_signs_list, container, false);

    recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
    recycler_view.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(getContext());
    recycler_view.setLayoutManager(layoutManager);
    adapter = new RoadSignsListAdapter(getDataSet(), mCategoryName);
    recycler_view.setAdapter(adapter);

    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {

    super.onViewCreated(view, savedInstanceState);

    // Restore the previously serialized activated item position.
    /*if (savedInstanceState != null
        && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }*/
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {

    super.onSaveInstanceState(outState);
    if (mActivatedPosition != RecyclerView.NO_POSITION) {
      // Serialize and persist the activated item position.
      outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
    }
  }

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

  /**
   * Returns data to populate the adapter with.
   */
  private ArrayList<RoadSignsListEntry> getDataSet()
  {

    ArrayList<RoadSignsListEntry> results = new ArrayList<>();

    // [Read the name, description and image paths from the database.]
    DbHelper dbHelper = new DbHelper(getContext());
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Cursor cursor = db.rawQuery(
        "SELECT " + DbContract.RoadSigns.COLUMN_NAME + ", " +
            DbContract.RoadSigns.COLUMN_DESCRIPTION + ", " +
            DbContract.RoadSigns.COLUMN_IMAGE + " FROM " +
            DbContract.RoadSigns.TABLE_NAME + " WHERE " +
            DbContract.RoadSigns.COLUMN_CATEGORY + " = ?", new String[] { mCategory });

    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      String name = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.
          RoadSigns.COLUMN_NAME));

      String desc = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.
          RoadSigns.COLUMN_DESCRIPTION));

      String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.
          RoadSigns.COLUMN_IMAGE));

      results.add(new RoadSignsListEntry(name, desc, mCategory + "/" + imagePath));
    }

    cursor.close();
    db.close();
    dbHelper.close();

    return results;
  }
}

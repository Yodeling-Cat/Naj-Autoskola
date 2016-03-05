/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.RoadSignsCategoryListItem;
import com.spiraclestudios.autoskola.activities.RoadSignsListActivity;

import java.util.ArrayList;

public class RoadSignsCategoryListFragment extends Fragment
{
  /**
   * The serialization (saved instance state) Bundle key representing the activated item position.
   * Only used on tablets.
   */
  //private static final String STATE_ACTIVATED_POSITION = "activated_position";
  /**
   * The current activated item position. Only used on tablets.
   */
  //private int mActivatedPosition = RecyclerView.NO_POSITION;

  private FastItemAdapter<RoadSignsCategoryListItem> mFastItemAdapter;
  private ClickListenerHelper<RoadSignsCategoryListItem> mClickListenerHelper;

  private final ArrayList<RoadSignsCategoryListItem> mRoadSignsCategories = new ArrayList<>();

  /** Class Constructor */
  public RoadSignsCategoryListFragment()
  {
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("A", "Výstražné značky", "A/a8"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("B", "Zákazové značky", "B/b31a"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("C", "Príkazové značky", "C/c4a"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("E", "Dodatkové tabuľky", "E/e9"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("II", "Informatívne iné značky", "II/ii11"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("IP", "Informatívne, prevádzkové, smerové a iné značky", "IP/ip10"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("IS", "Informatívne smerové značky", "IS/is5a"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("O", "Osobitné označenia", "O/o4"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("P", "Značky upravujúce prednosť v jazde", "P/p1"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("S", "Svetelné signály", "S/s5b"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("SPEC", "Príklady", "SPEC/spec113"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("V", "Vodorovné dopravné značky", "V/v10e"));
    mRoadSignsCategories.add(new RoadSignsCategoryListItem("Z", "Iné dopravné zariadenia", "Z/z10"));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    Helper.setTheme(getContext());
    View view = inflater.inflate(R.layout.list_road_signs_category, container, false);

    RecyclerView recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
    recycler_view.setHasFixedSize(true);
    // Find out how many columns we display.
    int columns = getResources().getInteger(R.integer.road_signs_category_columns);
    if (columns == 1) {
      recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
    } else {
      recycler_view.setLayoutManager(new GridLayoutManager(getContext(), columns));
    }
    mFastItemAdapter = new FastItemAdapter<>();
    mClickListenerHelper = new ClickListenerHelper<>(mFastItemAdapter);

    // Configure FastAdapter
    mFastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<RoadSignsCategoryListItem>()
    {
      @Override
      public boolean onClick(View v, IAdapter<RoadSignsCategoryListItem> adapter, RoadSignsCategoryListItem item, int position)
      {
        Toast.makeText(v.getContext(), item.categoryName, Toast.LENGTH_SHORT).show();
        return false;
      }
    });

    recycler_view.setAdapter(mFastItemAdapter);
    mFastItemAdapter.add(mRoadSignsCategories);

    // A custom OnCreateViewHolder listener class which is used to create the viewHolders.
    // We define the listener for the imageLovedContainer here for better performance,
    // you can also define the listener within the items bindView method but performance is better
    // if you do it like this.
    mFastItemAdapter.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener()
    {
      @Override
      public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType)
      {
        return mFastItemAdapter.getTypeInstance(viewType).getViewHolder(parent);
      }

      @Override
      public RecyclerView.ViewHolder onPostCreateViewHolder(final RecyclerView.ViewHolder viewHolder)
      {
        if (viewHolder instanceof RoadSignsCategoryListItem.ViewHolder) {
          mClickListenerHelper.listen(viewHolder, ((RoadSignsCategoryListItem.ViewHolder) viewHolder).view, new ClickListenerHelper.OnClickListener<RoadSignsCategoryListItem>()
          {
            @Override
            public void onClick(View v, int position, RoadSignsCategoryListItem item)
            {
              Context context = getContext();
              Intent intent = new Intent(context, RoadSignsListActivity.class);
              intent.putExtra(RoadSignsListFragment.ARG_ROAD_SIGN_CATEGORY, item.category);
              intent.putExtra(RoadSignsListFragment.ARG_ROAD_SIGN_CATEGORY_NAME, item.categoryName);
              context.startActivity(intent);
            }
          });
        }
        return viewHolder;
      }
    });
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

  /*@Override
  public void onSaveInstanceState(Bundle outState)
  {
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

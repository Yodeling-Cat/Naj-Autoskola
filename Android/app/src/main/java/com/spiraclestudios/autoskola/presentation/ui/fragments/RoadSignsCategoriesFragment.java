/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.presentation.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.domain.RoadSignsCategoriesItem;
import com.spiraclestudios.autoskola.presentation.ui.activities.RoadSignsActivity;
import com.spiraclestudios.autoskola.repository.RoadSignsCategoriesListRepository;
import java.util.ArrayList;

public class RoadSignsCategoriesFragment extends Fragment {

  /**
   * The serialization (saved instance state) Bundle key representing the activated item position.
   * Only used on tablets.
   */
  private static final String STATE_ACTIVATED_POSITION = "activated_position";
  /**
   * The current activated item position. Only used on tablets.
   */
  private int mActivatedPosition = RecyclerView.NO_POSITION;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_road_signs_categories, container, false);

    RecyclerView recycler_view = ButterKnife.findById(view, R.id.recycler_view);
    recycler_view.setHasFixedSize(true);
    recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
    FastItemAdapter<RoadSignsCategoriesItem> fastItemAdapter = new FastItemAdapter<>();

    // Configure FastAdapter
    fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<RoadSignsCategoriesItem>() {
      @Override public boolean onClick(View v, IAdapter<RoadSignsCategoriesItem> adapter,
          RoadSignsCategoriesItem item, int position) {
        Context context = getContext();
        Intent intent = new Intent(context, RoadSignsActivity.class);
        intent.putExtra(RoadSignsActivity.EXTRA_CATEGORY, item.category);
        intent.putExtra(RoadSignsActivity.EXTRA_CATEGORY_NAME, item.categoryName);
        context.startActivity(intent);
        return false;
      }
    });

    recycler_view.setAdapter(fastItemAdapter);
    fastItemAdapter.add(new RoadSignsCategoriesListRepository().getList());

    return view;
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

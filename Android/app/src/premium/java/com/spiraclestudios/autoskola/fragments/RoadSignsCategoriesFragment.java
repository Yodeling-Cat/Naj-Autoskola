/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.activities.RoadSignsActivity;
import com.spiraclestudios.autoskola.items.RoadSignsCategoriesItem;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class RoadSignsCategoriesFragment extends Fragment {

    private final ArrayList<RoadSignsCategoriesItem> roadSignsCategories = new ArrayList<>();

    /** Class Constructor */
    public RoadSignsCategoriesFragment() {
        roadSignsCategories.add(new RoadSignsCategoriesItem("A", "Výstražné značky", "A/a8"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("B", "Zákazové značky", "B/b31a"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("C", "Príkazové značky", "C/c4a"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("E", "Dodatkové tabuľky", "E/e9"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("II", "Informatívne iné značky", "II/ii11"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("IP", "Informatívne, prevádzkové, smerové a iné značky", "IP/ip10"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("IS", "Informatívne smerové značky", "IS/is5a"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("O", "Osobitné označenia", "O/o4"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("P", "Značky upravujúce prednosť v jazde", "P/p1"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("S", "Svetelné signály", "S/s5b"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("SPEC", "Príklady", "SPEC/spec113"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("V", "Vodorovné dopravné značky", "V/v10e"));
        roadSignsCategories.add(new RoadSignsCategoriesItem("Z", "Iné dopravné zariadenia", "Z/z10"));
    }

    /**
     * The serialization (saved instance state) Bundle key representing the activated item position.
     * Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = RecyclerView.NO_POSITION;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Helper.setTheme(getContext());
        View view = inflater.inflate(R.layout.fragment_road_signs_categories, container, false);

        RecyclerView recycler_view = ButterKnife.findById(view, R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        FastItemAdapter<RoadSignsCategoriesItem> fastItemAdapter = new FastItemAdapter<>();

        // Configure FastAdapter
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<RoadSignsCategoriesItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RoadSignsCategoriesItem> adapter, RoadSignsCategoriesItem item, int position) {
                Context context = getContext();
                Intent intent = new Intent(context, RoadSignsActivity.class);
                intent.putExtra(RoadSignsActivity.EXTRA_CATEGORY, item.category);
                intent.putExtra(RoadSignsActivity.EXTRA_CATEGORY_NAME, item.categoryName);
                context.startActivity(intent);
                return false;
            }
        });

        recycler_view.setAdapter(fastItemAdapter);
        fastItemAdapter.add(roadSignsCategories);

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

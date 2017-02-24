/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.spiraclestudios.autoskola.G;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import com.spiraclestudios.autoskola.presentation.ui.fragments.RoadSignsFragment;

public class RoadSignsActivity extends StandardActivity {

  public static final String EXTRA_CATEGORY = "com.spiraclestudios.autoskola.ROAD_SIGN_CATEGORY";
  public static final String EXTRA_CATEGORY_NAME =
      "com.spiraclestudios.autoskola.ROAD_SIGN_CATEGORY_NAME";

  private static final String STATE_CATEGORY = "category";
  private static final String STATE_CATEGORY_NAME = "categoryName";

  private String category;
  private String categoryName;

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.activity_road_signs_list);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {
      // Create the list fragment and add it to the activity
      // using a fragment transaction.
      Intent intent = getIntent();

      category = intent.getStringExtra(EXTRA_CATEGORY);
      categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);

      Bundle arguments = new Bundle();
      arguments.putString(RoadSignsFragment.ARG_CATEGORY, category);
      arguments.putString(RoadSignsFragment.ARG_CATEGORY_NAME, categoryName);
      RoadSignsFragment fragment = new RoadSignsFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.categories_fragment, fragment)
          .commit();
    } else {
      category = savedInstanceState.getString(STATE_CATEGORY);
      categoryName = savedInstanceState.getString(STATE_CATEGORY_NAME);
    }

    // Set up Toolbar.
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(categoryName);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(STATE_CATEGORY, category);
    outState.putString(STATE_CATEGORY_NAME, categoryName);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity__road_signs_list, menu);

    // Switch to Grid layout if viewing the SPEC category.
    boolean useGridLayout;
    if (category.equals("SPEC")) {
      useGridLayout = true;
    } else {
      SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
      useGridLayout = prefs.getBoolean("road_signs_list_use_grid_layout", false);
    }
    // Set action icon
    menu.findItem(R.id.action__switch_layout)
        .setIcon(useGridLayout ? R.drawable.ic_list_view
            : R.drawable.ic_grid_view);

    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        NavUtils.navigateUpTo(this, new Intent(this, com.spiraclestudios.autoskola.presentation.ui.activities.RoadSignsCategoriesActivity.class));
        return true;
      case R.id.action__switch_layout:
        if (category.equals("SPEC")) return true;

        RoadSignsFragment roadSignsFragment =
            ((RoadSignsFragment) getSupportFragmentManager().findFragmentById(
                R.id.categories_fragment));
        boolean useGridLayout;
        if (roadSignsFragment.recycler_view.getLayoutManager() instanceof GridLayoutManager) {
          useGridLayout = false;
          item.setIcon(R.drawable.ic_grid_view);
        } else {
          useGridLayout = true;
          item.setIcon(R.drawable.ic_list_view);
        }
        roadSignsFragment.setLayoutMode(useGridLayout);

        SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putBoolean("road_signs_list_use_grid_layout", useGridLayout);
        prefsEdit.apply();

        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

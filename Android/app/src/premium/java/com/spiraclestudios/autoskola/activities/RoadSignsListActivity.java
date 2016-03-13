/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.fragments.RoadSignsListFragment;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

public class RoadSignsListActivity extends BaseActivity
        implements IBaseActivity {

    public String activityName = "RoadSignsListActivity";

    public String getActivityName() {
        return activityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_signs_list);

        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Show the Up button in the action bar.
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the list fragment and add it to the activity
            // using a fragment transaction.
            Intent intent = getIntent();
            Bundle arguments = new Bundle();

            String categoryName =
                    intent.getStringExtra(RoadSignsListFragment.ARG_CATEGORY_NAME);

            arguments.putString(RoadSignsListFragment.ARG_CATEGORY,
                    intent.getStringExtra(RoadSignsListFragment.ARG_CATEGORY));
            arguments.putString(RoadSignsListFragment.ARG_CATEGORY_NAME, categoryName);
            RoadSignsListFragment fragment = new RoadSignsListFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_fragment, fragment)
                    .commit();

            // Set Toolbar title
            toolbar.setTitle(categoryName);
        }

        Helper.initializeDebugDrawer(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_road_signs_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, RoadSignsCategoryListActivity.class));
                return true;
            case R.id.action_switch_layout:
                RecyclerView recycler_view = ((RoadSignsListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment)).recycler_view;
                RecyclerView.LayoutManager layoutManager;

                // Toggle layout managers
                if (recycler_view.getLayoutManager() instanceof GridLayoutManager) {
                    layoutManager = new LinearLayoutManager(this);
                } else {
                    layoutManager = new GridLayoutManager(this, 3);
                }
                recycler_view.setLayoutManager(layoutManager);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

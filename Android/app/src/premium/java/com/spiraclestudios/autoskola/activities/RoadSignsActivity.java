/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

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
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.fragments.RoadSignsFragment;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

public class RoadSignsActivity extends BaseActivity
        implements IBaseActivity {

    public static final String EXTRA_CATEGORY = "com.spiraclestudios.autoskola.ROAD_SIGN_CATEGORY";
    public static final String EXTRA_CATEGORY_NAME = "com.spiraclestudios.autoskola.ROAD_SIGN_CATEGORY_NAME";

    public String activityName = "RoadSignsActivity";

    private String category;
    private String categoryName;

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

            category = intent.getStringExtra(EXTRA_CATEGORY);
            categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);
            arguments.putString(RoadSignsFragment.ARG_CATEGORY, category);
            arguments.putString(RoadSignsFragment.ARG_CATEGORY_NAME, categoryName);
            RoadSignsFragment fragment = new RoadSignsFragment();
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

        // Switch to Grid layout if viewing the SPEC category.
        boolean useGridLayout;
        if (category.equals("SPEC")) {
            useGridLayout = true;
        } else {
            SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
            useGridLayout = prefs.getBoolean("road_signs_list_use_grid_layout", false);
        }
        // Set action icon
        menu.findItem(R.id.action_switch_layout)
                .setIcon(useGridLayout ? R.drawable.ic_view_list_white_24dp : R.drawable.ic_view_module_white_24dp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, RoadSignsCategoriesActivity.class));
                return true;
            case R.id.action_switch_layout:
                if (category.equals("SPEC"))
                    return true;

                RoadSignsFragment roadSignsFragment = ((RoadSignsFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment));
                boolean useGridLayout;
                if (roadSignsFragment.recycler_view.getLayoutManager() instanceof GridLayoutManager) {
                    useGridLayout = false;
                    item.setIcon(R.drawable.ic_view_module_white_24dp);
                } else {
                    useGridLayout = true;
                    item.setIcon(R.drawable.ic_view_list_white_24dp);
                }
                roadSignsFragment.setLayoutMode(useGridLayout);

                SharedPreferences prefs = getSharedPreferences(G.PREFS_GENERIC, MODE_PRIVATE);
                SharedPreferences.Editor prefsEdit = prefs.edit();
                prefsEdit.putBoolean("road_signs_list_use_grid_layout", useGridLayout).apply();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

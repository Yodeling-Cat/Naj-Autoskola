/*
 * Copyright 2015-2016 Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.fragments.RoadSignsDetailFragment;
import com.spiraclestudios.autoskola.fragments.RoadSignsListFragment;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

public class RoadSignsDetailActivity extends BaseActivity
        implements IBaseActivity {

    public String mActivityName = "RoadSignsDetailActivity";

    public String getActivityName() {
        return mActivityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_signs_detail);

        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getIntent().
                getStringExtra(RoadSignsListFragment.ARG_ROAD_SIGN_CATEGORY_NAME));

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
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Intent intent = getIntent();
            Bundle arguments = new Bundle();

            arguments.putString(RoadSignsDetailFragment.ARG_ROAD_SIGN_NAME,
                    intent.getStringExtra(RoadSignsDetailFragment.ARG_ROAD_SIGN_NAME));
            arguments.putString(RoadSignsDetailFragment.ARG_ROAD_SIGN_DESC,
                    intent.getStringExtra(RoadSignsDetailFragment.ARG_ROAD_SIGN_DESC));
            arguments.putString(RoadSignsDetailFragment.ARG_ROAD_SIGN_IMAGE_PATH,
                    intent.getStringExtra(RoadSignsDetailFragment.ARG_ROAD_SIGN_IMAGE_PATH));
            RoadSignsDetailFragment fragment = new RoadSignsDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        Helper.initializeDebugDrawer(this);
    }
}

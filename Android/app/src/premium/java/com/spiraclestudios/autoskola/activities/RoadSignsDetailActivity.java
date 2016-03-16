/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.fragments.RoadSignsDetailFragment;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

public class RoadSignsDetailActivity extends BaseActivity
        implements IBaseActivity {

    public static final String EXTRA_NAME = "com.spiraclestudios.autoskola.NAME";
    public static final String EXTRA_DESCRIPTION = "com.spiraclestudios.autoskola.DESCRIPTION";
    public static final String EXTRA_IMAGE_PATH = "com.spiraclestudios.autoskola.IMAGE_PATH";
    public static final String EXTRA_CATEGORY_NAME = "com.spiraclestudios.autoskola.CATEGORY_NAME";

    public String activityName = "RoadSignsDetailActivity";

    public String getActivityName() {
        return activityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_signs_detail);

        Intent intent = getIntent();

        // Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(intent.getStringExtra(EXTRA_CATEGORY_NAME));

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
            Bundle args = new Bundle();

            String name = intent.getStringExtra(EXTRA_NAME);
            String description = intent.getStringExtra(EXTRA_DESCRIPTION);
            String imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);

            args.putString(RoadSignsDetailFragment.ARG_NAME, name);
            args.putString(RoadSignsDetailFragment.ARG_DESCRIPTION, description);
            args.putString(RoadSignsDetailFragment.ARG_IMAGE_PATH, imagePath);

            // Create the detail fragment and add it to the activity.
            RoadSignsDetailFragment fragment = new RoadSignsDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        Helper.initializeDebugDrawer(this);
    }
}

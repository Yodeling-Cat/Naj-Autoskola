/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.presentation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.framework.presentation.ui.BaseActivity;
import com.spiraclestudios.autoskola.framework.presentation.ui.StandardActivity;
import com.spiraclestudios.autoskola.presentation.ui.fragments.RoadSignsDetailFragment;

public class RoadSignsDetailActivity extends StandardActivity {

  public static final String EXTRA_NAME = "com.spiraclestudios.autoskola.NAME";
  public static final String EXTRA_DESCRIPTION = "com.spiraclestudios.autoskola.DESCRIPTION";
  public static final String EXTRA_IMAGE_PATH = "com.spiraclestudios.autoskola.IMAGE_PATH";
  public static final String EXTRA_CATEGORY_NAME = "com.spiraclestudios.autoskola.CATEGORY_NAME";

  @Override protected BaseActivity getThis() {
    return this;
  }

  @Override public void setActivityContentView() {
    setContentView(R.layout.road_sign__activity);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();

    // Set up Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (toolbar != null) {
      toolbar.setTitle(intent.getStringExtra(EXTRA_CATEGORY_NAME));
    }

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
  }
}

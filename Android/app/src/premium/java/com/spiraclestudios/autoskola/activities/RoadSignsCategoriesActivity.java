/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.interfaces.IBaseActivity;

public class RoadSignsCategoriesActivity extends BaseActivity implements IBaseActivity {

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
   */
  private boolean mTwoPane;

  @Override protected void onCreate(Bundle savedInstanceState) {
    Helper.setTheme(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_road_signs_categories_list);

    // Set up Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(R.string.title_road_signs);

    // Set up Navigation Drawer
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

        /*if (findViewById(R.id.road_signs_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
      *//*((RoadSignsCategoriesFragment) getSupportFragmentManager()
          .findFragmentById(R.id.znacka_list))
                    .setActivateOnItemClick(true);*//*
        }*/

    // TODO: If exposing deep links into your app, handle intents here.
  }

  @Override public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
    if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
    }
  }

    /*@Override
    public void onItemSelected(String id) {
        Timber.d("onItemSelected id: " + id);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(RoadSignsDetailFragment.ARG_ITEM_ID, id);
            RoadSignsDetailFragment fragment = new RoadSignsDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.znacka_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, RoadSignsDetailActivity.class);
            detailIntent.putExtra(RoadSignsDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }*/
}

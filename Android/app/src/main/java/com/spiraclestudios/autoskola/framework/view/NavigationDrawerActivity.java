/*
 * Copyright 2017 Spiracle Software. All Rights Reserved.
 */

package com.spiraclestudios.autoskola.framework.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import butterknife.Bind;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.Helper;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.view.activities.FeedbackActivity;
import com.spiraclestudios.autoskola.view.activities.HistoryActivity;
import com.spiraclestudios.autoskola.view.activities.InformationActivity;
import com.spiraclestudios.autoskola.view.activities.RoadSignsActivity;
import com.spiraclestudios.autoskola.view.activities.SettingsActivity;
import com.spiraclestudios.autoskola.view.activities.TestActivity;

public abstract class NavigationDrawerActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Bind(R.id.drawer_layout) public DrawerLayout drawerLayout;
  @Bind(R.id.nav_view) public NavigationView navigationView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setUpNavigationDrawer();
  }

  public void setUpNavigationDrawer() {
    if (backActionInToolbar()) {
      // Add Up action.
      if (getSupportActionBar() != null) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }
    } else {
      // Add Hamburger menu.
      if (getSupportActionBar() != null) {
        getSupportActionBar().setHomeButtonEnabled(true);
      }

      ActionBarDrawerToggle toggle =
          new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
              R.string.navigation_drawer_close);
      drawerLayout.addDrawerListener(toggle);
      toggle.syncState();
    }

    // Make the drawer items clickable.
    navigationView.setNavigationItemSelectedListener(this);
  }

  public boolean backActionInToolbar() {
    return false;
  }

  @Override public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();
    Intent intent;

    if (id == R.id.nav__tests) {
      if (this.getClass().equals(TestActivity.class)) return true;

      intent = new Intent(this, TestActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
    } else if (id == R.id.nav__history) {
      if (this.getClass().equals(HistoryActivity.class)) return true;

      intent = new Intent(this, HistoryActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
    } else if (id == R.id.nav__road_signs) {
      if (BuildConfig.PREMIUM) {
        if (this.getClass().equals(RoadSignsActivity.class)) return true;

        intent = new Intent(this, RoadSignsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      } else {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_premium_feature_title)
            .setMessage(R.string.dialog_premium_feature_message)
            .setPositiveButton(R.string.dialog_premium_feature_positive,
                new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    try {
                      startActivity(new Intent(Intent.ACTION_VIEW,
                          Uri.parse(Helper.googlePlayPremiumMarketURL)));
                    } catch (android.content.ActivityNotFoundException e) {
                      startActivity(
                          new Intent(Intent.ACTION_VIEW, Uri.parse(Helper.googlePlayPremiumURL)));
                    }
                  }
                })
            .setNegativeButton(R.string.dialog_premium_feature_negative,
                new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                  }
                });
        builder.create().show();
        return true;
      }
    } else if (id == R.id.nav__settings) {
      if (this.getClass().equals(SettingsActivity.class)) return true;

      intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav__information) {
      if (this.getClass().equals(InformationActivity.class)) return true;

      Bundle bundle = new Bundle();
      firebaseAnalytics.logEvent("nav_about", bundle);

      intent = new Intent(this, InformationActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav__feedback) {
      if (this.getClass().equals(FeedbackActivity.class)) return true;

      intent = new Intent(this, FeedbackActivity.class);
      startActivity(intent);
    }

    if (drawerLayout != null) {
      drawerLayout.closeDrawer(GravityCompat.START);
    }
    return true;
  }

  @Override public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }
}

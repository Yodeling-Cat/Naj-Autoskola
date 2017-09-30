// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.framework.presentation.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.spiraclestudios.autoskola.BuildConfig;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.Utils;
import com.spiraclestudios.autoskola.platform.ScreenFlowController;
import com.spiraclestudios.autoskola.presentation.ui.activities.FeedbackActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.HistoryActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.HomeActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.InformationActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.RoadSignsCategoriesActivity;
import com.spiraclestudios.autoskola.presentation.ui.activities.SettingsActivity;

public abstract class NavigationDrawerActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @BindView(R.id.drawer_layout) public DrawerLayout drawerLayout;
  @BindView(R.id.nav_view) public NavigationView navigationView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setUpNavigationDrawer();
  }

  public boolean backActionInToolbar() {
    return false;
  }

  public void setUpNavigationDrawer() {
    navigationView.setNavigationItemSelectedListener(this);

    if (backActionInToolbar()) {
      // Add Up action
      if (getSupportActionBar() != null) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }

      drawerLayout.addDrawerListener(new SimpleDrawerListener() {
        @Override public void onDrawerOpened(View drawerView) {
          super.onDrawerOpened(drawerView);
          Utils.hideSoftKeyboard(NavigationDrawerActivity.this);
        }
      });
    } else {
      // Add Hamburger menu
      if (getSupportActionBar() != null) {
        getSupportActionBar().setHomeButtonEnabled(true);
      }

      ActionBarDrawerToggle toggle =
          new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.content_desc__open_nav_drawer,
              R.string.content_desc__close_nav_drawer) {

            @Override public void onDrawerOpened(View drawerView) {
              super.onDrawerOpened(drawerView);
              Utils.hideSoftKeyboard(NavigationDrawerActivity.this);
            }
          };

      drawerLayout.addDrawerListener(toggle);
      toggle.syncState();
    }
  }

  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    Intent intent;

    if (id == R.id.nav__home) {
      if (this.getClass().equals(HomeActivity.class)) return closeDrawerAndReturn();
      ScreenFlowController.showHomeActivity(this);
    } else if (id == R.id.nav__history) {
      if (this.getClass().equals(HistoryActivity.class)) return closeDrawerAndReturn();
      ScreenFlowController.showHistoryActivity(this);
    } else if (id == R.id.nav__road_signs) {
      if (BuildConfig.PREMIUM) {
        if (this.getClass().equals(RoadSignsCategoriesActivity.class)) {
          return closeDrawerAndReturn();
        }

        intent = new Intent(this, RoadSignsCategoriesActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      } else {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.premium_feature__title)
            .setMessage(R.string.premium_feature__text__requires_full_version_of_app)
            .setPositiveButton(R.string.premium_feature__action__get_premium,
                new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    try {
                      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                          getString(R.string.link__app__google_play__launch_store_premium))));
                    } catch (android.content.ActivityNotFoundException e) {
                      startActivity(new Intent(Intent.ACTION_VIEW,
                          Uri.parse(getString(R.string.link__app__google_play_premium))));
                    }
                  }
                })
            .setNegativeButton(R.string.premium_feature__action__not_now,
                new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                  }
                });
        builder.create().show();
        return true;
      }
    } else if (id == R.id.nav__settings) {
      if (this.getClass().equals(SettingsActivity.class)) return closeDrawerAndReturn();

      intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav__information) {
      if (this.getClass().equals(InformationActivity.class)) return closeDrawerAndReturn();

      Bundle bundle = new Bundle();
      firebaseAnalytics.logEvent("nav_about", bundle);

      intent = new Intent(this, InformationActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav__feedback) {
      if (this.getClass().equals(FeedbackActivity.class)) return closeDrawerAndReturn();

      intent = new Intent(this, FeedbackActivity.class);
      startActivity(intent);
    }

    return closeDrawerAndReturn();
  }

  private boolean closeDrawerAndReturn() {
    drawerLayout.closeDrawer(GravityCompat.START);
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

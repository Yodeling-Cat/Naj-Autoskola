// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 14/10/2015.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import com.spiraclestudios.autoskola.domain.Groups;
import com.spiraclestudios.autoskola.presentation.ui.fragments.MainActivityFragment;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

  private AppCompatActivity activity;
  int mNumOfTabs;

  public MainActivityPagerAdapter(AppCompatActivity activity, int numOfTabs) {
    super(activity.getSupportFragmentManager());
    this.activity = activity;
    mNumOfTabs = numOfTabs;
  }

  @Override public Fragment getItem(int position) {
    boolean isCDTMainGroup = activity.getApplicationContext()
        .getSharedPreferences(G.PREFS_SETTINGS, Context.MODE_PRIVATE)
        .getBoolean("cdt_main_group", false);

    switch (position) {
      case 0:
        return MainActivityFragment.newInstance(isCDTMainGroup ? Groups.CDT : Groups.AB);
      case 1:
        return MainActivityFragment.newInstance(isCDTMainGroup ? Groups.AB : Groups.CDT);
      default:
        return null;
    }
  }

  @Override public int getCount() {
    return mNumOfTabs;
  }
}
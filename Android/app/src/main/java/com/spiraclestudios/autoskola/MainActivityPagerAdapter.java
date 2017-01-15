/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 14/10/2015.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.spiraclestudios.autoskola.view.fragments.MainActivityFragment;

public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

  int mNumOfTabs;

  public MainActivityPagerAdapter(FragmentManager fm, int numOfTabs) {
    super(fm);
    mNumOfTabs = numOfTabs;
  }

  @Override public Fragment getItem(int position) {
    boolean isCDTMainGroup = Helper.getApplicationContext()
        .getSharedPreferences(G.PREFS_SETTINGS, Context.MODE_PRIVATE)
        .getBoolean("cdt_main_group", false);

    switch (position) {
      case 0:
        return MainActivityFragment.newInstance(
            isCDTMainGroup ? Helper.Groups.CDT : Helper.Groups.AB);
      case 1:
        return MainActivityFragment.newInstance(
            isCDTMainGroup ? Helper.Groups.AB : Helper.Groups.CDT);
      default:
        return null;
    }
  }

  @Override public int getCount() {
    return mNumOfTabs;
  }
}
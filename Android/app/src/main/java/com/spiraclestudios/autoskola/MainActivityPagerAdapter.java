/*
 * Copyright (c) 2015-2016. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Original created by benji on 14/10/2015.
 */

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.spiraclestudios.autoskola.fragments.MainActivityFragment;

public class MainActivityPagerAdapter extends FragmentStatePagerAdapter
{

  int mNumOfTabs;

  public MainActivityPagerAdapter(FragmentManager fm, int numOfTabs)
  {

    super(fm);
    mNumOfTabs = numOfTabs;
  }

  @Override
  public Fragment getItem(int position)
  {

    boolean isCDTMainGroup = PreferenceManager.getDefaultSharedPreferences(Helper.getApplicationContext())
        .getBoolean("cdt_main_group", false);

    switch (position) {
      case 0:
        return isCDTMainGroup ? MainActivityFragment.newInstance(Helper.Groups.CDT) : MainActivityFragment.newInstance(Helper.Groups.AB);
      case 1:
        return isCDTMainGroup ? MainActivityFragment.newInstance(Helper.Groups.AB) : MainActivityFragment.newInstance(Helper.Groups.CDT);
      default:
        return null;
    }
  }

  @Override
  public int getCount()
  {

    return mNumOfTabs;
  }
}
// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.spiraclestudios.autoskola.presentation.ui.fragments.TestFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Added by benji on 14/10/2015.
 */
public class TestPagerAdapter extends FragmentStatePagerAdapter {

  private List<TestFragment> fragments = new ArrayList<>();
  private int numOfTabs;

  public TestPagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
    super(fragmentManager);
    this.numOfTabs = numOfTabs;
  }

  @Override public Fragment getItem(int position) {
    return TestFragment.newInstance(position);
  }

  @Override public int getCount() {
    return numOfTabs;
  }

  public void addTestPagerListener(TestFragment fragment) {
    if (!fragments.contains(fragment)) {
      fragments.add(fragment);
    }
  }

  public void removeTestPagerListener(TestFragment fragment) {
    if (fragments.contains(fragment)) {
      fragments.remove(fragment);
    }
  }

  public void onTestCompleted() {
    for (TestFragment fragment : fragments) {
      fragment.onTestCompleted();
    }
  }

  public void scrollToTop() {
    for (TestFragment fragment : fragments) {
      fragment.scrollToTop();
    }
  }
}